package dk.trifork.sdm.importer.sor;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import dk.trifork.sdm.config.MySQLConnectionManager;

@Ignore("Sloooow test!")
public class SORIntegrationTest {

	public static File onePraksis;
	public static File oneSygehus;
	public static File oneApotek;
	public static File fullSor;

	@Before
	public void setUp() {

		onePraksis = FileUtils.toFile(getClass().getClassLoader().getResource("data/sor/ONE_PRAKSIS.xml"));
		oneSygehus = FileUtils.toFile(getClass().getClassLoader().getResource("data/sor/ONE_SYGEHUS.xml"));
		oneApotek = FileUtils.toFile(getClass().getClassLoader().getResource("data/sor/ONE_APOTEK.xml"));
		fullSor = FileUtils.toFile(getClass().getClassLoader().getResource("data/sor/SOR_FULL.xml"));
	}

	@After
	@Before
	public void cleanDb() throws Exception {

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();
		stmt.executeQuery("truncate table Praksis");
		stmt.executeQuery("truncate table Yder");
		stmt.executeQuery("truncate table Sygehus");
		stmt.executeQuery("truncate table SygehusAfdeling");
		stmt.executeQuery("truncate table Apotek");
		stmt.close();
		con.close();
	}

	@Test
	public void testImport() throws Exception {

		SORImporter importer = new SORImporter();
		ArrayList<File> files = new ArrayList<File>();
		files.add(fullSor);
		importer.run(files);

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("Select count(*) from Praksis");
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
		con.close();
	}
}
