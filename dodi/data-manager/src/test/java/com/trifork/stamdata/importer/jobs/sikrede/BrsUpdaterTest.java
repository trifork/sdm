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
package com.trifork.stamdata.importer.jobs.sikrede;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.importer.config.ConnectionManager;
import com.trifork.stamdata.persistence.Record;
import com.trifork.stamdata.persistence.RecordBuilder;
import com.trifork.stamdata.specs.SikredeRecordSpecs;

public class BrsUpdaterTest {

    private static final DateTime ASSIGNED_FROM = new DateTime(2011, 10, 10, 0, 0);
    private static final DateTime ASSIGNED_TO = new DateTime(2011, 11, 10, 0, 0);
    
    private String examplePatientCpr;
    private String exampleDoctorOrganisationIdentifier;
    
    private Connection connection;
    private BrsUpdater brsUpdater;

    @Before
    public void setup() throws SQLException
    {
        examplePatientCpr = "1234567890123456789012345678901234567890";
        exampleDoctorOrganisationIdentifier = "123456";

        connection = new ConnectionManager().getConnection();
        connection.createStatement().executeUpdate("TRUNCATE TABLE AssignedDoctor");
        this.brsUpdater = new BrsUpdater(connection);
    }
    
    @After
    public void tearDown() throws SQLException
    {
        connection.close();
    }
    
    @Test
    public void testHashCpr()
    {
        String hashedCpr = BrsUpdater.hashCpr("1234567890");
        assertEquals(40, hashedCpr.length());
    }
    
    @Test
    public void testParseSikredeRecordDate()
    {
        DateTime parsedDate = BrsUpdater.parseSikredeRecordDate("20110507");
        DateTime expectedDate = new DateTime(2011, 5, 7, 0, 0, 0);
        assertEquals(expectedDate, parsedDate);
    }
    
    @Test
    public void testOpenRelationshipExistsWhenNoRecords() throws SQLException
    {
        assertEquals(BrsUpdater.NO_EXISTING_RELATIONSHIP, brsUpdater.openRelationshipExists(examplePatientCpr, exampleDoctorOrganisationIdentifier));
    }
    
    @Test
    public void testOpenRelationshipExistsWhenRecordWhichHasOtherDoctorOrganisationIdExists() throws SQLException
    {
        brsUpdater.insertRelationship(examplePatientCpr, "654321", ASSIGNED_FROM, null);
        assertEquals(BrsUpdater.NO_EXISTING_RELATIONSHIP, brsUpdater.openRelationshipExists(examplePatientCpr, exampleDoctorOrganisationIdentifier));
    }
    
    @Test
    public void testOpenRelationshipExistsWhenRecordThatIsClosedIsFoundInDatabase() throws SQLException
    {
        brsUpdater.insertRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM, ASSIGNED_TO);
        assertEquals(BrsUpdater.NO_EXISTING_RELATIONSHIP, brsUpdater.openRelationshipExists(examplePatientCpr, exampleDoctorOrganisationIdentifier));
    }
    
    @Test
    public void testOpenRelationshipExistsWhenMatchingRecordInDatabase() throws SQLException
    {
        brsUpdater.insertRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM, null);
        assertTrue(BrsUpdater.NO_EXISTING_RELATIONSHIP != brsUpdater.openRelationshipExists(examplePatientCpr, exampleDoctorOrganisationIdentifier));
    }
    
    @Test
    public void testCloseRelationship() throws SQLException
    {
        brsUpdater.insertRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM, null);
        long primaryKeyOfOpenRelationship = brsUpdater.openRelationshipExists(examplePatientCpr, exampleDoctorOrganisationIdentifier);
        brsUpdater.closeRelationship(primaryKeyOfOpenRelationship, ASSIGNED_TO);
        assertClosedRelationship(primaryKeyOfOpenRelationship);
    }

	@Test
    public void testInsertOrUpdateHistoricalRelationshipWhenDoctorDoesNotExist() throws SQLException
    {
        brsUpdater.insertOrUpdateHistoricalRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM, ASSIGNED_TO);
        assertClosedRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier);
    }

    @Test
    public void testInsertOrUpdateHistoricalRelationshipWhenDoctorExistWithOpenRelationship() throws SQLException
    {
        brsUpdater.insertRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM, null);
        assertRecordExists(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM);
        brsUpdater.insertOrUpdateHistoricalRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM.minusDays(1), ASSIGNED_TO);
        assertRecordExists(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM, ASSIGNED_TO);
    }
    
    @Test
    public void testInsertOrUpdateHistoricalRelationshipWhenDoctorExistWithClosedRelationship() throws SQLException
    {
        brsUpdater.insertRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM, ASSIGNED_TO);
        assertRecordExists(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM, ASSIGNED_TO);
        brsUpdater.insertOrUpdateHistoricalRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_TO.plusYears(2), ASSIGNED_TO.plusYears(3));
        assertRecordExists(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM, ASSIGNED_TO);
        assertRecordExists(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_TO.plusYears(2), ASSIGNED_TO.plusYears(3));
    }
    
    // NSPSUPPORT-95 - citizens switching doctor several times between updates caused the importer
    //                 to mess up the data - we ended with > 1 open record. This test checks that this
    //                 will not happen in the future
    @Test
    public void testHistoryMismatch() throws SQLException {
    	DateTime DATE_1 = new DateTime(2012, 11, 30, 0, 0);
    	String date_1_string = "20121130";
    	String date_2_string = "20120930";
    	String date_3_string = "20120730";
    	String date_4_string = "20120530";
    	String cpr = "1111111118";
    	String yder1 = "123456";
    	String yder2 = "234567";
    	String yder3 = "345678";

    	RecordBuilder builder1 = new RecordBuilder(SikredeRecordSpecs.ENTRY_RECORD_SPEC);
    	builder1.addDummyFieldsAndBuild(); // fill up fields that are irrelevant for this test
    	builder1.field("CPRnr", cpr);
    	builder1.field("SYdernr", yder2);
    	builder1.field("SYdernrGl", yder1);
    	builder1.field("SIkraftDatoYder", date_3_string);
    	builder1.field("SIkraftDatoYderGl", date_4_string);
    	Record record1 = builder1.build();
    	
    	RecordBuilder builder2 = new RecordBuilder(SikredeRecordSpecs.ENTRY_RECORD_SPEC);
    	builder2.addDummyFieldsAndBuild(); // fill up fields that are irrelevant for this test
    	builder2.field("CPRnr", cpr);
    	builder2.field("SYdernr", yder1);
    	builder2.field("SYdernrGl", yder3);
    	builder2.field("SIkraftDatoYder", date_1_string);
    	builder2.field("SIkraftDatoYderGl", date_2_string);
    	Record record2 = builder2.build();

    	brsUpdater.updateRecord(record1);
    	brsUpdater.updateRecord(record2);

    	assertClosedRelationship("AF3F5EC3C82E368A231444633154F7A5A1340085", yder1);
    	assertClosedRelationship("AF3F5EC3C82E368A231444633154F7A5A1340085", yder2);
    	assertClosedRelationship("AF3F5EC3C82E368A231444633154F7A5A1340085", yder3);
    	assertRecordExists("AF3F5EC3C82E368A231444633154F7A5A1340085", yder1, DATE_1);
    }
    
    ////////////////////////
    
    private void assertRecordExists(String patientCpr, String doctorOrganisationIdentifier, DateTime assignedFrom, DateTime assignedTo) throws SQLException
    {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM AssignedDoctor WHERE patientCpr = ? AND doctorOrganisationIdentifier = ? AND assignedFrom = ? AND assignedTo = ?");
        preparedStatement.setString(1, patientCpr);
        preparedStatement.setString(2, doctorOrganisationIdentifier);
        preparedStatement.setDate(3, new Date(assignedFrom.getMillis()));
        preparedStatement.setDate(4, new Date(assignedTo.getMillis()));
        ResultSet resultSet = preparedStatement.executeQuery();
        assertTrue(resultSet.next());
    }
    
    private void assertRecordExists(String patientCpr, String doctorOrganisationIdentifier, DateTime assignedFrom) throws SQLException
    {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM AssignedDoctor WHERE patientCpr = ? AND doctorOrganisationIdentifier = ? AND assignedFrom = ? AND assignedTo IS NULL");
        preparedStatement.setString(1, patientCpr);
        preparedStatement.setString(2, doctorOrganisationIdentifier);
        preparedStatement.setDate(3, new Date(assignedFrom.getMillis()));
        ResultSet resultSet = preparedStatement.executeQuery();
        assertTrue(resultSet.next());
    }
    
    private void assertClosedRelationship(long pk) throws SQLException
    {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM AssignedDoctor WHERE pk = ? AND assignedTo IS NOT NULL");
        preparedStatement.setLong(1, pk);
        ResultSet resultSet = preparedStatement.executeQuery();
        assertTrue(resultSet.next());
    }
    
    private void assertClosedRelationship(String patientCpr, String doctorOrganisationIdentifier) throws SQLException
    {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM AssignedDoctor WHERE patientCpr = ? AND doctorOrganisationIdentifier = ? AND assignedTo IS NOT NULL");
        preparedStatement.setString(1, patientCpr);
        preparedStatement.setString(2, doctorOrganisationIdentifier);
        ResultSet resultSet = preparedStatement.executeQuery();
        assertTrue(resultSet.next());
    }
}
