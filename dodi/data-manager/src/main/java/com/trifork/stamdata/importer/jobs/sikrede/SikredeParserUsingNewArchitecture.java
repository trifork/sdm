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

import java.io.File;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.joda.time.DateTime;
import org.slf4j.MDC;

import com.trifork.stamdata.importer.config.KeyValueStore;
import com.trifork.stamdata.importer.jobs.FileParser;
import com.trifork.stamdata.importer.parsers.dkma.ParserException;
import com.trifork.stamdata.importer.persistence.Persister;
import com.trifork.stamdata.persistence.SikredeFields;
import com.trifork.stamdata.persistence.SikredeRecord;
import com.trifork.stamdata.persistence.SikredeFields.SikredeType;

public class SikredeParserUsingNewArchitecture implements FileParser {

    static final String ACCEPTED_MODTAGER = "F053";
    static final String ACCEPTED_SNITFLADE_ID = "S1061023";
    
    private static final String RECORD_TYPE_ENTRY = "10";
    private static final String RECORD_TYPE_END = "99";
    private static final String RECORD_TYPE_START = "00";
    static final String FILE_ENCODING = "ISO-8859-1";
    
    static final SikredeFields startRecordSikredeFields = SikredeFields.newSikredeFields(
            "PostType", SikredeType.NUMERICAL, 2,
            "OpgDato", SikredeType.ALFANUMERICAL, 8,
            "Timestamp", SikredeType.ALFANUMERICAL, 20,
            "Modt", SikredeType.ALFANUMERICAL, 6,
            "SnitfladeId", SikredeType.ALFANUMERICAL, 8);
    static final SikredeLineParser startRecordParser = new SikredeLineParser(startRecordSikredeFields);

    static final SikredeFields endRecordSikredeFields = SikredeFields.newSikredeFields(
            "PostType", SikredeType.NUMERICAL, 2,
            "AntPost", SikredeType.NUMERICAL, 8);
    static final SikredeLineParser endRecordParser = new SikredeLineParser(endRecordSikredeFields);
    
    private final SikredeLineParser entryParser;
    private SikredeFields sikredeFields;
    private final String key;
    
    public SikredeParserUsingNewArchitecture(SikredeLineParser entryParser, SikredeFields sikredeFields, String key)
    {
        this.entryParser = entryParser;
        this.sikredeFields = sikredeFields;
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
    public boolean ensureRequiredFileArePresent(File[] input) 
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
        
        // FIXME: Check that files are imported in the right order. We can not do this yet as we do not know what the files are named
        
        SikredePersisterUsingNewArchitecture persister = new SikredePersisterUsingNewArchitecture(sikredeFields, oldPersister);
        
        LineIterator lines = null;
        
        try
        {
            lines = FileUtils.lineIterator(file, FILE_ENCODING);
            importFile(lines, persister);
        }
        catch (Exception e)
        {
            throw new ParserException("An error occured while parsing the sikrede file.", e);
        }
        finally
        {
            LineIterator.closeQuietly(lines);
        }
    }
    
    private void importFile(Iterator<String> lines, SikredePersisterUsingNewArchitecture persister) throws SQLException
    {       
        DateTime timestampOfInsertion = new DateTime();
        
        SikredeRecord startRecord = null;
        SikredeRecord endRecord = null;
        
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
                
                // FIXME: Verificer Modt og SnitfladeId jf. dokumentation
                if(!ACCEPTED_MODTAGER.equals(startRecord.get("Modt")))
                {
                    throw new ParserException("The \"Modt\" field of the start record did not match the accepted: " + ACCEPTED_MODTAGER + ", but was " + startRecord.get("Modt"));
                }
                
                if(!ACCEPTED_SNITFLADE_ID.equals(startRecord.get("SnitfladeId")))
                {
                    throw new ParserException("The \"SnitfladeId\" field of the start record did not match the accepted: " + ACCEPTED_SNITFLADE_ID + ", but was " + startRecord.get("SnitfladeId"));
                }

            }
            else if (line.startsWith(RECORD_TYPE_END))
            {
                if (startRecord == null)
                {
                    throw new ParserException("Start record was not found before end record.");
                }
                
                endRecord = endRecordParser.parseLine(line);
            }
            else if (line.startsWith(RECORD_TYPE_ENTRY))
            {
                if (startRecord == null)
                {
                    throw new ParserException("Start record was not found before first entry.");
                }
                
                SikredeRecord record = entryParser.parseLine(line);
                persister.persistRecordWithValidityDate(record, key, timestampOfInsertion);
                
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
