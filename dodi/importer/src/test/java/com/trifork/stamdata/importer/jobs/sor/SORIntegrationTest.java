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

package com.trifork.stamdata.importer.jobs.sor;

import static com.trifork.stamdata.Helpers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.sql.*;

import org.apache.commons.io.FileUtils;
import org.junit.*;

import com.trifork.stamdata.Helpers;
import com.trifork.stamdata.importer.persistence.*;


public class SORIntegrationTest
{
	public static File onePraksis;
	public static File oneSygehus;
	public static File oneApotek;
	public static File fullSor;

	private Connection connection;

	@BeforeClass
	public static void init()
	{
		ClassLoader classLoader = SORIntegrationTest.class.getClassLoader();

		onePraksis = FileUtils.toFile(classLoader.getResource("data/sor/ONE_PRAKSIS.xml"));
		oneSygehus = FileUtils.toFile(classLoader.getResource("data/sor/ONE_SYGEHUS.xml"));
		oneApotek = FileUtils.toFile(classLoader.getResource("data/sor/ONE_APOTEK.xml"));
		fullSor = FileUtils.toFile(classLoader.getResource("data/sor/SOR_FULL.xml"));
	}

	@Before
	public void setUp() throws Exception
	{
		connection = Helpers.getConnection();

		Statement stmt = connection.createStatement();
		stmt.executeQuery("truncate table Praksis");
		stmt.executeQuery("truncate table Yder");
		stmt.executeQuery("truncate table Sygehus");
		stmt.executeQuery("truncate table SygehusAfdeling");
		stmt.executeQuery("truncate table Apotek");
		stmt.close();
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
		SORParser importer = new SORParser(FAKE_TIME_GAP);
		File[] files = new File[] { fullSor };

		importer.run(files, new AuditingPersister(connection));

		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Praksis");
		rs.next();
		assertEquals(3148, rs.getInt(1));
		rs.close();

		rs = stmt.executeQuery("Select count(*) from Yder");
		rs.next();
		assertEquals(5434, rs.getInt(1));
		rs.close();

		rs = stmt.executeQuery("Select count(*) from Sygehus");
		rs.next();
		assertEquals(469, rs.getInt(1));
		rs.close();

		rs = stmt.executeQuery("Select count(*) from SygehusAfdeling");
		rs.next();
		assertEquals(2890, rs.getInt(1));
		rs.close();

		rs = stmt.executeQuery("Select count(*) from Apotek");
		rs.next();
		assertEquals(328, rs.getInt(1));
		rs.close();

		rs = stmt.executeQuery("Select count(*) from Praksis where ValidTo < now()");
		rs.next();
		assertEquals(49, rs.getInt(1));
		rs.close();

		rs = stmt.executeQuery("Select count(*) from Yder where ValidTo < now()");
		rs.next();
		assertEquals(451, rs.getInt(1));
		rs.close();

		rs = stmt.executeQuery("Select count(*) from Sygehus where ValidTo < now()");
		rs.next();
		assertEquals(20, rs.getInt(1));
		rs.close();

		rs = stmt.executeQuery("Select count(*) from SygehusAfdeling where ValidTo < now()");
		rs.next();
		assertEquals(255, rs.getInt(1));
		rs.close();

		rs = stmt.executeQuery("Select count(*) from Apotek where ValidTo < now()");
		rs.next();
		assertEquals(2, rs.getInt(1));
		rs.close();

		rs = stmt.executeQuery("Select count(*) from Praksis where ValidTo > now()");
		rs.next();
		assertEquals(3148 - 49, rs.getInt(1));
		rs.close();

		rs = stmt.executeQuery("Select count(*) from Yder where ValidTo > now()");
		rs.next();
		assertEquals(5434 - 451, rs.getInt(1));
		rs.close();

		rs = stmt.executeQuery("Select count(*) from Sygehus where ValidTo > now()");
		rs.next();
		assertEquals(469 - 20, rs.getInt(1));
		rs.close();

		rs = stmt.executeQuery("Select count(*) from SygehusAfdeling where ValidTo > now()");
		rs.next();
		assertEquals(2890 - 255, rs.getInt(1));
		rs.close();

		rs = stmt.executeQuery("Select count(*) from Apotek where ValidTo > now()");
		rs.next();
		assertEquals(328 - 2, rs.getInt(1));
		rs.close();
		stmt.close();
	}
}
