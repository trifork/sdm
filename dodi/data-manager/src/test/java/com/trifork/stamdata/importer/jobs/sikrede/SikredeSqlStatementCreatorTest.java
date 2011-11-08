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

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
        String expected = "INSERT INTO SikredeGenerated (Foo, Bar) VALUES (?, ?)";
        String actual = exampleStatementCreator.insertStatementString();
        assertEquals(expected, actual);
    }

    @Test
    public void testInsertValuesIntoPreparedStatement() throws SQLException
    {
        PreparedStatement mockedPrepareStatement = mock(PreparedStatement.class);
        Map<String, Object> values = createMap("Bar", "Baz", "Foo", 42);
        
        exampleStatementCreator.insertValuesIntoPreparedStatement(mockedPrepareStatement, values);

        verify(mockedPrepareStatement).setInt(1, 42);
        verify(mockedPrepareStatement).setString(2, "Baz");
    }
    
    private Map<String, Object> createMap(Object... keysAndValues)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        
        assertTrue(keysAndValues.length % 2 == 0);
        
        for(int i = 0; i < keysAndValues.length; i += 2)
        {
            String key = (String) keysAndValues[i];
            Object value = keysAndValues[i+1];
            map.put(key, value);
        }
        
        return map;
    }
   
}
