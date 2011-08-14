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

import static com.trifork.stamdata.Helpers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.sql.*;

import org.apache.commons.io.FileUtils;
import org.junit.*;

import com.trifork.stamdata.Helpers;
import com.trifork.stamdata.importer.persistence.*;


public class AutorisationParserTest
{
	private AutorisationsregisterParser parser = new AutorisationsregisterParser(FAKE_TIME_GAP);

	private Connection connection;

	private File[] next;
	private File[] initial;
	private File[] invalid;

	@Before
	public void init()
	{
		connection = Helpers.getConnection();

		ClassLoader classLoader = getClass().getClassLoader();

		initial = new File[] { FileUtils.toFile(classLoader.getResource("data/aut/valid/20090915AutDK.csv")) };
		next = new File[] { FileUtils.toFile(classLoader.getResource("data/aut/valid/20090918AutDK.csv")) };
		invalid = new File[] { FileUtils.toFile(classLoader.getResource("data/aut/invalid/20090915AutDK.csv")) };
	}

	@After
	public void tearDown() throws SQLException
	{
		connection.rollback();
		connection.close();
	}

	@Test
	public void testImport() throws Exception
	{
		parser.run(initial, null, connection, 0);

		ResultSet rs = connection.createStatement().executeQuery("SELECT COUNT(*) AS count FROM " + Dataset.getEntityTypeDisplayName(Autorisation.class));
		rs.next();

		assertEquals("Number of records in database.", 4, rs.getInt("count"));

		rs = connection.createStatement().executeQuery("SELECT * FROM Autorisation ORDER BY PID");

		rs.next();
		assertEquals(rs.getString("Autorisationsnummer"), "0013F");
		assertEquals(rs.getString("CPR"), "0101251489");
		assertEquals(rs.getString("Fornavn"), "Jørgen");
		assertEquals(rs.getString("Efternavn"), "Bondo");
		assertEquals(rs.getString("Uddannelseskode"), "7170");

		rs.next();
		assertEquals(rs.getString("Autorisationsnummer"), "0013H");
		assertEquals(rs.getString("CPR"), "0101280063");
		assertEquals(rs.getString("Fornavn"), "Tage Søgaard");
		assertEquals(rs.getString("Efternavn"), "Johnsen");
		assertEquals(rs.getString("Uddannelseskode"), "7170");

		rs.next();
		assertEquals(rs.getString("Autorisationsnummer"), "0013J");
		assertEquals(rs.getString("CPR"), "0101280551");
		assertEquals(rs.getString("Fornavn"), "Svend Christian");
		assertEquals(rs.getString("Efternavn"), "Bertelsen");
		assertEquals(rs.getString("Uddannelseskode"), "7170");

		rs.next();
		assertEquals(rs.getString("Autorisationsnummer"), "0013K");
		assertEquals(rs.getString("CPR"), "0101280896");
		assertEquals(rs.getString("Fornavn"), "Lilian");
		assertEquals(rs.getString("Efternavn"), "Frederiksen");
		assertEquals(rs.getString("Uddannelseskode"), "7170");
	}

	@Test
	public void should_return_true_if_expected_files_are_present()
	{
		assertTrue(parser.checkFileSet(initial));
	}

	@Test
	public void should_return_false_if_no_file_are_present()
	{
		File[] file = new File[] {};
		assertFalse(parser.checkFileSet(file));
	}

	@Test
	public void should_keep_the_STS_table_updated_after_each_import() throws Exception
	{
		parser.run(initial, null, connection, 0);

		ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM autreg ORDER BY aut_id");
		rs.next();
		assertEquals(rs.getString("aut_id"), "0013F");
		rs.next();
		assertEquals(rs.getString("aut_id"), "0013H");
		rs.next();
		assertEquals(rs.getString("aut_id"), "0013J");
		rs.next();
		assertEquals(rs.getString("aut_id"), "0013K");

		// Update the registry with a new file.
		
		parser.run(next, null, connection, 1);
		
		rs = connection.createStatement().executeQuery("SELECT * FROM autreg ORDER BY aut_id");
		rs.next();
		assertEquals(rs.getString("aut_id"), "0013H");
		rs.next();
		assertEquals(rs.getString("aut_id"), "0013J");
		rs.next();
		assertEquals(rs.getString("aut_id"), "0013K");
		rs.next();
		assertEquals(rs.getString("aut_id"), "0013L");
		rs.next();
		assertEquals(rs.getString("aut_id"), "0013M");
	}

	@Test(expected = Exception.class)
	public void should_not_allow_the_same_version_to_be_imported_twice() throws Exception
	{
		parser.run(initial, null, connection, 0);
		parser.run(initial, null, connection, 0);
	}

	@Test(expected = Exception.class)
	public void should_fail_to_persist_invalid_file() throws Exception
	{
		parser.run(invalid, null, connection, 0);
	}
}
