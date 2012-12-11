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
package com.trifork.stamdata.persistence;

import static com.trifork.stamdata.Preconditions.checkArgument;
import static com.trifork.stamdata.Preconditions.checkNotNull;

import com.trifork.stamdata.persistence.RecordSpecification.FieldSpecification;
import com.trifork.stamdata.persistence.RecordSpecification.RecordFieldType;

public class RecordBuilder
{
    private RecordSpecification recordSpecification;
    private Record record;
    
    public RecordBuilder(RecordSpecification recordSpecification)
    {
        this.recordSpecification = recordSpecification;
        record = new Record();
    }

    public RecordBuilder field(String fieldName, Object value)
    {
        if (value instanceof Long)
        {
            return field(fieldName, (Long)value);
        }
        else if (value instanceof String)
        {
            return field(fieldName, (String)value);
        }
        else
        {
            throw new IllegalArgumentException("Values in records must be string or long. field=" + fieldName);
        }
    }

    public RecordBuilder field(String fieldName, long value)
    {
        return field(fieldName, value, RecordSpecification.RecordFieldType.NUMERICAL);
    }
    
    public RecordBuilder field(String fieldName, String value)
    {
        return field(fieldName, value, RecordSpecification.RecordFieldType.ALPHANUMERICAL);
    }
    
    private RecordBuilder field(String fieldName, Object value, RecordSpecification.RecordFieldType recordFieldType)
    {
        checkNotNull(fieldName);
        checkArgument(value == null || getFieldType(fieldName) == recordFieldType, "Field " + fieldName + " is not " + recordFieldType);
        
        record = record.put(fieldName, value);
        
        return this;
    }
    
    public Record build()
    {
        if (recordSpecification.conformsToSpecifications(record))
        {
            return record;
        }
        else
        {
            throw new IllegalStateException("Mandatory fields not set");
        }
    }
    
    public Record addDummyFieldsAndBuild()
    {
        for(FieldSpecification fieldSpecification : recordSpecification.getFieldSpecs())
        {
            if(!record.containsKey(fieldSpecification.name))
            {
            if(fieldSpecification.type == RecordFieldType.ALPHANUMERICAL)
            {
                record = record.put(fieldSpecification.name, "D");
            }
            else if(fieldSpecification.type == RecordSpecification.RecordFieldType.NUMERICAL)
            {
                record = record.put(fieldSpecification.name, 0l);
            }
            else
            {
                throw new AssertionError("");
            }
            }
        }
        return build();
    }
    
    private RecordSpecification.RecordFieldType getFieldType(String fieldName)
    {
        for (FieldSpecification fieldSpecification: recordSpecification.getFieldSpecs())
        {
            if (fieldSpecification.name.equals(fieldName))
            {
                return fieldSpecification.type;
            }
        }
        
        return null;
    }
}
