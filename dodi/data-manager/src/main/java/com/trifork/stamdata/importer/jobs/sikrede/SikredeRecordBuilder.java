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

import static com.trifork.stamdata.Preconditions.checkArgument;
import static com.trifork.stamdata.Preconditions.checkNotNull;

import com.trifork.stamdata.importer.jobs.sikrede.SikredeFields.SikredeFieldSpecification;
import com.trifork.stamdata.importer.jobs.sikrede.SikredeFields.SikredeType;
import com.trifork.stamdata.persistence.SikredeRecord;

public class SikredeRecordBuilder {

    private SikredeFields sikredeFields;
    private SikredeRecord sikredeRecord;
    
    public SikredeRecordBuilder(SikredeFields sikredeFields)
    {
        this.sikredeFields = sikredeFields;
        sikredeRecord = new SikredeRecord();
    }
    
    public SikredeRecordBuilder field(String fieldName, int value)
    {
        return field(fieldName, value, SikredeType.NUMERICAL);
    }
    
    public SikredeRecordBuilder field(String fieldName, String value)
    {
        return field(fieldName, value, SikredeType.ALFANUMERICAL);
    }
    
    private SikredeRecordBuilder field(String fieldName, Object value, SikredeType sikredeType)
    {
        checkNotNull(fieldName);
        checkArgument(getFieldType(fieldName) == sikredeType, "Field " + fieldName + " is not " + sikredeType);
        
        sikredeRecord = sikredeRecord.withField(fieldName, value);
        
        return this;
    }
    
    public SikredeRecord build()
    {
        if(sikredeFields.conformsToSpecifications(sikredeRecord))
        {
            return sikredeRecord;
        }
        else
        {
            throw new IllegalStateException("Mandatory fields not set");
        }
    }
    
    private SikredeType getFieldType(String fieldName)
    {
        for(SikredeFieldSpecification fieldSpecification: sikredeFields.getFieldSpecificationsInCorrectOrder())
        {
            if(fieldSpecification.name.equals(fieldName))
            {
                return fieldSpecification.type;
            }
        }
        return null;
    }
}
