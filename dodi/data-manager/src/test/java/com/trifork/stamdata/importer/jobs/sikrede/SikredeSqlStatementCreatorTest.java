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

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.importer.jobs.sikrede.SikredeFields.SikredeType;

public class SikredeSqlStatementCreatorTest {

    private SikredeFields exampleSikredeFields;
    private SikredeSqlStatementCreator exampleStatementCreator;

    @Before
    public void setupExampleSikredeFields()
    {
        this.exampleSikredeFields = SikredeFields.newSikredeFields(
                "Foo", SikredeType.NUMERICAL, 3,
                "Bar", SikredeType.ALFANUMERICAL, 5);
        
        this.exampleStatementCreator = new SikredeSqlStatementCreator(exampleSikredeFields);
    }
    
    @Test
    public void testInsertStatementString() 
    {
        String expected = "INSERT INTO SikredeGenerated (Foo, Bar, ValidFrom) VALUES (?, ?, ?)";
        String actual = exampleStatementCreator.insertStatementString();
        assertEquals(expected, actual);
    }

    @Test
    public void testInsertValuesIntoPreparedStatement() throws SQLException
    {
        PreparedStatement mockedPrepareStatement = mock(PreparedStatement.class);
        SikredeRecord record = SikredeRecordStringGenerator.sikredeRecordFromKeysAndValues("Bar", "Baz", "Foo", 42);
        
        exampleStatementCreator.insertValuesIntoPreparedStatement(mockedPrepareStatement, record, new DateTime());

        verify(mockedPrepareStatement).setInt(1, 42);
        verify(mockedPrepareStatement).setString(2, "Baz");
    }
    
    @Test
    public void testSelectStatementString()
    {
        String actual = exampleStatementCreator.createSelectStatementAsString("Foo");
        String expected = "SELECT * FROM SikredeGenerated WHERE Foo = ?";
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testSelectStatementAsPrepredStatement() throws SQLException
    {
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        
        when(mockConnection.prepareStatement("SELECT * FROM SikredeGenerated WHERE Foo = ?")).thenReturn(mockPreparedStatement);

        exampleStatementCreator.createSelectStatementAsPreparedStatement(mockConnection, "Foo", 10);

        verify(mockPreparedStatement).setObject(1, 10);
    }
    
    @Test
    public void testSikredeDataFromResultSet() throws SQLException
    {
        ResultSet mockResultSet = mock(ResultSet.class);
        
        when(mockResultSet.isBeforeFirst()).thenReturn(false);
        when(mockResultSet.isAfterLast()).thenReturn(false);

        when(mockResultSet.getInt("Foo")).thenReturn(42);
        when(mockResultSet.getString("Bar")).thenReturn("Moo");
        
        SikredeRecord actual = exampleStatementCreator.sikredeDataFromResultSet(mockResultSet);
        
        assertTrue(actual.containsKey("Foo"));
        assertEquals(42, actual.get("Foo"));
        
        assertTrue(actual.containsKey("Bar"));
        assertEquals("Moo", actual.get("Bar"));
    }
    
    @Test(expected=IllegalStateException.class)
    public void testSikredeDataFromResultSetWhichContainsStringThatIsLongerThanSpecified() throws SQLException
    {
        ResultSet mockResultSet = mock(ResultSet.class);
        
        when(mockResultSet.isBeforeFirst()).thenReturn(false);
        when(mockResultSet.isAfterLast()).thenReturn(false);
        
        when(mockResultSet.getInt("Foo")).thenReturn(42);
        when(mockResultSet.getString("Bar")).thenReturn("MooMoo");
        
        exampleStatementCreator.sikredeDataFromResultSet(mockResultSet);
    }
    
}
