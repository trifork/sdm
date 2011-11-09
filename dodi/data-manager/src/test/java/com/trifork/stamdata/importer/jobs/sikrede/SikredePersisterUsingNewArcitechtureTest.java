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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.jobs.sikrede.SikredeFields.SikredeType;
import com.trifork.stamdata.importer.persistence.AuditingPersister;

public class SikredePersisterUsingNewArcitechtureTest {

    Connection connection;
    SikredeFields exampleSikredeFields;
    SikredePersisterUsingNewArchitecture sikredePersister;
    
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
        sikredePersister = new SikredePersisterUsingNewArchitecture(new SikredeSqlStatementCreator(exampleSikredeFields), persister);
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
    public void testSimplePersistense() throws SQLException 
    {
        SikredeRecordBuilder builder = new SikredeRecordBuilder(exampleSikredeFields);
        SikredeRecord record = builder.field("Foo", 42).field("Moo", "Far").build();
        sikredePersister.persist(record);
        connection.commit();
        
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT Count(*) FROM SikredeGenerated");
        resultSet.next();
        long numberOfFoundRecords = resultSet.getLong(1);
        assertEquals(1, numberOfFoundRecords);
    }

    @Test
    public void testAddingTheSameRecordTwiceButWithNeverTimestamp() throws SQLException 
    {
        SikredeRecordBuilder builder = new SikredeRecordBuilder(exampleSikredeFields);
        SikredeRecord record = builder.field("Foo", 42).field("Moo", "Far").build();
        
        DateTime theYear2000 = new DateTime(2000, 1, 1, 0, 0);
        sikredePersister.persistRecordWithValidityDate(record.setField("Foo", 42), "Moo", theYear2000);
        connection.commit();
        
        DateTime theYear2010 = theYear2000.plusYears(10);
        sikredePersister.persistRecordWithValidityDate(record.setField("Foo", 10), "Moo", theYear2010);
        connection.commit();
        
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM SikredeGenerated");
        
        SikredeSqlStatementCreator sikredeSqlStatementCreator = new SikredeSqlStatementCreator(exampleSikredeFields);
        
        int recordCount = 0;
        while(resultSet.next())
        {
            recordCount++;
            
            SikredeRecord sikredeRecord = sikredeSqlStatementCreator.sikredeDataFromResultSet(resultSet);
            assertEquals("Far", sikredeRecord.get("Moo"));

            DateTime validFrom = sikredePersister.getValidFrom(resultSet);
            DateTime validTo = sikredePersister.getValidtTo(resultSet);
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
        
        SikredeSqlStatementCreator sikredeSqlStatementCreator = new SikredeSqlStatementCreator(exampleSikredeFields);
        
        int recordCount = 0;
        while(resultSet.next())
        {
            recordCount++;
            
            SikredeRecord sikredeRecord = sikredeSqlStatementCreator.sikredeDataFromResultSet(resultSet);
            
            assertTrue(recordsEqual(recordA, sikredeRecord) || recordsEqual(recordB, sikredeRecord));
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
        
        SikredeSqlStatementCreator sikredeSqlStatementCreator = new SikredeSqlStatementCreator(exampleSikredeFields);
        
        int recordCount = 0;
        while(resultSet.next())
        {
            recordCount++;
            
            SikredeRecord sikredeRecord = sikredeSqlStatementCreator.sikredeDataFromResultSet(resultSet);
            assertEquals("Far", sikredeRecord.get("Moo"));
            
            DateTime validFrom = sikredePersister.getValidFrom(resultSet);
            DateTime validTo = sikredePersister.getValidtTo(resultSet);

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
    
    private void createSikredeFieldsTableOnDatabase(Connection connection, SikredeFields sikredeFields) throws SQLException
    {
        Statement setupStatements = connection.createStatement();
        setupStatements.executeUpdate("DROP TABLE IF EXISTS SikredeGenerated");
        setupStatements.executeUpdate(SikredeSqlSchemaCreator.createSqlSchema(sikredeFields));
    }
    
    private boolean recordsEqual(SikredeRecord lhs, SikredeRecord rhs) 
    {
        return lhs.map.equals(rhs.map);
    }
}
