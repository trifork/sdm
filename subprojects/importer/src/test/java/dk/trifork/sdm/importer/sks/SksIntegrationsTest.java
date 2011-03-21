package dk.trifork.sdm.importer.sks;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.trifork.sdm.config.MySQLConnectionManager;


public class SksIntegrationsTest {

	public File SHAKCompleate;

	@After
	@Before
	public void cleanDb() throws Exception {

		SHAKCompleate = FileUtils.toFile(getClass().getClassLoader().getResource("data/sks/SHAKCOMPLETE.TXT"));

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();
		stmt.executeQuery("truncate table Organisation");
		stmt.close();
		con.close();
	}

	@Test
	public void testSHAKImport() throws Throwable {

		ArrayList<File> files = new ArrayList<File>();
		files.add(SHAKCompleate);
		SksImporter importer = new SksImporter();
		importer.run(files);

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
	}

}
