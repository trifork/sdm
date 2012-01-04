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
package com.trifork.stamdata.importer.jobs.yderregister;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import javax.inject.Provider;
import javax.xml.parsers.SAXParser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.trifork.stamdata.importer.config.KeyValueStore;
import com.trifork.stamdata.importer.parsers.exceptions.OutOfSequenceException;
import com.trifork.stamdata.importer.parsers.exceptions.ParserException;
import com.trifork.stamdata.persistence.RecordPersister;

public class YderregisterParserTest
{
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    private KeyValueStore keyValueStore;
    private YderregisterParser parser;
    private RecordPersister persister;
    private Provider<YderregisterSaxEventHandler> saxHandlerProvider;
    private YderregisterSaxEventHandler saxHandler;
    private SAXParser saxParser;

    @Before
    public void setUp() throws Exception
    {
        keyValueStore = mock(KeyValueStore.class);
        persister = mock(RecordPersister.class);
        
        saxHandlerProvider = mock(Provider.class);
        saxHandler = mock(YderregisterSaxEventHandler.class);
        saxParser = mock(SAXParser.class);

        when(saxHandlerProvider.get()).thenReturn(saxHandler);
        
        parser = new YderregisterParser(keyValueStore, saxParser, saxHandlerProvider);
    }

    @Test(expected = OutOfSequenceException.class)
    public void testImportingOldVersionWillResultInAnOutOfSequenceException() throws Exception
    {
        when(keyValueStore.get("version")).thenReturn("00002");
        File fileSet = createFileSet("00001");

        parser.process(fileSet, persister);
    }

    @Test(expected = OutOfSequenceException.class)
    public void testImportingCurrentVersionResultInAnOutOfSequenceException() throws Exception
    {
        when(keyValueStore.get("version")).thenReturn("00002");
        File fileSet = createFileSet("00002");

        parser.process(fileSet, persister);
    }

    @Test(expected = ParserException.class)
    public void testMissingFilesResultsInAParserException() throws Exception
    {
        File fileSet = createFileSet("00001");
        fileSet.listFiles()[0].delete();

        parser.process(fileSet, persister);
    }

    @Test
    public void testStoresTheVersionFromStartRecordCorrectly() throws Exception
    {
        String version = "00031";

        File fileSet = createFileSet(version);

        parser.process(fileSet, persister);

        verify(keyValueStore).put("version", version);
    }

    @Test
    public void testThatAllFilesArePassedToTheParser() throws Exception
    {
        File fileSet = createFileSet("00001");

        parser.process(fileSet, persister);

        for (File file : fileSet.listFiles())
        {
            verify(saxParser).parse(file, saxHandler);
        }
    }

    //
    // Helpers
    //

    public File createFileSet(String filename, String version) throws IOException
    {
        File root = folder.newFolder("root");

        File file = new File(root, filename);
        file.createNewFile();
        
        when(saxHandler.GetVersionFromFileSet()).thenReturn(version);
        
        return root;
    }

    public File createFileSet(String version) throws IOException
    {
        return createFileSet("M.S1040025.SB025.xml", version);
    }
}
