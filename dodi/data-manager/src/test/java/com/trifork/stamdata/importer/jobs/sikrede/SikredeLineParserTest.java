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

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.trifork.stamdata.importer.jobs.sikrede.SikredeFields.SikredeFieldSpecification;
import com.trifork.stamdata.importer.jobs.sikrede.SikredeFields.SikredeType;

public class SikredeLineParserTest {

    SikredeFields exampleSikredeFields;
    SikredeLineParser exampleSikredeLineParser;
    
    @Before
    public void setupExampleSikredeFields()
    {
        exampleSikredeFields = SikredeFields.newSikredeFields(
                "PostType", SikredeType.NUMERICAL, 2,
                "TruncatedString", SikredeType.ALFANUMERICAL, 5,
                "UntruncatedString", SikredeType.ALFANUMERICAL, 6,
                "NumberWithSpacesForPadding", SikredeType.NUMERICAL, 6);
        
        exampleSikredeLineParser = new SikredeLineParser(exampleSikredeFields);
    }
    
    @Test
    public void testSikredeLineParserParsingASimpleLine() 
    {
        String exampleLine = validateStringsConformToSikredeLengths(
                exampleSikredeFields, 
                    "01",
                 "din  ",
                "farmor",
                "    42");
        
        SikredeRecord record = exampleSikredeLineParser.parseLine(exampleLine);
        
        assertEquals(1, record.get("PostType"));
        assertEquals("din", record.get("TruncatedString"));
        assertEquals("farmor", record.get("UntruncatedString"));
        assertEquals(42, record.get("NumberWithSpacesForPadding"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSikredeLineParserParsingALineWithANumberContainingChars() 
    {
        String exampleLine = validateStringsConformToSikredeLengths(
                exampleSikredeFields, 
                "A1",
                "din  ",
                "farmor",
                "    42");
        exampleSikredeLineParser.parseLine(exampleLine);
    }
    
    /*
     * This method is used for validating the test data before the actual test.
     */
    private String validateStringsConformToSikredeLengths(SikredeFields sikredeFields, String... strings)
    {
        ImmutableList<SikredeFieldSpecification> fieldSpecs = sikredeFields.getFieldSpecificationsInCorrectOrder();
        assertEquals(fieldSpecs.size(), strings.length);
        
        String result = "";
        for(int i = 0; i < strings.length; i++)
        {
            assertEquals(fieldSpecs.get(i).length, strings[i].length());
            result = result + strings[i];
        }
        
        return result;
    }
}