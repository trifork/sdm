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

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.trifork.stamdata.Preconditions;

/**
 * @author Thomas G. Kristensen <tgk@trifork.com>
 */
public class RecordSpecification
{
    private final String table;
    private final String keyColumn;

    public String getTable()
    {
        return table;
    }

    public String getKeyColumn()
    {
        return keyColumn;
    }

    public static enum RecordFieldType
    {
        ALPHANUMERICAL,
        NUMERICAL
    }
    
    public static class FieldSpecification
    {
        public final String name;
        public final RecordFieldType type;
        public final int length;
        
        public FieldSpecification(String name, RecordFieldType type, int length)
        {
            this.name = name;
            this.type = type;
            this.length = length;
        }
    }

    private final List<FieldSpecification> fields;
    
    private RecordSpecification(String table, String keyColumn)
    {
        this.table = table;
        this.keyColumn = keyColumn;
        
        fields = new ArrayList<FieldSpecification>();
    }
    
    public static RecordSpecification createSpec(String table, String keyColumn, Object... fieldDefinitions)
    {
        Preconditions.checkArgument(fieldDefinitions.length % 3 == 0);

        RecordSpecification recordSpecification = new RecordSpecification(table, keyColumn);
        
        for (int i = 0; i < fieldDefinitions.length; i += 3)
        {
            String name = (String) fieldDefinitions[i + 0];
            RecordFieldType type = (RecordFieldType) fieldDefinitions[i + 1];
            int length = (Integer) fieldDefinitions[i + 2];
            
            FieldSpecification fieldSpecification = new FieldSpecification(name, type, length);
            
            recordSpecification.fields.add(fieldSpecification);
        }
        
        return recordSpecification;
    }
    
    public Iterable<FieldSpecification> getFieldSpecs()
    {
        return ImmutableList.copyOf(fields);
    }
    
    public int acceptedTotalLineLength()
    {
        int totalLength = 0;
        for(FieldSpecification fieldSpecification: fields)
        {
            totalLength += fieldSpecification.length;
        }
        return totalLength;
    }
    
    public boolean conformsToSpecifications(Record record)
    {
        Preconditions.checkNotNull(record, "record");
        
        if (record.size() != fields.size())
        {
            return false;
        }
        
        for (FieldSpecification fieldsSpecification: fields)
        {
            if (!record.containsKey(fieldsSpecification.name))
            {
                return false;
            }
            else
            {
                Object value = record.get(fieldsSpecification.name);
                
                if (fieldsSpecification.type == RecordFieldType.NUMERICAL)
                {
                    if (!(value instanceof Integer))
                    {
                        return false;
                    }
                }
                else if (fieldsSpecification.type == RecordFieldType.ALPHANUMERICAL)
                {
                    if (value != null && !(value instanceof String))
                    {
                        return false;
                    }
                    else if (value != null)
                    {
                        String valueAsString = String.valueOf(value);
                        
                        if (valueAsString.length() > fieldsSpecification.length)
                        {
                            return false;
                        }
                    }
                }
                else
                {
                    throw new AssertionError("Field specification is in illegal state. Type must be set.");
                }
            }
        }
        
        return true;
    }
}
