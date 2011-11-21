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

import static org.junit.Assert.*;

import org.junit.Test;

import com.trifork.stamdata.importer.jobs.sikrede.RecordGenerator;
import com.trifork.stamdata.specs.SikredeRecordSpecs;
import com.trifork.stamdata.specs.YderregisterRecordSpecs;

public class SchemaDumpTest {

    @Test
    public void test() 
    {
        
        System.out.println(RecordMySQLTableGenerator.createSqlSchema(SikredeRecordSpecs.ENTRY_RECORD_SPEC));
        System.out.println(RecordMySQLTableGenerator.createSqlSchema(YderregisterRecordSpecs.PERSON_RECORD_TYPE));
        System.out.println(RecordMySQLTableGenerator.createSqlSchema(YderregisterRecordSpecs.YDER_RECORD_TYPE));
    }

    @Test
    public void writeSikredeExamples()
    {
        
        
        RecordGenerator startRecordGenerator = new RecordGenerator(SikredeRecordSpecs.START_RECORD_SPEC);
        System.out.println(startRecordGenerator.stringFromIncompleteRecord(new RecordBuilder(SikredeRecordSpecs.START_RECORD_SPEC).field("PostType", 1).addDummyFieldsAndBuild()));
        
        RecordGenerator entryRecordGenerator = new RecordGenerator(SikredeRecordSpecs.ENTRY_RECORD_SPEC);
        System.out.println(entryRecordGenerator.stringFromIncompleteRecord(new RecordBuilder(SikredeRecordSpecs.ENTRY_RECORD_SPEC).field("PostType", 10).field("CPRnr", "1234567890").addDummyFieldsAndBuild()));
        
        RecordGenerator endRecordGenerator = new RecordGenerator(SikredeRecordSpecs.END_RECORD_SPEC);
        System.out.println(endRecordGenerator.stringFromIncompleteRecord(new RecordBuilder(SikredeRecordSpecs.END_RECORD_SPEC).field("PostType", 99).addDummyFieldsAndBuild()));
    }
}
