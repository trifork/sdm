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

package com.trifork.stamdata.importer.jobs.takst;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.importer.config.ConnectionManager;
import com.trifork.stamdata.importer.persistence.AuditingPersister;
import com.trifork.stamdata.importer.util.Dates;

/**
 * Integration test of the database access layer. Tests that a dataset can be written to the
 * database
 * 
 * @author Anders Bo Christensen
 * 
 */
public class DataLayerIntegrationTest
{
    private Connection connection;
    private AuditingPersister persister;
    private Statement statement;

    @Before
    public void setUp() throws Exception
    {
        connection = new ConnectionManager().getAutoCommitConnection();

        statement = connection.createStatement();
        statement.execute("truncate table TakstVersion");
        statement.execute("truncate table Laegemiddel");
        statement.execute("truncate table Pakning");
        statement.execute("truncate table Administrationsvej");
        statement.execute("truncate table ATC");
        statement.execute("truncate table IndikationATCRef");
        statement.execute("truncate table Indikation");
        statement.execute("truncate table LaegemiddelDoseringRef");
        statement.execute("truncate table Klausulering");
        statement.execute("truncate table Medicintilskud");
        statement.execute("truncate table Dosering");
        statement.execute("truncate table Formbetegnelse");
        statement.execute("truncate table Tidsenhed");
        statement.execute("truncate table Pakningsstoerrelsesenhed");
        statement.execute("truncate table Styrkeenhed");
        statement.execute("truncate table LaegemiddelAdministrationsvejRef");
        statement.execute("truncate table Beregningsregler");
        statement.execute("truncate table EmballagetypeKoder");
        statement.execute("truncate table Enhedspriser");
        statement.execute("truncate table Indholdsstoffer");
        statement.execute("truncate table Laegemiddelnavn");
        statement.execute("truncate table Opbevaringsbetingelser");
        statement.execute("truncate table OplysningerOmDosisdispensering");
        statement.execute("truncate table Pakningskombinationer");
        statement.execute("truncate table PakningskombinationerUdenPriser");
        statement.execute("truncate table Priser");
        statement.execute("truncate table Rekommandationer");
        statement.execute("truncate table SpecialeForNBS");
        statement.execute("truncate table Substitution");
        statement.execute("truncate table SubstitutionAfLaegemidlerUdenFastPris");
        statement.execute("truncate table Tilskudsintervaller");
        statement.execute("truncate table TilskudsprisgrupperPakningsniveau");
        statement.execute("truncate table UdgaaedeNavne");
        statement.execute("truncate table Udleveringsbestemmelser");
        statement.execute("truncate table Firma");

        // We don't want an auto commit connection for the tests.

        connection.setAutoCommit(false);

        persister = new AuditingPersister(connection);
    }

    @After
    public void tearDown() throws Exception
    {
        connection.commit();
        connection.close();
    }

    @Test
    public void simpleImportOfInitilDataset() throws Exception
    {
        Takst takst = parse("data/takst/initial");

        persister.persistCompleteDataset(takst.getDatasets());

        assertThat(count("Laegemiddel"), is(100));
    }

    @Test
    public void updateANameShouldResultInTheOldRecordBeingInvalidated() throws Exception
    {
        Takst initialDataset = parse("data/takst/initial");
        persister.persistCompleteDataset(initialDataset.getDatasets());
        connection.commit();

        Takst updateDataset = parse("data/takst/update");
        persister.persistCompleteDataset(updateDataset.getDatasets());
        connection.commit();

        int numOfRecords = 100;
        int numOfChangesToExisting = 1;

        // The overwritten record should be kept but have its validity period
        // set to validTo = validFrom.

        assertThat(count("Laegemiddel"), is(numOfRecords + numOfChangesToExisting));

        // The update changes the name 'Kemadrin' to 'Kemadron'.

        ResultSet rs = statement.executeQuery("SELECT * FROM Laegemiddel WHERE DrugName LIKE 'Kemadrin' AND DrugId = 28100009555");
        rs.next();
        assertThat(rs.getTimestamp("ValidTo").getTime(), is(new DateTime(2009, 7, 30, 0, 0).getMillis()));

        rs = statement.executeQuery("SELECT * FROM Laegemiddel WHERE DrugName LIKE 'Kemadron' AND DrugId = 28100009555");
        rs.next();
        assertThat(rs.getTimestamp("ValidTo").getTime(), is(Dates.THE_END_OF_TIME.getTime()));
    }

    @Test
    public void ifARecordIsMissingInANewDatasetTheCoresponsingRecordFromAnyExistingDatasetShouldBeInvalidated() throws Exception
    {
        Takst initDataset = parse("data/takst/initial");
        persister.persistCompleteDataset(initDataset.getDatasets());

        Takst updatedDataset = parse("data/takst/delete");
        persister.persistCompleteDataset(updatedDataset.getDatasets());

        assertThat(count("Laegemiddel"), is(100));

        ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM Laegemiddel WHERE DrugId = 28100009555");

        rs.next();

        assertThat(rs.getTimestamp("ValidTo").getTime(), is(new DateTime(2009, 7, 31, 0, 0).getMillis()));
    }

    @Test
    public void canImportUdgaaedeNavneSubsetWhereEntriesHaveDifferentLetterCase() throws Exception
    {
        Takst takst = parse("data/takst/udgaaedeNavneTakst");

        persister.persistCompleteDataset(takst.getDatasets());

        connection.commit();

        assertThat(count("UdgaaedeNavne"), is(3));
    }

    @Test
    public void canImportACompleteDatasetWithAllDataTypes() throws Exception
    {
        Takst takst = parse("data/takst/realtakst");

        persister.persistCompleteDataset(takst.getDatasets());

        connection.commit();

        // See these numbers in the system.txt file.

        assertThat(count("Laegemiddel"), is(5492));
        assertThat(count("Pakning"), is(8809));

        // Udgaaede navne is a bit special. Since the keys we are able to
        // construct from the line entries might create duplicates, we might
        // not persist all entries. This is a problem stamdata solves itself
        // (by keeping track of historical data). Removal of UdgaaedeNavne (LMS10)
        // should be considered.

        int totalUdgaaedeNavnRecords = 2547;
        int numDublicateEntriesOnSameDay = 7;

        assertThat(count("UdgaaedeNavne"), is(totalUdgaaedeNavnRecords - numDublicateEntriesOnSameDay));
    }

    //
    // Helpers
    //

    public int count(String tableName) throws SQLException
    {
        ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM " + tableName);
        rs.next();

        int count = rs.getInt(1);

        return count;
    }

    private Takst parse(String dir) throws Exception
    {
        File file = FileUtils.toFile(getClass().getClassLoader().getResource(dir));
        TakstParser parser = new TakstParser();
        Takst takst = parser.parseFiles(file.listFiles());

        return takst;
    }
}
