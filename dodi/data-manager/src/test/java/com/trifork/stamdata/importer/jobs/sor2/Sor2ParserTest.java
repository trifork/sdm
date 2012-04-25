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


package com.trifork.stamdata.importer.jobs.sor2;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import javax.inject.Provider;
import javax.xml.parsers.SAXParser;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.trifork.stamdata.importer.jobs.sor.sor2.SORFullEventHandler;
import com.trifork.stamdata.importer.jobs.sor.sor2.SORXmlParser;
import com.trifork.stamdata.persistence.RecordPersister;

public class Sor2ParserTest
{

	@Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private SORXmlParser parser;
    private RecordPersister persister;
    private Provider<SORFullEventHandler> saxHandlerProvider;
    private SORFullEventHandler saxHandler;
    private SAXParser saxParser;

	private File inbox1;
	private File inbox2;

	public File getFile(String path)
	{
		return FileUtils.toFile(getClass().getClassLoader().getResource(path));
	}

	@Before
	public void setUp() throws Exception
	{
		File full1 = getFile("data/sor/SOR_FULL.xml");
		File full2 = getFile("data/sor/SOR_FULL2.xml");
		
		inbox1 = temporaryFolder.newFolder("full1");
		inbox2 = temporaryFolder.newFolder("full2");
		
		FileUtils.copyFileToDirectory(full1, inbox1);
		FileUtils.copyFileToDirectory(full2, inbox2);
		
        persister = mock(RecordPersister.class);
        
        saxHandlerProvider = mock(Provider.class);
        saxHandler = mock(SORFullEventHandler.class);
        saxParser = mock(SAXParser.class);

        when(saxHandlerProvider.get()).thenReturn(saxHandler);
        
        parser = new SORXmlParser(saxParser, saxHandlerProvider);
    }

	@Test
	public void testParser1() throws Exception
	{
		long timeStart = System.currentTimeMillis();
		parser.process(inbox1, persister);
		
		long timeEnd = System.currentTimeMillis();
		System.out.println("full1 file took: " + (timeEnd-timeStart) + " ms");
		
		timeStart = System.currentTimeMillis();
		parser.process(inbox2, persister);
		timeEnd = System.currentTimeMillis();
		System.out.println("full2 file took: " + (timeEnd-timeStart) + " ms");
	}

}
