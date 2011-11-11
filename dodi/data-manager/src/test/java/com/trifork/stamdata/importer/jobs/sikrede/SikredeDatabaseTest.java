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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.persistence.AuditingPersister;
import com.trifork.stamdata.persistence.SikredeFetcher;
import com.trifork.stamdata.persistence.SikredeFields;
import com.trifork.stamdata.persistence.SikredeRecord;
import com.trifork.stamdata.persistence.SikredeRecordBuilder;
import com.trifork.stamdata.persistence.SikredeFields.SikredeType;

// FIXME: The tests concerning the fetcher should be moved to common
public class SikredeDatabaseTest {

    Connection connection;
    SikredeFields exampleSikredeFields;
    SikredePersisterUsingNewArchitecture sikredePersister;
    SikredeFetcher sikredeFetcher;
    
    @Before
    public void setupSikredePersister() throws SQLException
    {
        // TODO: Add test with two identical field names
        exampleSikredeFields = SikredeFields.newSikredeFields(
                "Foo", SikredeType.NUMERICAL, 2,
                "Moo", SikredeType.ALFANUMERICAL, 5
                );
        
        connection = MySQLConnectionManager.getConnection();
        createSikredeFieldsTableOnDatabase(connection, exampleSikredeFields);
        
        AuditingPersister persister = new AuditingPersister(connection);
        sikredePersister = new SikredePersisterUsingNewArchitecture(exampleSikredeFields, persister);
        sikredeFetcher = new SikredeFetcher(exampleSikredeFields);
    }

    @After
    public void closeConnection() throws SQLException
    {
        if(connection != null)
        {
            connection.close();
        }
    }
    
    @Test
    public void testAddingTheSameRecordTwiceButWithNeverTimestamp() throws SQLException 
    {
        SikredeRecordBuilder builder = new SikredeRecordBuilder(exampleSikredeFields);
        SikredeRecord record = builder.field("Foo", 42).field("Moo", "Far").build();
        
        DateTime theYear2000 = new DateTime(2000, 1, 1, 0, 0);
        sikredePersister.persistRecordWithValidityDate(record.withField("Foo", 42), "Moo", theYear2000);
        connection.commit();
        
        DateTime theYear2010 = theYear2000.plusYears(10);
        sikredePersister.persistRecordWithValidityDate(record.withField("Foo", 10), "Moo", theYear2010);
        connection.commit();
        
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM SikredeGenerated");
        
        int recordCount = 0;
        while(resultSet.next())
        {
            recordCount++;
            
            SikredeRecord sikredeRecord = sikredeFetcher.sikredeDataFromResultSet(resultSet);
            assertEquals("Far", sikredeRecord.get("Moo"));

            DateTime validFrom = sikredePersister.getValidFrom(resultSet);
            DateTime validTo = sikredePersister.getValidTo(resultSet);
            if(sikredeRecord.get("Foo").equals(42))
            {
                assertEquals(theYear2000, validFrom);
                assertEquals(theYear2010, validTo);
            }
            else if(sikredeRecord.get("Foo").equals(10))
            {
                assertEquals(theYear2010, validFrom);
                assertEquals(null, validTo);
            }
            else
            {
                throw new AssertionError("Unexpected value of \"Foo\" in test: " + sikredeRecord.get("Foo"));
            }
        }
        
        assertEquals(2, recordCount);
    }
    
    @Test
    public void testAddingTwoDifferentRecordsDontEffectEachOther() throws SQLException 
    {
        SikredeRecordBuilder builder = new SikredeRecordBuilder(exampleSikredeFields);
        SikredeRecord recordA = builder.field("Foo", 42).field("Moo", "Far").build();
        SikredeRecord recordB = builder.field("Foo", 23).field("Moo", "Bar").build();
        
        DateTime theYear2000 = new DateTime(2000, 1, 1, 0, 0);
        sikredePersister.persistRecordWithValidityDate(recordA, "Moo", theYear2000);
        connection.commit();
        
        DateTime theYear2010 = theYear2000.plusYears(10);
        sikredePersister.persistRecordWithValidityDate(recordB, "Moo", theYear2010);
        connection.commit();
        
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM SikredeGenerated");
        
        int recordCount = 0;
        while(resultSet.next())
        {
            recordCount++;
            
            SikredeRecord sikredeRecord = sikredeFetcher.sikredeDataFromResultSet(resultSet);
            
            assertTrue(recordA.equals(sikredeRecord) || recordB.equals(sikredeRecord));
        }
        
        assertEquals(2, recordCount);
    }
    
    @Test
    public void testAddingExactSameRecordTwiceAddsANewRecord() throws SQLException 
    {
        SikredeRecordBuilder builder = new SikredeRecordBuilder(exampleSikredeFields);
        SikredeRecord record = builder.field("Foo", 42).field("Moo", "Far").build();
        
        DateTime theYear2000 = new DateTime(2000, 1, 1, 0, 0);
        sikredePersister.persistRecordWithValidityDate(record, "Moo", theYear2000);
        connection.commit();
        
        DateTime theYear2010 = theYear2000.plusYears(10);
        sikredePersister.persistRecordWithValidityDate(record, "Moo", theYear2010);
        connection.commit();
        
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM SikredeGenerated");
        
        int recordCount = 0;
        while(resultSet.next())
        {
            recordCount++;
            
            SikredeRecord sikredeRecord = sikredeFetcher.sikredeDataFromResultSet(resultSet);
            assertEquals("Far", sikredeRecord.get("Moo"));
            
            DateTime validFrom = sikredePersister.getValidFrom(resultSet);
            DateTime validTo = sikredePersister.getValidTo(resultSet);

            if(validFrom.equals(theYear2000))
            {
                assertEquals(theYear2010, validTo);
            }
            else if(validFrom.equals(theYear2010))
            {
                assertEquals(null, validTo);
            }
            else
            {
                throw new AssertionError("Unexpected value of \"ValidFrom\" in test: " + validFrom);
            }
        }
        
        assertEquals(2, recordCount);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testAddingTheSameRecordWithAnEarlierTimestampRaisesAnException() throws SQLException 
    {
        SikredeRecordBuilder builder = new SikredeRecordBuilder(exampleSikredeFields);
        SikredeRecord record = builder.field("Foo", 42).field("Moo", "Far").build();
        
        DateTime theYear2000 = new DateTime(2000, 1, 1, 0, 0);
        DateTime theYear2010 = theYear2000.plusYears(10);

        sikredePersister.persistRecordWithValidityDate(record, "Moo", theYear2010);
        connection.commit();
        
        sikredePersister.persistRecordWithValidityDate(record, "Moo", theYear2000);
        connection.commit();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testAddingARecordWithATimestampInTheMiddleOfAnExistingRecordRaisesAnException() throws SQLException 
    {
        SikredeRecordBuilder builder = new SikredeRecordBuilder(exampleSikredeFields);
        SikredeRecord record = builder.field("Foo", 42).field("Moo", "Far").build();
        
        DateTime theYear2000 = new DateTime(2000, 1, 1, 0, 0);
        DateTime theYear2005 = theYear2000.plusYears(5);
        DateTime theYear2010 = theYear2000.plusYears(10);
        
        sikredePersister.persistRecordWithValidityDate(record, "Moo", theYear2000);
        connection.commit();
        
        sikredePersister.persistRecordWithValidityDate(record, "Moo", theYear2010);
        connection.commit();
        
        sikredePersister.persistRecordWithValidityDate(record, "Moo", theYear2005);
        connection.commit();
    }
    
    @Test
    public void testInsertStatementString() 
    {
        String expected = "INSERT INTO SikredeGenerated (Foo, Moo, ValidFrom) VALUES (?, ?, ?)";
        String actual = sikredePersister.insertStatementString();
        assertEquals(expected, actual);
    }

    @Test
    public void testInsertValuesIntoPreparedStatement() throws SQLException
    {
        PreparedStatement mockedPrepareStatement = mock(PreparedStatement.class);
        SikredeRecord record = SikredeRecordStringGenerator.sikredeRecordFromKeysAndValues("Moo", "Baz", "Foo", 42);
        
        sikredePersister.insertValuesIntoPreparedStatement(mockedPrepareStatement, record, new DateTime());

        verify(mockedPrepareStatement).setInt(1, 42);
        verify(mockedPrepareStatement).setString(2, "Baz");
    }
    
    @Test
    public void testSelectStatementString()
    {
        String actual = sikredeFetcher.createSelectStatementAsString("Foo");
        String expected = "SELECT * FROM SikredeGenerated WHERE Foo = ?";
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testSelectStatementAsPrepredStatement() throws SQLException
    {
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        
        when(mockConnection.prepareStatement("SELECT * FROM SikredeGenerated WHERE Foo = ?")).thenReturn(mockPreparedStatement);

        sikredeFetcher.createSelectStatementAsPreparedStatement(mockConnection, "Foo", 10);

        verify(mockPreparedStatement).setObject(1, 10);
    }
    
    @Test
    public void testSikredeDataFromResultSet() throws SQLException
    {
        ResultSet mockResultSet = mock(ResultSet.class);
        
        when(mockResultSet.isBeforeFirst()).thenReturn(false);
        when(mockResultSet.isAfterLast()).thenReturn(false);

        when(mockResultSet.getInt("Foo")).thenReturn(42);
        when(mockResultSet.getString("Moo")).thenReturn("Moo");
        
        SikredeRecord actual = sikredeFetcher.sikredeDataFromResultSet(mockResultSet);
        
        assertTrue(actual.containsKey("Foo"));
        assertEquals(42, actual.get("Foo"));
        
        assertTrue(actual.containsKey("Moo"));
        assertEquals("Moo", actual.get("Moo"));
    }
    
    @Test(expected=IllegalStateException.class)
    public void testSikredeDataFromResultSetWhichContainsStringThatIsLongerThanSpecified() throws SQLException
    {
        ResultSet mockResultSet = mock(ResultSet.class);
        
        when(mockResultSet.isBeforeFirst()).thenReturn(false);
        when(mockResultSet.isAfterLast()).thenReturn(false);
        
        when(mockResultSet.getInt("Foo")).thenReturn(42);
        when(mockResultSet.getString("Moo")).thenReturn("MooMoo");
        
        sikredeFetcher.sikredeDataFromResultSet(mockResultSet);
    }
    
    private void createSikredeFieldsTableOnDatabase(Connection connection, SikredeFields sikredeFields) throws SQLException
    {
        Statement setupStatements = connection.createStatement();
        setupStatements.executeUpdate("DROP TABLE IF EXISTS SikredeGenerated");
        setupStatements.executeUpdate(SikredeSqlSchemaCreator.createSqlSchema(sikredeFields));
    }
}
