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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.jobs.sor.SORImporter;
import com.trifork.stamdata.importer.persistence.AuditingPersister;


public class SORIntegrationTest
{
	public static File onePraksis;
	public static File oneSygehus;
	public static File oneApotek;
	public static File fullSor;

	private Connection con;

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
		con = MySQLConnectionManager.getConnection();

		Statement stmt = con.createStatement();
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
		con.rollback();
		con.close();
	}

	@Test
	public void testImport() throws Exception
	{
		SORImporter importer = new SORImporter();
		File[] files = new File[] { fullSor };

		importer.importFiles(files, new AuditingPersister(con));

		Statement stmt = con.createStatement();
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
