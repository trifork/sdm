package com.trifork.sdm.importer.sks;


import java.io.File;

import org.junit.*;

import com.trifork.sdm.importer.TestHelper;


public class SksIntegrationsTest
{

	public static File SHAKCompleate = TestHelper.getFile("testdata/sks/SHAKCOMPLETE.TXT");


	@Before
	@After
	public void cleanDb() throws Throwable
	{
		/*
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();
		stmt.executeQuery("truncate table Organisation");
		stmt.close();
		con.close();
		*/
	}


	@Test
	public void testSHAKImport() throws Throwable
	{
		/*
		ArrayList<File> files = new ArrayList<File>();
		files.add(SHAKCompleate);
		SksImporter importer = new SksImporter();
		importer.importFiles(files);

		Connection con = MySQLConnectionManager.getConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("Select count(*) from Organisation");
		rs.next();
		assertEquals(9717, rs.getInt(1));

		rs = stmt.executeQuery("Select count(*) from Organisation where Organisationstype ='Sygehus' ");
		rs.next();
		assertEquals(689, rs.getInt(1));

		rs = stmt.executeQuery("Select count(*) from Organisation where Organisationstype ='Afdeling' ");
		rs.next();
		assertEquals(9028, rs.getInt(1));
		stmt.close();
		con.close();
		*/
	}

}
