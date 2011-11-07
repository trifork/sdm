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
package com.trifork.stamdata.importer.parsers.dkma;

import static java.lang.String.format;

import java.io.File;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.trifork.stamdata.importer.config.KeyValueStore;
import com.trifork.stamdata.importer.parsers.Parser;
import com.trifork.stamdata.importer.parsers.dkma.SystemFile.FileDescriptor;
import com.trifork.stamdata.importer.persistence.Persister;
import com.trifork.stamdata.importer.util.Files;

public class DkmaParser implements Parser
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DkmaParser.class);
    
    private static final String PREFIX_CURRENT_VERSION = "current_version_for_";
    
    private static final String SUPPORTED_INTERFACE_VERSION = "12.0";
    
    private final Persister persister;
    private final KeyValueStore keyValueStore;
    private final FileParserFactory parserFactory;
    
    @Inject
    DkmaParser(Persister persister, KeyValueStore keyValueStore, FileParserFactory parserFactory)
    {
        this.persister = persister;
        this.keyValueStore = keyValueStore;
        this.parserFactory = parserFactory;
    }
    
    @Override
    public void process(File inputDirectory)
    {
        // The system.txt file holds the metadata for
        // the import.
        //
        SystemFile systemFile = parseSystemFile(inputDirectory);
        
        // Make sure everything is as we expect.
        //
        enforceInterfaceVersion(systemFile);
        enforceImportSequence(systemFile);
        
        // The loop will also check for the presence of
        // all the files mentioned in the system.txt file.
        //
        for (FileDescriptor fileInfo : systemFile.getFiles())
        {
            MDC.put("filename", fileInfo.getFilename());
            
            LOGGER.info("Started processing.");
            
            FileParser fileParser = parserFactory.create(fileInfo.getFilename());
            File file = Files.getFile(inputDirectory, fileInfo.getFilename(), true);
            
            int numProcessedRecords = fileParser.parse(file, persister);
            
            if (numProcessedRecords != fileInfo.getNumRecords())
            {
                throw new ParserException(format("Not all records could be parsed. expected=%d, actual=%d", fileInfo.getNumRecords(), numProcessedRecords));
            }
            
            LOGGER.info("Processing complete.");
        }
    }

    private void enforceImportSequence(SystemFile systemFile)
    {
        LOGGER.info("Enforcing import sequence.");

        String currentVersion = keyValueStore.get(getVersionKey(systemFile));
        String newVersion = systemFile.getCreationDate();

        if (currentVersion == null)
        {
            LOGGER.info("Importing initial version of week {}.", systemFile.getValidityWeek());
        }
        else if (newVersion.compareTo(currentVersion) > 0)
        {
            LOGGER.info("Updating existing version of week {}.", systemFile.getValidityWeek());
        }
        else
        {
            throw new OutOfSequenceException(currentVersion, newVersion);
        }
    }
    
    public void updateWeekVersion(SystemFile systemFile)
    {
        LOGGER.info("Updating version number for week {} to {}.", systemFile.getValidityWeek(), systemFile.getCreationDate());
        
        keyValueStore.put(getVersionKey(systemFile), systemFile.getCreationDate());
    }
    
    private String getVersionKey(SystemFile systemFile)
    {
        String validityWeek = systemFile.getValidityWeek();
        return PREFIX_CURRENT_VERSION + validityWeek;
    }
    
    private void enforceInterfaceVersion(SystemFile systemFile)
    {
        String datasetInterface = systemFile.getInterfaceVersion();
        
        if (!SUPPORTED_INTERFACE_VERSION.equals(datasetInterface))
        {
            throw new ParserException(format("Unsupported interface version. found=%s expected=%s", datasetInterface, SUPPORTED_INTERFACE_VERSION));
        }
    }
    
    private SystemFile parseSystemFile(File root)
    {
        File file = Files.getFile(root, "system.txt", true);
        return new SystemFile(file);
    }
}
