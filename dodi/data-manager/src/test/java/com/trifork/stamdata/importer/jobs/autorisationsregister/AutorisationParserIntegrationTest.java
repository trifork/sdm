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


package com.trifork.stamdata.importer.jobs.autorisationsregister;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.sql.Connection;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.persistence.AuditingPersister;
import com.trifork.stamdata.importer.persistence.CompleteDataset;


public class AutorisationParserIntegrationTest
{
	private static final File valid = FileUtils.toFile(AutorisationParserIntegrationTest.class.getClassLoader().getResource("data/aut/valid/20090915AutDK.csv"));

	private AutorisationImporter importer = new AutorisationImporter();

	private Connection connection;

	@Before
	public void setUp() throws Exception
	{
		connection = MySQLConnectionManager.getConnection();
	}

	@After
	public void tearDown() throws Exception
	{
		connection.rollback();
		connection.close();
	}

	@Test
	public void should_return_true_if_expected_files_are_present()
	{
		File[] files = new File[] { valid };
		assertTrue(importer.ensureRequiredFileArePresent(files));
	}

	@Test
	public void should_return_false_if_no_file_are_present()
	{
		File[] file = new File[] {};
		assertFalse(importer.ensureRequiredFileArePresent(file));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void should_successfully_import_a_file() throws Exception
	{
		AuditingPersister persister = Mockito.spy(new AuditingPersister(connection));

		File[] files = new File[] { valid };
		importer.parse(files, persister, null);

		verify(persister).persistCompleteDataset(Mockito.any(CompleteDataset.class));
	}
}
