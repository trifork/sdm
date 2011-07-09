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

package com.trifork.stamdata.importer.jobs.autorisationsregister;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.trifork.stamdata.importer.jobs.autorisationsregister.AutorisationImporter;
import com.trifork.stamdata.importer.persistence.AuditingPersister;
import com.trifork.stamdata.importer.persistence.CompleteDataset;


public class AutImporterTest
{
	private static final File valid = FileUtils.toFile(AutImporterTest.class.getClassLoader().getResource("data/aut/valid/20090915AutDK.csv"));
	
	private AutorisationImporter importer = new AutorisationImporter();

	@Mock
	private AuditingPersister persister;
	
	@Test
	public void testAreRequiredInputFilesPresent()
	{
		File[] files = new File[] {valid};
		assertTrue(importer.ensureRequiredFileArePresent(files));
	}

	public void should_return_false_if_no_file_are_present()
	{
		File[] file = new File[] {};
		assertFalse(importer.ensureRequiredFileArePresent(file));
	}

	@Test
	public void testImport() throws Exception
	{
		File[] files = new File[] {valid};
		importer.importFiles(files, persister);
		
		verify(persister).persistCompleteDataset(Mockito.any(CompleteDataset.class));
	}

	@Test
	public void testGetDateFromFileName() throws ParseException
	{
		AutorisationImporter importer = new AutorisationImporter();
		Date date = importer.getDateFromFilename("19761110sgfdgfg");
		assertEquals("19761110", new SimpleDateFormat("yyyyMMdd").format(date));
	}
}
