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
import static java.lang.String.format;

import java.io.File;
import java.sql.Connection;
import java.util.Iterator;

import com.trifork.stamdata.importer.parsers.Parser;
import com.trifork.stamdata.importer.parsers.ParserException;
import com.trifork.stamdata.importer.parsers.annotations.ParserInformation;
import com.trifork.stamdata.specs.SikredeRecordSpecs;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.joda.time.Instant;
import org.slf4j.MDC;

import com.trifork.stamdata.persistence.Record;
import com.trifork.stamdata.persistence.RecordPersister;
import com.trifork.stamdata.persistence.RecordSpecification;

import javax.inject.Inject;

@ParserInformation(id="sikrede", name="\"Sikrede\"")
public class SikredeParser implements Parser
{
    public static final String ACCEPTED_RECIPIENT_ID = "F053";
    public static final String ACCEPTED_INTERFACE_ID = "S1061023";
    
    public static final String FILE_ENCODING = "ISO-8859-1";

    private static final String RECORD_TYPE_START = "00";
    private static final String RECORD_TYPE_ENTRY = "10";
    private static final String RECORD_TYPE_END = "99";
    
    private final SingleLineRecordParser recordParser;
    private final RecordSpecification recordSpecification;
    private final String key;

    @Inject
    SikredeParser()
    {
        recordSpecification = SikredeRecordSpecs.ENTRY_RECORD_SPEC;
        recordParser = new SingleLineRecordParser(recordSpecification);
        key = "CPRnr";
    }

    /** For testing only */
    SikredeParser(SingleLineRecordParser recordParser, RecordSpecification recordSpecification, String key)
    {
        this.recordParser = recordParser;
        this.recordSpecification = recordSpecification;
        this.key = key;
    }

    @Override
    public void process(File dataSet, Connection connection, Instant transactionTime) throws Exception
    {
        RecordPersister persister = new RecordPersister(connection, transactionTime);

        File[] input = dataSet.listFiles();
        checkArgument(input.length == 1, "Only one file is expected at this point.");
        File file = input[0];
        
        MDC.put("filename", file.getName());
        
        // FIXME: Check that files are imported in the right order.
        // We can not do this yet as we do not know what the files are named.

        LineIterator lines = null;
        
        try
        {
            lines = FileUtils.lineIterator(file, FILE_ENCODING);
            importFile(lines, persister, transactionTime);
        }
        finally
        {
            LineIterator.closeQuietly(lines);
        }
    }
    
    private void importFile(Iterator<String> lines, RecordPersister persister, Instant transactionTime) throws Exception
    {
        Record startRecord = null;
        Record endRecord = null;
        
        int numRecords = 0;

        SingleLineRecordParser startRecordParser = new SingleLineRecordParser(SikredeRecordSpecs.START_RECORD_SPEC);
        SingleLineRecordParser endRecordParser = new SingleLineRecordParser(SikredeRecordSpecs.END_RECORD_SPEC);
        
        for (String line; lines.hasNext();)
        {
            if (endRecord != null)
            {
                throw new ParserException("Lines found after End Record.");
            }
            
            line = lines.next();
            
            if (line.startsWith(RECORD_TYPE_START))
            {
                if (startRecord != null)
                {
                    throw new ParserException("Several Start Records were found.");
                }
                
                startRecord = startRecordParser.parseLine(line);
                
                // FIXME: Verificer Modt og SnitfladeId jf. dokumentation.
                //
                if (!ACCEPTED_RECIPIENT_ID.equals(startRecord.get("Modt")))
                {
                    throw new ParserException(format("The receiver id '%s' did not match the expected '%s'.", startRecord.get("Modt"), ACCEPTED_RECIPIENT_ID));
                }
                
                if (!ACCEPTED_INTERFACE_ID.equals(startRecord.get("SnitfladeId")))
                {
                    throw new ParserException(format("The interface id did not match the expected '%s'.", startRecord.get("SnitfladeId"), ACCEPTED_INTERFACE_ID));
                }
            }
            else if (line.startsWith(RECORD_TYPE_END))
            {
                if (startRecord == null) throw new ParserException("Start record was not found before end record.");
                
                endRecord = endRecordParser.parseLine(line);
            }
            else if (line.startsWith(RECORD_TYPE_ENTRY))
            {
                if (startRecord == null) throw new ParserException("Start record was not found before first entry.");

                Record record = recordParser.parseLine(line);
                persister.persist(record, recordSpecification);
                
                numRecords++;
            }
            else
            {
                throw new ParserException("Unknown record type. line_content='" + line + "'");
            }
        }
        
        if (!endRecord.get("AntPost").equals(numRecords))
        {
            throw new ParserException("The number of records that were parsed did not match the total from the end record.");
        }
    }
}
