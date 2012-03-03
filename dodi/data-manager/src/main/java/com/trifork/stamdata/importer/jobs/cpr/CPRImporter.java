/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */


package com.trifork.stamdata.importer.jobs.cpr;

import static com.trifork.stamdata.importer.tools.SLALoggerHolder.getSLALogger;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.trifork.stamdata.Entities;
import com.trifork.stamdata.Preconditions;
import com.trifork.stamdata.importer.config.Configuration;
import com.trifork.stamdata.importer.config.KeyValueStore;
import com.trifork.stamdata.importer.jobs.FileParser;
import com.trifork.stamdata.importer.jobs.cpr.models.Klarskriftadresse;
import com.trifork.stamdata.importer.jobs.cpr.models.NavneBeskyttelse;
import com.trifork.stamdata.importer.jobs.cpr.models.Navneoplysninger;
import com.trifork.stamdata.importer.jobs.cpr.models.Personoplysninger;
import com.trifork.stamdata.importer.persistence.Dataset;
import com.trifork.stamdata.importer.persistence.Persister;
import com.trifork.stamdata.importer.util.Dates;
import com.trifork.stamdata.models.TemporalEntity;
import dk.sdsd.nsp.slalog.api.SLALogItem;
import dk.sdsd.nsp.slalog.api.SLALogger;
import org.apache.log4j.Logger;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.regex.Pattern;

import static com.trifork.stamdata.Preconditions.checkNotNull;


public class CPRImporter implements FileParser {
    private static final Logger logger = Logger.getLogger(CPRImporter.class);

    private final Pattern personFilePattern;
    private final Pattern personFileDeltaPattern;

    public CPRImporter() {
        personFilePattern = Pattern.compile(Configuration.getString("spooler.cpr.file.pattern.person"));
        personFileDeltaPattern = Pattern.compile(Configuration.getString("spooler.cpr.file.pattern.person.delta"));
    }

    @Override
    public String identifier() {
        return "cpr";
    }

    @Override
    public boolean validateInputStructure(File[] input) {
        return true; // TODO: Check if the required files are there.
    }

    @Override
    public void parse(File[] input, Persister persister, KeyValueStore keyValueStore) throws Exception {
        checkNotNull(input);
        checkNotNull(persister);

        // TODO: Should this not be done in #validateInputStructure()

        for (File personFile : input) {
            if (!isPersonFile(personFile)) {
                throw new Exception("File " + personFile.getAbsolutePath() + " is not a valid CPR file. Nothing will be imported from the fileset.");
            }
        }

        SLALogItem slaLogItem = getSLALogger().createLogItem("CPRImport", "All Files");
        try {

            // Check that the sequence is kept.

            Connection connection = persister.getConnection();
            ArrayList<String> cprWithChanges = Lists.newArrayList();

            for (File personFile : input) {
                SLALogItem slaLogItemFile = getSLALogger().createLogItem("CPRImport of file", personFile.getName());
                logger.info("Started parsing CPR file. file=" + personFile.getAbsolutePath());

                CPRDataset changes = CPRParser.parse(personFile);

                if (isDeltaFile(personFile)) {
                    Date previousVersion = getLatestVersion(connection);

                    if (previousVersion == null) {
                        logger.warn("Didn't find any previous versions of CPR. Asuming an initial import and skipping sequence checks.");
                    }
                }

                // All these entities affect the person table.

                Set<Class<?>> classesObservedByPVIT = Sets.newHashSet();
                classesObservedByPVIT.add(Navneoplysninger.class);
                classesObservedByPVIT.add(Klarskriftadresse.class);
                classesObservedByPVIT.add(Personoplysninger.class);
                classesObservedByPVIT.add(NavneBeskyttelse.class);

                for (Dataset<? extends TemporalEntity> dataset : changes.getDatasets()) {
                    persister.persistDeltaDataset(dataset);

                    // The GOS/CPR component requires a table with
                    // a combination columns (CPR, ModifiedDate).
                    //
                    // The key for all CPR entities is the CPR and thus
                    // we can safely case the key value.

                    for (TemporalEntity record : dataset.getEntities()) {
                        if (!classesObservedByPVIT.contains(record.getClass())) continue;

                        // It does not matter that the same CPR is added
                        // multiple times. It is a set and only contains
                        // distinct entries.

                        String cpr = Entities.getEntityID(record).toString();

                        try {
                            Preconditions.checkState(cpr.length() == 10, "Invalid key used in the CPR parser. Only CPR keys are supported.");
                        } catch (Exception e) {
                            slaLogItemFile.setCallResultError("CPR Import failed importing file " + personFile.getName() + " - Cause: " + e.getMessage());
                            slaLogItemFile.store();

                            throw e;
                        }

                        cprWithChanges.add(cpr);
                    }
                }

                // Add latest 'version' date to database if we are not importing
                // a full set.

                if (isDeltaFile(personFile)) {
                    insertVersion(changes.getValidFrom(), connection);
                }

                slaLogItemFile.setCallResultOk();
                slaLogItemFile.store();
            }

            // Update the GOS/CPR table with the current time stamp and
            // CPR numbers.

            PreparedStatement updateChangesTable = persister.getConnection().prepareStatement("REPLACE INTO ChangesToCPR (CPR, ModifiedDate) VALUES (?,?)");
            Date modifiedDate = new Date();

            for (String cpr : cprWithChanges) {
                updateChangesTable.setObject(1, cpr);
                updateChangesTable.setObject(2, modifiedDate);
                updateChangesTable.executeUpdate();
            }

            updateChangesTable.close();
            slaLogItem.setCallResultOk();
            slaLogItem.store();

        } catch (Exception e) {
            slaLogItem.setCallResultError("CPR Import failed - Cause: " + e.getMessage());
            slaLogItem.store();

            throw e;
        }
    }

    private boolean isPersonFile(File f) {
        return personFilePattern.matcher(f.getName()).matches();
    }

    private boolean isDeltaFile(File f) {
        return personFileDeltaPattern.matcher(f.getName()).matches();
    }

    static public Date getLatestVersion(Connection con) throws SQLException {
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery("SELECT MAX(IkraftDato) AS Ikraft FROM PersonIkraft");

        if (rs.next()) return rs.getTimestamp("Ikraft");

        // Returns null if no previous version of CPR has been imported.

        return null;
    }

    void insertVersion(Date calendar, Connection con) throws SQLException {
        Statement stm = con.createStatement();
        String query = "INSERT INTO PersonIkraft (IkraftDato) VALUES ('" + Dates.toSqlDate(calendar) + "');";
        stm.execute(query);
    }

    @Override
    public String name() {
        return "CPR Parser";
    }
}
