package dk.trifork.sdm.integration.autorisationsregister;

import dk.trifork.sdm.config.MySQLConnectionManager;
import dk.trifork.sdm.importer.autorisationsregister.AutImporter;
import dk.trifork.sdm.importer.autorisationsregister.model.Autorisation;
import dk.trifork.sdm.importer.autorisationsregister.model.Autorisationsregisterudtraek;
import dk.trifork.sdm.importer.exceptions.FileImporterException;
import dk.trifork.sdm.model.Dataset;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class AutIntegrationTest {

	public static File initial;
	public static File next;
	public static File invalid;

	static Autorisationsregisterudtraek initialCompares = new Autorisationsregisterudtraek(Calendar.getInstance());

	static {
		initialCompares.addEntity(new Autorisation("0013F;0101251489;Bondo;Jørgen;7170"));
		initialCompares.addEntity(new Autorisation("0013H;0101280063;Johnsen;Tage Søgaard;7170"));
		initialCompares.addEntity(new Autorisation("0013J;0101280551;Bertelsen;Svend Christian;7170"));
		initialCompares.addEntity(new Autorisation("0013K;0101280896;Frederiksen;Lilian;7170"));
	}

	@Before
	public void init() {

		initial = FileUtils.toFile(getClass().getClassLoader().getResource("data/aut/valid/20090915AutDK.csv"));
		next = FileUtils.toFile(getClass().getClassLoader().getResource("data/aut/valid/20090918AutDK.csv"));
		invalid = FileUtils.toFile(getClass().getClassLoader().getResource("data/aut/invalid/20090915AutDK.csv"));
	}

	@Before
	@After
	public void cleanDb() throws SQLException {

		Connection con = MySQLConnectionManager.getConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("TRUNCATE TABLE " + Dataset.getEntityTypeDisplayName(Autorisation.class));
		stmt.close();
		con.close();
	}

	@Test
	public void testImport() throws Exception {

		List<File> files = new ArrayList<File>();
		files.add(initial);
		AutImporter importer = new AutImporter();
		importer.run(files);

		Connection con = MySQLConnectionManager.getConnection();
		Statement stmt = con.createStatement();

		ResultSet rs = stmt.executeQuery("select count(*) from " + Dataset.getEntityTypeDisplayName(Autorisation.class));
		rs.next();
		assertEquals("Number of records in database", initialCompares.getEntities().size(), rs.getInt(1));

		rs = stmt.executeQuery("select * from " + Dataset.getEntityTypeDisplayName(Autorisation.class));

		for (int i = 0; i < initialCompares.getEntities().size(); i++) {
			rs.next();
			Autorisation compare = initialCompares.getEntityById(rs.getString("Autorisationsnummer"));
			assertEquals(compare.getCpr(), rs.getString("cpr"));
			assertEquals(compare.getFornavn(), rs.getString("Fornavn"));
			assertEquals(compare.getEfternavn(), rs.getString("Efternavn"));
			assertEquals(compare.getUddannelsesKode(), rs.getString("UddannelsesKode"));
		}
		stmt.close();
		con.close();

	}

	@Test
	public void testImportX2() throws Exception {

		List<File> files = new ArrayList<File>();
		files.add(initial);
		AutImporter importer = new AutImporter();
		importer.run(files);
		importer.run(files);

		Connection con = MySQLConnectionManager.getConnection();
		Statement stmt = con.createStatement();

		ResultSet rs = stmt.executeQuery("select count(*) from " + Dataset.getEntityTypeDisplayName(Autorisation.class));
		rs.next();
		assertEquals("Number of records in database", initialCompares.getEntities().size(), rs.getInt(1));

		rs = stmt.executeQuery("select * from " + Dataset.getEntityTypeDisplayName(Autorisation.class));

		for (int i = 0; i < initialCompares.getEntities().size(); i++) {
			rs.next();
			Autorisation compare = initialCompares.getEntityById(rs.getString("Autorisationsnummer"));
			assertEquals(compare.getCpr(), rs.getString("cpr"));
			assertEquals(compare.getFornavn(), rs.getString("Fornavn"));
			assertEquals(compare.getEfternavn(), rs.getString("Efternavn"));
			assertEquals(compare.getUddannelsesKode(), rs.getString("UddannelsesKode"));
		}
		stmt.close();
		con.close();
	}

	/**
	 * Test adding a dataset on top of an existing one
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDelta() throws Exception {

		List<File> files = new ArrayList<File>();
		files.add(initial);
		AutImporter importer = new AutImporter();
		importer.run(files);

		List<File> nextFiles = new ArrayList<File>();
		nextFiles.add(next);
		importer = new AutImporter();
		importer.run(nextFiles);

		Dataset<Autorisation> nextCompares = new Dataset<Autorisation>(Autorisation.class);
		nextCompares.addEntity(new Autorisation("0013H;0101280063;Johnsen;Tage Søgaard;7170"));
		nextCompares.addEntity(new Autorisation("0013M;0101340074;Østerby;Ester Ruth;7170"));
		nextCompares.addEntity(new Autorisation("0013J;0101280551;Bertelsen;Svend Christian;7170"));
		nextCompares.addEntity(new Autorisation("0013K;0101280896;Frederiksen;Lilian;7170"));
		nextCompares.addEntity(new Autorisation("0013L;0101290565;Heering;Eli;7170"));

		Connection con = MySQLConnectionManager.getConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select count(*) from " + Dataset.getEntityTypeDisplayName(Autorisation.class) + " where validfrom < '2009-09-19' and validto > '2009-09-19'");
		rs.next();
		assertEquals(nextCompares.getEntities().size(), rs.getInt(1));

		rs = stmt.executeQuery("select * from " + Dataset.getEntityTypeDisplayName(Autorisation.class) + " where validfrom < '2009-09-19' and validto > '2009-09-19'");

		for (int i = 0; i < nextCompares.getEntities().size(); i++) {
			rs.next();
			Autorisation compare = nextCompares.getEntityById(rs.getString("Autorisationsnummer"));
			assertEquals(compare.getCpr(), rs.getString("cpr"));
			assertEquals(compare.getFornavn(), rs.getString("Fornavn"));
			assertEquals(compare.getEfternavn(), rs.getString("Efternavn"));
			assertEquals(compare.getUddannelsesKode(), rs.getString("UddannelsesKode"));
		}

		stmt.close();
		con.close();
	}

	@Test
	public void testInvalid() throws Exception {

		List<File> files = new ArrayList<File>();
		files.add(invalid);
		AutImporter importer = new AutImporter();
		try {
			importer.run(files);
			fail();
		}
		catch (FileImporterException e) {
		}
	}

}
