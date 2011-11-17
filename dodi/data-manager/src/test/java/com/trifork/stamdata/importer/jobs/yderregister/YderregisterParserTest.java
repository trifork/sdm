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

import com.trifork.stamdata.importer.config.KeyValueStore;
import com.trifork.stamdata.importer.parsers.OutOfSequenceException;
import com.trifork.stamdata.importer.parsers.ParserException;
import com.trifork.stamdata.persistence.RecordPersister;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;

public class YderregisterParserTest
{
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    private KeyValueStore keyValueStore;
    private YderregisterParser parser;
    private RecordPersister persister;

    @Before
    public void setUp() throws Exception
    {
        keyValueStore = mock(KeyValueStore.class);
        persister = mock(RecordPersister.class);
        
        parser = new YderregisterParser(keyValueStore);
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

    @Test
    public void testAFileSetWithMultipleVersionNumbersResultsInAParserException()
    {

    }

    @Test(expected = ParserException.class)
    public void testMissingFilesResultsInAParserException() throws Exception
    {
        File fileSet = createFileSet("00001");
        fileSet.listFiles()[0].delete();

        parser.process(fileSet, persister);
    }

    @Test
    public void testStoresTheVersionFromFileNamesCorrectly() throws Exception
    {
        String version = "00031";

        File fileSet = createFileSet("00031");

        parser.process(fileSet, persister);

        verify(keyValueStore).put("version", version);
    }

    //
    // Helpers
    //

    public File createFileSet(String ...filenames) throws IOException
    {
        File root = folder.newFolder("root");

        for (String filename : filenames)
        {
            new File(root, filename).createNewFile();
        }

        return root;
    }

    public File createFileSet(String version) throws IOException
    {
        return createFileSet(getRequiredFilenames(version));
    }

    public String[] getRequiredFilenames(String version)
    {
        int i = 0;

        String[] filenames = new String[5];

        for (String extension : new String[] {"K05", "K40", "K45", "K1025", "K5094"})
        {
            // FIXME: What is the first part of the filename? A recipient id maybe?

            filenames[i++] = "SSR1040013" + version + "." + extension + ".xml";
        }

        return filenames;
    }
}
