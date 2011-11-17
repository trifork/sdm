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

import com.google.common.collect.Iterables;
import com.trifork.stamdata.persistence.Record;
import com.trifork.stamdata.persistence.RecordSpecification;
import com.trifork.stamdata.persistence.RecordSpecification.FieldSpecification;
import com.trifork.stamdata.persistence.RecordSpecification.RecordFieldType;
import com.trifork.stamdata.specs.SikredeRecordSpecs;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

// FIXME: These tests should be moved to common
public class SikredeFieldsTester
{
    @Test
    public void testCorrectNumberOfFields() 
    {
        Iterable<FieldSpecification> fieldSpecs = SikredeRecordSpecs.ENTRY_RECORD_SPEC.getFieldSpecs();
        assertThat(Iterables.size(fieldSpecs), is(48));
    }

    @Test
    public void testCorrectNumberOfAlfanumericalFields() 
    {
        Iterable<FieldSpecification> fieldSpecs = SikredeRecordSpecs.ENTRY_RECORD_SPEC.getFieldSpecs();
        
        int alphanumericalFields = 0;

        for(FieldSpecification spec : fieldSpecs)
        {
            if(spec.type == RecordSpecification.RecordFieldType.ALPHANUMERICAL)
            {
                alphanumericalFields++;
            }
        }
        
        assertEquals(47, alphanumericalFields);
    }
    
    @Test
    public void testCorrectNumberOfNumericalFields() 
    {
        Iterable<FieldSpecification> fieldSpecs = SikredeRecordSpecs.ENTRY_RECORD_SPEC.getFieldSpecs();
        
        int numericalFields = 0;
        for(FieldSpecification spec : fieldSpecs)
        {
            if(spec.type == RecordFieldType.NUMERICAL)
            {
                numericalFields++;
            }
        }
        
        assertEquals(1, numericalFields);
    }
    
    @Test
    public void testCorrectAcceptedTotalLineLength()
    {
        RecordSpecification exampleRecordSpecification = RecordSpecification.createSpec("SikredeGenerated", "Foo",
                "Foo", RecordFieldType.NUMERICAL, 10,
                "Bar", RecordFieldType.ALPHANUMERICAL, 32);
        assertEquals(42, exampleRecordSpecification.acceptedTotalLineLength());
    }
    
    @Test
    public void testCorrectAcceptedTotalLineLengthForSingleton()
    {
        assertEquals(629, SikredeRecordSpecs.ENTRY_RECORD_SPEC.acceptedTotalLineLength());
    }
    
    @Test
    public void testConformsToSchemaSpecification()
    {
        RecordSpecification exampleRecordSpecification = RecordSpecification.createSpec("SikredeGenerated", "Foo",
                "Foo", RecordFieldType.NUMERICAL, 10,
                "Bar", RecordSpecification.RecordFieldType.ALPHANUMERICAL, 32);

        Record correctValues = RecordGenerator.createRecord("Foo", 42, "Bar", "12345678901234567890123456789012");
        Record correctValuesWhereBarIsShorter = RecordGenerator.createRecord("Foo", 42, "Bar", "123456789012345678901234567890");
        Record missingFoo = RecordGenerator.createRecord("Bar", "12345678901234567890123456789012");
        Record fooIsNotNumerical = RecordGenerator.createRecord("Foo", "Baz", "Bar", "12345678901234567890123456789012");
        Record barIsTooLong = RecordGenerator.createRecord("Foo", 42, "Bar", "1234567890123456789012345678901234567890");
        Record containsUnknownKey = RecordGenerator.createRecord("Foo", 42, "Bar", "12345678901234567890123456789012", "Baz", "Foobar");
        
        assertTrue(exampleRecordSpecification.conformsToSpecifications(correctValues));
        assertTrue(exampleRecordSpecification.conformsToSpecifications(correctValuesWhereBarIsShorter));
        assertFalse(exampleRecordSpecification.conformsToSpecifications(missingFoo));
        assertFalse(exampleRecordSpecification.conformsToSpecifications(fooIsNotNumerical));
        assertFalse(exampleRecordSpecification.conformsToSpecifications(barIsTooLong));
        assertFalse(exampleRecordSpecification.conformsToSpecifications(containsUnknownKey));
    }
}
