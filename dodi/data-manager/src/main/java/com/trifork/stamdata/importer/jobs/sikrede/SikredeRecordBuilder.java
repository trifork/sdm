package com.trifork.stamdata.importer.jobs.sikrede;

import static com.trifork.stamdata.Preconditions.checkArgument;
import static com.trifork.stamdata.Preconditions.checkNotNull;

import com.trifork.stamdata.importer.jobs.sikrede.SikredeFields.SikredeFieldSpecification;
import com.trifork.stamdata.importer.jobs.sikrede.SikredeFields.SikredeType;

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
        
        sikredeRecord = sikredeRecord.setField(fieldName, value);
        
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
