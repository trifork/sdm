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

import com.trifork.stamdata.persistence.Record;
import com.trifork.stamdata.persistence.RecordSpecification;
import com.trifork.stamdata.persistence.RecordSpecification.FieldSpecification;
import com.trifork.stamdata.persistence.RecordSpecification.SikredeType;

// FIXME: These tests should be moved to common
public class SikredeFieldsTester {

    @Test
    public void testCorrectNumberOfFields() 
    {
        ImmutableList<FieldSpecification> fieldSpecs = RecordSpecification.SIKREDE_FIELDS_SINGLETON.getFieldSpecificationsInCorrectOrder();
        assertEquals(48, fieldSpecs.size());
    }

    @Test
    public void testCorrectNumberOfAlfanumericalFields() 
    {
        ImmutableList<FieldSpecification> fieldSpecs = RecordSpecification.SIKREDE_FIELDS_SINGLETON.getFieldSpecificationsInCorrectOrder();
        
        int alfanumericalFields = 0;
        for(FieldSpecification spec : fieldSpecs)
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
        ImmutableList<FieldSpecification> fieldSpecs = RecordSpecification.SIKREDE_FIELDS_SINGLETON.getFieldSpecificationsInCorrectOrder();
        
        int numericalFields = 0;
        for(FieldSpecification spec : fieldSpecs)
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
        RecordSpecification exampleRecordSpecification = RecordSpecification.newSikredeFields(
                "Foo", SikredeType.NUMERICAL, 10,
                "Bar", SikredeType.ALFANUMERICAL, 32);
        assertEquals(42, exampleRecordSpecification.acceptedTotalLineLength());
    }
    
    @Test
    public void testCorrectAcceptedTotalLineLengthForSingleton()
    {
        assertEquals(629, RecordSpecification.SIKREDE_FIELDS_SINGLETON.acceptedTotalLineLength());
    }
    
    @Test
    public void testConformsToSchemaSpecification()
    {
        RecordSpecification exampleRecordSpecification = RecordSpecification.newSikredeFields(
                "Foo", SikredeType.NUMERICAL, 10,
                "Bar", SikredeType.ALFANUMERICAL, 32);

        Record correctValues = SikredeRecordStringGenerator.sikredeRecordFromKeysAndValues("Foo", 42, "Bar", "12345678901234567890123456789012");
        Record correctValuesWhereBarIsShorter = SikredeRecordStringGenerator.sikredeRecordFromKeysAndValues("Foo", 42, "Bar", "123456789012345678901234567890");
        Record missingFoo = SikredeRecordStringGenerator.sikredeRecordFromKeysAndValues("Bar", "12345678901234567890123456789012");
        Record fooIsNotNumerical = SikredeRecordStringGenerator.sikredeRecordFromKeysAndValues("Foo", "Baz", "Bar", "12345678901234567890123456789012");
        Record barIsTooLong = SikredeRecordStringGenerator.sikredeRecordFromKeysAndValues("Foo", 42, "Bar", "1234567890123456789012345678901234567890");
        Record containsUnknownKey = SikredeRecordStringGenerator.sikredeRecordFromKeysAndValues("Foo", 42, "Bar", "12345678901234567890123456789012", "Baz", "Foobar");
        
        assertTrue(exampleRecordSpecification.conformsToSpecifications(correctValues));
        assertTrue(exampleRecordSpecification.conformsToSpecifications(correctValuesWhereBarIsShorter));
        assertFalse(exampleRecordSpecification.conformsToSpecifications(missingFoo));
        assertFalse(exampleRecordSpecification.conformsToSpecifications(fooIsNotNumerical));
        assertFalse(exampleRecordSpecification.conformsToSpecifications(barIsTooLong));
        assertFalse(exampleRecordSpecification.conformsToSpecifications(containsUnknownKey));
    }
}
