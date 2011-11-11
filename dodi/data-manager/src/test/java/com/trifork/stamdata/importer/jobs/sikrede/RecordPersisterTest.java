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

import static com.trifork.stamdata.importer.jobs.sikrede.RecordSpecification.SikredeType.ALFANUMERICAL;
import static com.trifork.stamdata.importer.jobs.sikrede.RecordSpecification.SikredeType.NUMERICAL;
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
import org.joda.time.Instant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.jobs.sikrede.RecordSpecification.SikredeType;
import com.trifork.stamdata.importer.persistence.AuditingPersister;

public class RecordPersisterTest
{
    Connection connection;
    RecordSpecification recordSpecification;
    RecordPersister persister;
    
    @Before
    public void setupSikredePersister() throws SQLException
    {
        // TODO: Add test with two identical field names
        recordSpecification = RecordSpecification.newSikredeFields(
            "Foo",  NUMERICAL,       2,
            "Moo",  ALFANUMERICAL,   5
        );
        
        connection = MySQLConnectionManager.getConnection();
        createSikredeFieldsTableOnDatabase(connection, recordSpecification);
        
        AuditingPersister oldPersister = new AuditingPersister(connection);
        persister = new RecordPersister(recordSpecification, oldPersister);
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
        RecordBuilder builder = new RecordBuilder(recordSpecification);
        Record recordA = builder.field("Foo", 42).field("Moo", "Far").build();
        Record recordB = builder.field("Foo", 23).field("Moo", "Bar").build();

        DateTime theYear2000 = new DateTime(2000, 1, 1, 0, 0);
        persister.persistRecordWithValidityDate(recordA, "Moo", theYear2000.toInstant());
        connection.commit();

        DateTime theYear2010 = theYear2000.plusYears(10);
        persister.persistRecordWithValidityDate(recordB, "Moo", theYear2010.toInstant());
        connection.commit();

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM SikredeGenerated");

        int recordCount = 0;
        while(resultSet.next())
        {
            recordCount++;

            Record sikredeRecord = persister.createRecordUsingResultSet(resultSet);

            assertTrue(recordA.equals(sikredeRecord) || recordB.equals(sikredeRecord));
        }

        assertEquals(2, recordCount);
    }
    
    @Test
    public void testAddingTwoDifferentRecordsDontEffectEachOther() throws SQLException 
    {
        RecordBuilder builder = new RecordBuilder(recordSpecification);
        Record recordA = builder.field("Foo", 42).field("Moo", "Far").build();
        Record recordB = builder.field("Foo", 23).field("Moo", "Bar").build();
        
        Instant theYear2000 = new DateTime(2000, 1, 1, 0, 0).toInstant();
        persister.persistRecordWithValidityDate(recordA, "Moo", theYear2000);
        connection.commit();
        
        Instant theYear2010 = new DateTime(2010, 1, 1, 0, 0).toInstant();
        persister.persistRecordWithValidityDate(recordB, "Moo", theYear2010);
        connection.commit();
        
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM SikredeGenerated");
        
        int recordCount = 0;
        while(resultSet.next())
        {
            recordCount++;
            
            Record record = persister.createRecordUsingResultSet(resultSet);
            assertTrue(recordA.equals(record) || recordB.equals(record));
        }
        
        assertEquals(2, recordCount);
    }
    
    @Test
    public void testAddingExactSameRecordTwiceAddsANewRecord() throws SQLException 
    {
        RecordBuilder builder = new RecordBuilder(recordSpecification);
        Record record = builder.field("Foo", 42).field("Moo", "Far").build();
        
        Instant theYear2000 = new DateTime(2000, 1, 1, 0, 0).toInstant();
        persister.persistRecordWithValidityDate(record, "Moo", theYear2000);
        connection.commit();
        
        Instant theYear2010 = new DateTime(2010, 1, 1, 0, 0).toInstant();
        persister.persistRecordWithValidityDate(record, "Moo", theYear2010);
        connection.commit();
        
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM SikredeGenerated");
        
        int recordCount = 0;
        while(resultSet.next())
        {
            recordCount++;
            
            Record dbRecord = persister.createRecordUsingResultSet(resultSet);
            assertEquals("Far", dbRecord.get("Moo"));
            
            Instant validFrom = persister.getValidFrom(resultSet);
            Instant validTo = persister.getValidTo(resultSet);

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
        RecordBuilder builder = new RecordBuilder(recordSpecification);
        Record record = builder.field("Foo", 42).field("Moo", "Far").build();
        
        DateTime theYear2000 = new DateTime(2000, 1, 1, 0, 0);
        DateTime theYear2010 = theYear2000.plusYears(10);

        persister.persistRecordWithValidityDate(record, "Moo", theYear2010.toInstant());
        connection.commit();
        
        persister.persistRecordWithValidityDate(record, "Moo", theYear2000.toInstant());
        connection.commit();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testAddingARecordWithATimestampInTheMiddleOfAnExistingRecordRaisesAnException() throws SQLException 
    {
        RecordBuilder builder = new RecordBuilder(recordSpecification);
        Record record = builder.field("Foo", 42).field("Moo", "Far").build();
        
        DateTime theYear2000 = new DateTime(2000, 1, 1, 0, 0);
        DateTime theYear2005 = theYear2000.plusYears(5);
        DateTime theYear2010 = theYear2000.plusYears(10);
        
        persister.persistRecordWithValidityDate(record, "Moo", theYear2000.toInstant());
        connection.commit();
        
        persister.persistRecordWithValidityDate(record, "Moo", theYear2010.toInstant());
        connection.commit();
        
        persister.persistRecordWithValidityDate(record, "Moo", theYear2005.toInstant());
        connection.commit();
    }
    
    @Test
    public void testInsertStatementString() 
    {
        String expected = "INSERT INTO SikredeGenerated (Foo, Moo, ValidFrom) VALUES (?, ?, ?)";
        String actual = persister.createInsertStatementSql();
        assertEquals(expected, actual);
    }

    @Test
    public void testInsertValuesIntoPreparedStatement() throws SQLException
    {
        PreparedStatement mockedPrepareStatement = mock(PreparedStatement.class);
        Record record = SikredeRecordStringGenerator.sikredeRecordFromKeysAndValues("Moo", "Baz", "Foo", 42);
        
        persister.populateStatement(mockedPrepareStatement, record, Instant.now());

        verify(mockedPrepareStatement).setInt(1, 42);
        verify(mockedPrepareStatement).setString(2, "Baz");
    }
    
    @Test
    public void testSelectStatementString()
    {
        String actual = persister.createSelectStatementAsString("Foo");
        String expected = "SELECT * FROM SikredeGenerated WHERE Foo = ?";
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testSelectStatementAsPrepredStatement() throws SQLException
    {
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        
        when(mockConnection.prepareStatement("SELECT * FROM SikredeGenerated WHERE Foo = ?")).thenReturn(mockPreparedStatement);

        persister.createSelectStatementAsPreparedStatement(mockConnection, "Foo", 10);

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
        
        Record actual = persister.createRecordUsingResultSet(mockResultSet);
        
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
        
        persister.createRecordUsingResultSet(mockResultSet);
    }
    
    private void createSikredeFieldsTableOnDatabase(Connection connection, RecordSpecification recordSpecification) throws SQLException
    {
        Statement setupStatements = connection.createStatement();
        setupStatements.executeUpdate("DROP TABLE IF EXISTS SikredeGenerated");
        setupStatements.executeUpdate(RecordMySQLTableGenerator.createSqlSchema(recordSpecification));
    }
}
