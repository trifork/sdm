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

import static org.junit.Assert.assertTrue;

import com.trifork.stamdata.persistence.SikredeFields;
import com.trifork.stamdata.persistence.SikredeRecord;
import com.trifork.stamdata.persistence.SikredeFields.SikredeFieldSpecification;
import com.trifork.stamdata.persistence.SikredeFields.SikredeType;

public class SikredeRecordStringGenerator {

    private SikredeFields sikredeFields;

    public SikredeRecordStringGenerator(SikredeFields sikredeFields)
    {
        this.sikredeFields = sikredeFields;
    }
    
    public String stringFromRecords(SikredeRecord sikredeRecord)
    {
        if(!sikredeFields.conformsToSpecifications(sikredeRecord))
        {
            throw new IllegalArgumentException("Sikrede record does not conform to specification");
        }
        
        StringBuilder builder = new StringBuilder();
        for(SikredeFieldSpecification fieldSpecification: sikredeFields.getFieldSpecificationsInCorrectOrder())
        {
            if(fieldSpecification.type == SikredeType.ALFANUMERICAL)
            {
                String value = (String) sikredeRecord.get(fieldSpecification.name);
                builder.append(prefixPadding(' ', fieldSpecification.length - value.length()));
                builder.append(value);
            }
            else if(fieldSpecification.type == SikredeType.NUMERICAL)
            {
                String value = Integer.toString((Integer) sikredeRecord.get(fieldSpecification.name));
                builder.append(prefixPadding('0', fieldSpecification.length - value.length()));
                builder.append(value);
            }
            else
            {
                throw new AssertionError("Missing implementation for type " + fieldSpecification.type);
            }
        }
        return builder.toString();
    }
    
    public String stringFromIncompleteRecord(SikredeRecord sikredeRecord)
    {
        for(SikredeFieldSpecification fieldSpecification: sikredeFields.getFieldSpecificationsInCorrectOrder())
        {
            if(!sikredeRecord.containsKey(fieldSpecification.name))
            {
                if(fieldSpecification.type == SikredeType.ALFANUMERICAL)
                {
                    sikredeRecord = sikredeRecord.withField(fieldSpecification.name, "");
                }
                else if(fieldSpecification.type == SikredeType.NUMERICAL)
                {
                    sikredeRecord = sikredeRecord.withField(fieldSpecification.name, 0);
                } 
                else
                {
                    throw new AssertionError("Missing implementation for type " + fieldSpecification.type);
                }
            }
        }
        
        return stringFromRecords(sikredeRecord);
    }
    
    public String stringRecordFromIncompleteSetOfFields(Object... keysAndValues)
    {
        return stringFromIncompleteRecord(sikredeRecordFromKeysAndValues(keysAndValues));
    }
    
    public static SikredeRecord sikredeRecordFromKeysAndValues(Object... keysAndValues)
    {
        SikredeRecord record = new SikredeRecord();
        
        assertTrue(keysAndValues.length % 2 == 0);
        
        for(int i = 0; i < keysAndValues.length; i += 2)
        {
            String key = (String) keysAndValues[i];
            Object value = keysAndValues[i+1];
            record = record.withField(key, value);
        }
        
        return record;
    }
    
    private String prefixPadding(char paddingElement, int n)
    {
        String padding = "";
        for(int i = 0; i < n; i++)
        {
            padding += paddingElement;
        }
        return padding;
    }
}
