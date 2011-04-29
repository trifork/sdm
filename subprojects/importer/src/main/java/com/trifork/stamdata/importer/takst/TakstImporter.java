// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
//
// Contributor(s): Contributors are attributed in the source code
// where applicable.
//
// The Original Code is "Stamdata".
//
// The Initial Developer of the Original Code is Trifork Public A/S.
//
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
//
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.importer.takst;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.config.MySQLConnectionManager;
import com.trifork.stamdata.dao.AuditingPersister;
import com.trifork.stamdata.importer.FileImporterControlledIntervals;
import com.trifork.stamdata.importer.exceptions.FileImporterException;
import com.trifork.stamdata.importer.exceptions.FilePersistException;
import com.trifork.stamdata.importer.takst.model.Takst;


public class TakstImporter implements FileImporterControlledIntervals {

    public static final String[] requiredFileNames = new String[]{"system.txt", "lms01.txt", "lms02.txt",
            "lms03.txt", "lms04.txt", "lms05.txt", "lms07.txt", "lms09.txt", "lms10.txt", "lms11.txt", "lms12.txt",
            "lms13.txt", "lms14.txt", "lms15.txt", "lms16.txt", "lms17.txt", "lms18.txt", "lms19.txt", "lms20.txt",
            "lms23.txt", "lms24.txt", "lms25.txt", "lms26.txt", "lms27.txt", "lms28.txt"};
    // final String[] optionalFileNames = new String[] {"lms32.txt", "lms21.txt",
    // "lms22.txt","lms29.txt","lms30.txt","lms31.txt"};

    private static final DateTimeFormatter weekFormatter = DateTimeFormat.forPattern("xxxxww").withLocale(new Locale("da", "DK"));

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void run(List<File> files) throws FileImporterException {
        Takst takst;
        logger.debug("Starting to parse takst");
        TakstParser tp = new TakstParser();
        takst = tp.parseFiles(files);
        logger.debug("Takst parsed");
        Connection con = null;
        try {
            logger.debug("Starting to import takst into database");
            con = MySQLConnectionManager.getConnection();
            AuditingPersister versionedDao = new AuditingPersister(con);
            versionedDao.persistCompleteDataset(takst.getDatasets());
            logger.debug("Done importing takst into database");
            try {
                con.commit();
            } catch (SQLException e) {
                throw new FileImporterException("could not commit transaction", e);
            }

        } catch (Exception e) {
            logger.error("An error occured while persisting the takst to database " + e.getMessage(), e);
            throw new FilePersistException("An error occured while persisting the takst to database: " + e.getMessage(), e);
        } finally {
            MySQLConnectionManager.close(con);
        }

    }

    public boolean checkRequiredFiles(List<File> files) {
        logger.debug("Checking takst file list for presence of all required files");
        Map<String, File> fileMap = new HashMap<String, File>(files.size());
        for (File f : files)
            fileMap.put(f.getName(), f);

        for (String reqFile : Arrays.asList(requiredFileNames)) {
            if (!fileMap.containsKey(reqFile)) {
                logger.debug("Did not find required file: " + reqFile);
                return false;
            }
            logger.debug("Found required file: " + reqFile);
        }
        return true;
    }

    @Override
    /**
     * Der findes to typer takster: Ordinære takster og "indimellem" takster.
     * Ordinære takster skal komme hver 14. dag.
     * "Indimellem" takster kommer ad hoc, og vi kan ikke sætte forventning op til dem.
     */
    public Calendar getNextImportExpectedBefore(Calendar lastImport) {

        Connection con = null;
        Statement stmt = null;
        Calendar ordinaryTakst = null;
        try {
            con = MySQLConnectionManager.getAutoCommitConnection();
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT TakstUge FROM TakstVersion WHERE validFrom IN (select MAX(validFrom) FROM TakstVersion)");
            if (rs.next()) {
		String lastWeek = rs.getString(1);

		ordinaryTakst = weekFormatter.parseDateTime(lastWeek).toGregorianCalendar();

		ordinaryTakst.add(Calendar.DATE, 14); // Next ordinary 'takst' expected 14 days after
		ordinaryTakst.add(Calendar.HOUR, -36); // We want the ordinary 'takst' to be imported 36 hours before it is suppose to be in effect
            }
        } catch (Exception e) {
            logger.error("Cannot get last TakstVersion from database. Could be that no 'takst' have been imported", e);
        }
        finally {
            MySQLConnectionManager.close(stmt, con);
        }

        if (ordinaryTakst == null) {
		// Something failed. Raise an alarm by setting the expected next import to past time
		ordinaryTakst = Calendar.getInstance();
		ordinaryTakst.add(Calendar.HOUR, -1);
        }
        return ordinaryTakst;
    }

}
