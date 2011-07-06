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

package com.trifork.stamdata.importer.parsers.yderregister;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.parsers.exceptions.FilePersistException;


/*
 * Tests that an import of an yderregister can be performed correctly.
 *
 * @author Anders Bo Christensen
 */
public class YderregisterIntegrationTest
{
	private Connection con;

	public File getFile(String path)
	{
		return FileUtils.toFile(getClass().getClassLoader().getResource(path));
	}

	@Before
	public void cleanDatabase() throws Exception
	{
		con = MySQLConnectionManager.getConnection();

		Statement stmt = con.createStatement();
		stmt.execute("truncate table Yderregister");
		stmt.execute("truncate table YderregisterPerson");
		stmt.execute("truncate table YderLoebenummer");
		stmt.close();
	}

	@After
	public void tearDown() throws SQLException
	{
		con.rollback();
		con.close();
	}

	@Test
	public void ImportTest() throws Exception
	{
		File fInitial = getFile("data/yderregister/initial/");
		List<File> files = Arrays.asList((fInitial.listFiles()));

		new YderregisterImporter().run(files, con);

		Statement stmt = con.createStatement();

		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Yderregister;");

		rs.next();

		assertEquals("Samlet antal yderere", 12, rs.getInt(1));

		rs = stmt.executeQuery("SELECT * FROM Yderregister WHERE Nummer = 4219;");

		assertFalse("Fandt ikke den forventede yder med ydernummer 4219", rs.next());

		assertEquals("Yders Navn", "Jørgen Vagn Nielsen", rs.getString("Navn"));
		assertEquals("Yders Vejnavn", "Store Kongensgade 96,4.", rs.getString("Vejnavn"));
		assertEquals("Yders Telefon", "33112299", rs.getString("Telefon"));
		assertEquals("Yders Postnummer", "1264", rs.getString("Postnummer"));
		assertEquals("Yders Bynavn", "København K", rs.getString("Bynavn"));
		assertEquals("Yders Amtsnummer", 84, rs.getInt("AmtNummer"));
		assertEquals("Yders Email", "klinik@33112299.dk", rs.getString("Email"));
		assertEquals("Yders www", "www.plib.dk", rs.getString("Www"));
		assertEquals("Yders www", "1978-07-01 00:00:00.0", rs.getString("ValidFrom"));
		assertEquals("Yders www", "2999-12-31 00:00:00.0", rs.getString("ValidTo"));
		stmt.close();
	}

	@Test(expected = FilePersistException.class)
	public void LoebenummerTest() throws Exception
	{
		File fInitial = getFile("data/yderregister/initial/");
		File fNext = getFile("data/yderregister/nextversion/");

		List<File> files = Arrays.asList((fInitial.listFiles()));

		new YderregisterImporter().run(files, con);

		files = Arrays.asList((fNext.listFiles()));

		new YderregisterImporter().run(files, con);
	}

	@Test
	public void CompleteFilesTest() throws Exception
	{
		File fInitial = getFile("data/yderregister/initial/");

		YderregisterImporter yImp = new YderregisterImporter();
		boolean isComplete = yImp.checkRequiredFiles(Arrays.asList(fInitial.listFiles()));

		assertTrue("Expected to be complete", isComplete);
	}

	@Test
	public void IncompleteFilesTest() throws Exception
	{
		File fInitial = getFile("data/yderregister/incomplete/");

		YderregisterImporter yImp = new YderregisterImporter();
		boolean isComplete = yImp.checkRequiredFiles(Arrays.asList(fInitial.listFiles()));

		assertEquals("Expected to be incomplete", false, isComplete);
	}
}
