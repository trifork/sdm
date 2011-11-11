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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.trifork.stamdata.persistence.SikredeFields;
import com.trifork.stamdata.persistence.SikredeRecord;
import com.trifork.stamdata.persistence.SikredeFields.SikredeFieldSpecification;
import com.trifork.stamdata.persistence.SikredeFields.SikredeType;

// FIXME: These tests should be moved to common
public class SikredeFieldsTester {

    @Test
    public void testCorrectNumberOfFields() 
    {
        ImmutableList<SikredeFieldSpecification> fieldSpecs = SikredeFields.SIKREDE_FIELDS_SINGLETON.getFieldSpecificationsInCorrectOrder();
        assertEquals(48, fieldSpecs.size());
    }

    @Test
    public void testCorrectNumberOfAlfanumericalFields() 
    {
        ImmutableList<SikredeFieldSpecification> fieldSpecs = SikredeFields.SIKREDE_FIELDS_SINGLETON.getFieldSpecificationsInCorrectOrder();
        
        int alfanumericalFields = 0;
        for(SikredeFieldSpecification spec : fieldSpecs)
        {
            if(spec.type == SikredeType.ALFANUMERICAL)
            {
                alfanumericalFields++;
            }
        }
        
        assertEquals(47, alfanumericalFields);
    }
    
    @Test
    public void testCorrectNumberOfNumericalFields() 
    {
        ImmutableList<SikredeFieldSpecification> fieldSpecs = SikredeFields.SIKREDE_FIELDS_SINGLETON.getFieldSpecificationsInCorrectOrder();
        
        int numericalFields = 0;
        for(SikredeFieldSpecification spec : fieldSpecs)
        {
            if(spec.type == SikredeType.NUMERICAL)
            {
                numericalFields++;
            }
        }
        
        assertEquals(1, numericalFields);
    }
    
    @Test
    public void testCorrectAcceptedTotalLineLength()
    {
        SikredeFields exampleSikredeFields = SikredeFields.newSikredeFields(
                "Foo", SikredeType.NUMERICAL, 10,
                "Bar", SikredeType.ALFANUMERICAL, 32);
        assertEquals(42, exampleSikredeFields.acceptedTotalLineLength());
    }
    
    @Test
    public void testCorrectAcceptedTotalLineLengthForSingleton()
    {
        assertEquals(629, SikredeFields.SIKREDE_FIELDS_SINGLETON.acceptedTotalLineLength());
    }
    
    @Test
    public void testConformsToSchemaSpecification()
    {
        SikredeFields exampleSikredeFields = SikredeFields.newSikredeFields(
                "Foo", SikredeType.NUMERICAL, 10,
                "Bar", SikredeType.ALFANUMERICAL, 32);

        SikredeRecord correctValues = SikredeRecordStringGenerator.sikredeRecordFromKeysAndValues("Foo", 42, "Bar", "12345678901234567890123456789012");
        SikredeRecord correctValuesWhereBarIsShorter = SikredeRecordStringGenerator.sikredeRecordFromKeysAndValues("Foo", 42, "Bar", "123456789012345678901234567890");
        SikredeRecord missingFoo = SikredeRecordStringGenerator.sikredeRecordFromKeysAndValues("Bar", "12345678901234567890123456789012");
        SikredeRecord fooIsNotNumerical = SikredeRecordStringGenerator.sikredeRecordFromKeysAndValues("Foo", "Baz", "Bar", "12345678901234567890123456789012");
        SikredeRecord barIsTooLong = SikredeRecordStringGenerator.sikredeRecordFromKeysAndValues("Foo", 42, "Bar", "1234567890123456789012345678901234567890");
        SikredeRecord containsUnknownKey = SikredeRecordStringGenerator.sikredeRecordFromKeysAndValues("Foo", 42, "Bar", "12345678901234567890123456789012", "Baz", "Foobar");
        
        assertTrue(exampleSikredeFields.conformsToSpecifications(correctValues));
        assertTrue(exampleSikredeFields.conformsToSpecifications(correctValuesWhereBarIsShorter));
        assertFalse(exampleSikredeFields.conformsToSpecifications(missingFoo));
        assertFalse(exampleSikredeFields.conformsToSpecifications(fooIsNotNumerical));
        assertFalse(exampleSikredeFields.conformsToSpecifications(barIsTooLong));
        assertFalse(exampleSikredeFields.conformsToSpecifications(containsUnknownKey));
    }
}
