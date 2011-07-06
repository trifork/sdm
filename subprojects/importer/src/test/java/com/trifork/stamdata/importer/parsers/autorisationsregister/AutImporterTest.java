// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
//
// Contributor(s): Contributors are attributed in the source code
// where applicable.
//
// The Original Code is "Stamdata".
//
// The Initial Developer of the Original Code is Trifork Public A/S.
//
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
//
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.importer.parsers.autorisationsregister;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;


public class AutImporterTest
{
	public File valid;
	private AutImporter importer;

	@Before
	public void Setup()
	{
		valid = FileUtils.toFile(getClass().getClassLoader().getResource("data/aut/valid/20090915AutDK.csv"));
		importer = new AutImporter();
	}

	@Test
	public void testAreRequiredInputFilesPresent()
	{
		List<File> files = new ArrayList<File>();

		// one good file

		files = new ArrayList<File>();
		files.add(valid);

		assertTrue(importer.checkRequiredFiles(files));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testShouldRequireFiles()
	{
		List<File> files = new ArrayList<File>();

		// empty set

		assertFalse(importer.checkRequiredFiles(files));
	}

	/* TODO: Reenable
	@Test
	@SuppressWarnings("unchecked")
	public void testImport() throws Exception
	{
		List<File> files = new ArrayList<File>();
		files.add(valid);
		AuditingPersister daoMock = mock(AuditingPersister.class);
		importer.doImport(files, daoMock);
		verify(daoMock).persistCompleteDataset(any(CompleteDataset.class));
	}
	*/

	@Test
	public void testGetDateFromFileName() throws ParseException
	{
		AutImporter importer = new AutImporter();
		Date date = importer.getDateFromFilename("19761110sgfdgfg");
		assertEquals("19761110", new SimpleDateFormat("yyyyMMdd").format(date));
	}
}
