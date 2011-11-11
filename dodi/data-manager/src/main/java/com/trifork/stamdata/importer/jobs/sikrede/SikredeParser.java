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
import java.sql.SQLException;
import java.util.Iterator;

import com.trifork.stamdata.importer.persistence.Persister;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.joda.time.Instant;
import org.slf4j.MDC;

import com.trifork.stamdata.importer.config.KeyValueStore;
import com.trifork.stamdata.importer.jobs.FileParser;
import com.trifork.stamdata.importer.parsers.dkma.ParserException;
import com.trifork.stamdata.persistence.Record;
import com.trifork.stamdata.persistence.RecordPersister;
import com.trifork.stamdata.persistence.RecordSpecification;
import com.trifork.stamdata.persistence.RecordSpecification.SikredeType;

public class SikredeParser implements FileParser
{
    public static final String ACCEPTED_MODTAGER = "F053";
    public static final String ACCEPTED_SNITFLADE_ID = "S1061023";
    
    public static final String FILE_ENCODING = "ISO-8859-1";
    
    private static final String RECORD_TYPE_ENTRY = "10";
    private static final String RECORD_TYPE_END = "99";
    private static final String RECORD_TYPE_START = "00";
    
    static final RecordSpecification START_RECORD_RECORD_SPECIFICATION = RecordSpecification.newSikredeFields(
            "PostType", SikredeType.NUMERICAL, 2,
            "OpgDato", SikredeType.ALFANUMERICAL, 8,
            "Timestamp", SikredeType.ALFANUMERICAL, 20,
            "Modt", SikredeType.ALFANUMERICAL, 6,
            "SnitfladeId", SikredeType.ALFANUMERICAL, 8);
    
    static final RecordSpecification END_RECORD_RECORD_SPECIFICATION = RecordSpecification.newSikredeFields(
            "PostType", SikredeType.NUMERICAL, 2,
            "AntPost", SikredeType.NUMERICAL, 8);

    final SingleLineRecordParser startRecordParser = new SingleLineRecordParser(START_RECORD_RECORD_SPECIFICATION);
    final SingleLineRecordParser endRecordParser = new SingleLineRecordParser(END_RECORD_RECORD_SPECIFICATION);
    
    private final SingleLineRecordParser entryParser;
    private final RecordSpecification recordSpecification;
    private final String key;
    
    public SikredeParser(SingleLineRecordParser entryParser, RecordSpecification recordSpecification, String key)
    {
        this.entryParser = entryParser;
        this.recordSpecification = recordSpecification;
        this.key = key;
    }
    
    @Override
    public String getIdentifier() 
    {
        return "sikrede";
    }

    @Override
    public String getHumanName() 
    {
        return "\"Sikrede\" Parser";
    }

    @Override
    public boolean validateInputStructure(File[] input)
    {
        // FIXME: What are the expected names? We don't want to see unexpected filenames
        checkNotNull(input);
        return (input.length == 1);
    }

    @Override
    public void parse(File[] input, Persister oldPersister, KeyValueStore keyValueStore) throws Exception
    {
        checkArgument(input.length == 1, "Only one file is expected at this point.");
        File file = input[0];
        
        MDC.put("filename", file.getName());
        
        // FIXME: Check that files are imported in the right order.
        // We can not do this yet as we do not know what the files are named.
        
        RecordPersister persister = new RecordPersister(recordSpecification, oldPersister.getConnection());

        LineIterator lines = null;
        
        try
        {
            lines = FileUtils.lineIterator(file, FILE_ENCODING);
            importFile(lines, persister);
        }
        finally
        {
            LineIterator.closeQuietly(lines);
        }
    }
    
    private void importFile(Iterator<String> lines, RecordPersister persister) throws SQLException
    {
        // FIXME: If called two times tt would be different!!
        final Instant transactionTime = Instant.now();
        
        Record startRecord = null;
        Record endRecord = null;
        
        int numRecords = 0;
        
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
                if (!ACCEPTED_MODTAGER.equals(startRecord.get("Modt")))
                {
                    throw new ParserException(format("The receiver id '%s' did not match the expected '%s'.", startRecord.get("Modt"), ACCEPTED_MODTAGER));
                }
                
                if (!ACCEPTED_SNITFLADE_ID.equals(startRecord.get("SnitfladeId")))
                {
                    throw new ParserException(format("The interface id did not match the expected '%s'.", startRecord.get("SnitfladeId"), ACCEPTED_SNITFLADE_ID));
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

                Record record = entryParser.parseLine(line);
                persister.persistRecordWithValidityDate(record, key, transactionTime);
                
                numRecords++;
            }
            else
            {
                throw new ParserException("Unknown record type. line_content='" + line + "'");
            }
        }
        
        if (!endRecord.getField("AntPost").equals(numRecords))
        {
            throw new ParserException("The number of records that were parsed did not match the total from the end record.");
        }
    }
}
