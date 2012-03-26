package com.trifork.stamdata.specs;

import static com.trifork.stamdata.persistence.RecordSpecification.field;

import com.trifork.stamdata.persistence.RecordSpecification;

public final class BemyndigelseRecordSpecs
{
    protected BemyndigelseRecordSpecs() {}
    
    public static final RecordSpecification START_RECORD_SPEC = RecordSpecification.createSpecification("DummyTable", "DummyKey", 
            field("PostType", 2).numerical().doNotPersist(),
            field("Dato", 8),
            field("Timestamp", 20),
            field("Version", 6),
            field("SnitfladeId", 8));
    
    public static final RecordSpecification END_RECORD_SPEC = RecordSpecification.createSpecification("DummyTable", "DummyKey", 
            field("PostType", 2).numerical().doNotPersist(),
            field("AntalPost", 8).numerical());
    
    public static final RecordSpecification ENTRY_RECORD_SPEC = RecordSpecification.createSpecification("Bemyndigelse", "kode", 
            field("PostType", 2).numerical().doNotPersist(),
            field("CPRnr", 10),
            field("bemyndigende_cpr", 10),
            field("bemyndigede_cpr", 10),
            field("bemyndigede_cvr", 8),
            field("system", 255),
            field("arbejdsfunktion", 255),
            field("rettighed", 255),
            field("status", 255),
            field("godkendelsesdato", 8),

            field("CreatedDate", 8),
            field("ModifiedDate", 8),
            field("ValidFrom", 8),
            field("ValidTo", 8));
}
