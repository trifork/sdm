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
    public void testUpdateExistingRelationshipWhenDoctorDoesNotExist() throws SQLException
    {
        brsUpdater.updateExistingRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM, ASSIGNED_TO);
        assertClosedRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier);
    }

    @Test
    public void testUpdateExistingRelationshipWhenDoctorExistWithOpenRelationship() throws SQLException
    {
        brsUpdater.insertRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM, null);
        assertRecordExists(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM);
        brsUpdater.updateExistingRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM.minusDays(1), ASSIGNED_TO);
        assertRecordExists(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM, ASSIGNED_TO);
    }
    
    @Test
    public void testUpdateExistingRelationshipWhenDoctorExistWithClosedRelationship() throws SQLException
    {
        brsUpdater.insertRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM, ASSIGNED_TO);
        assertRecordExists(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM, ASSIGNED_TO);
        brsUpdater.updateExistingRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_TO.plusYears(2), ASSIGNED_TO.plusYears(3));
        assertRecordExists(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM, ASSIGNED_TO);
        assertRecordExists(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_TO.plusYears(2), ASSIGNED_TO.plusYears(3));
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

    private void assertOpenRelationship(String patientCpr, String doctorOrganisationIdentifier) throws SQLException
    {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM AssignedDoctor WHERE patientCpr = ? AND doctorOrganisationIdentifier = ? AND assignedTo IS NULL");
        preparedStatement.setString(1, patientCpr);
        preparedStatement.setString(2, doctorOrganisationIdentifier);
        ResultSet resultSet = preparedStatement.executeQuery();
        assertTrue(resultSet.next());
    }

    private void assertAssignedFromExists(String patientCpr, String doctorOrganisationIdentifier, DateTime assignedFrom) throws SQLException
    {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM AssignedDoctor WHERE patientCpr = ? AND doctorOrganisationIdentifier = ? AND assignedFrom = ?");
        preparedStatement.setString(1, patientCpr);
        preparedStatement.setString(2, doctorOrganisationIdentifier);
        preparedStatement.setDate(3, new Date(assignedFrom.getMillis()));
        ResultSet resultSet = preparedStatement.executeQuery();
        assertTrue(resultSet.next());
    }
}
