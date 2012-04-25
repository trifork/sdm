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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import javax.inject.Provider;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.trifork.stamdata.importer.config.ConnectionManager;
import com.trifork.stamdata.importer.config.KeyValueStore;
import com.trifork.stamdata.importer.jobs.sikrede.SikredeParser;
import com.trifork.stamdata.importer.jobs.sor.sor2.SORFullImporter;
import com.trifork.stamdata.importer.jobs.sor.sor2.SORXmlParser;
import com.trifork.stamdata.persistence.RecordMySQLTableGenerator;
import com.trifork.stamdata.persistence.RecordSpecification;

public class Sor2ParserTest
{

	@Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

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
    }

	@Test
	public void testParser1() throws Exception
	{
		long timeStart = System.currentTimeMillis();
		SORXmlParser sorXmlParser = new SORXmlParser();
		sorXmlParser.process(inbox1, null);
		
		long timeEnd = System.currentTimeMillis();
		System.out.println("full1 file took: " + (timeEnd-timeStart) + " ms");
		
		timeStart = System.currentTimeMillis();
		sorXmlParser.process(inbox2, null);
		timeEnd = System.currentTimeMillis();
		System.out.println("full2 file took: " + (timeEnd-timeStart) + " ms");
	}

}
