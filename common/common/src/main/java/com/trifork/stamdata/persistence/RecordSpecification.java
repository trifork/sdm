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
import java.util.Arrays;
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
    
    public static enum Modifiers
    {
        DO_NOT_PERSIST;
    }
    
    public static class FieldSpecification
    {
        public final String name;
        public final RecordFieldType type;
        public final int length;
        public final boolean persistField;
        
        public FieldSpecification(String name, RecordFieldType type, int length, boolean persistField)
        {
            this.name = name;
            this.type = type;
            this.length = length;
            this.persistField = persistField;
        }

        /**
         * Returns a copy of the field with a numerical entry
         */
        public FieldSpecification numerical()
        {
            return new FieldSpecification(name, RecordFieldType.NUMERICAL, length, persistField);
        }
        
        /**
         * Returns a copy of the field that will not be persisted
         */
        public FieldSpecification doNotPersist()
        {
            return new FieldSpecification(name, type, length, false);
        }
    }
    
    /**
     * Creates an alphanumerical field that will be persisted
     */
    public static FieldSpecification field(String name, int length)
    {
        return new FieldSpecification(name, RecordFieldType.ALPHANUMERICAL, length, true);
    }

    private List<FieldSpecification> fields;
    
    private RecordSpecification(String table, String keyColumn)
    {
        this.table = table;
        this.keyColumn = keyColumn;
        
        fields = new ArrayList<FieldSpecification>();
    }
    
    public static RecordSpecification createSpecification(String tableName, String keyColumnName, FieldSpecification... fieldSpecifications)
    {
        RecordSpecification recordSpecification = new RecordSpecification(tableName, keyColumnName);
        
        recordSpecification.fields = Arrays.asList(fieldSpecifications);
        
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
        
        for (FieldSpecification fieldsSpecification: fields)
        {
            if(fieldsSpecification.persistField)
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
        }
        
        return true;
    }
}
