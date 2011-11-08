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

import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.importer.jobs.sikrede.SikredeFields.SikredeType;

public class SikredeSqlSchemaCreatorTest {

    private SikredeFields exampleSikredeFields;
    
    @Before
    public void createExampleSikredeFields()
    {
        exampleSikredeFields = SikredeFields.newSikredeFields(
                "Foo", SikredeType.ALFANUMERICAL, 10,
                "Bar", SikredeType.NUMERICAL, 5,
                "Baz", SikredeType.ALFANUMERICAL, 42);
    }
    
    @Test
    public void testExampleSikredeFields() 
    {
        String expected = 
                "CREATE TABLE SikredeGenerated (" + 
                "SikredePID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY," + 
                "Foo VARCHAR(10) NOT NULL," +
                "Bar BIGINT NOT NULL," +
                "Baz VARCHAR(42) NOT NULL" +
                ") ENGINE=InnoDB COLLATE=utf8_bin;";
        String result = SikredeSqlSchemaCreator.createSqlSchema(exampleSikredeFields);
        
        assertEquals(expected, result.replaceAll("\n", "").replaceAll("\t", ""));
    }

    @Test
    public void testPrintOfActualSchema()
    {
        System.out.println(SikredeSqlSchemaCreator.createSqlSchema(SikredeFields.SIKREDE_FIELDS_SINGLETON));
    }
}
