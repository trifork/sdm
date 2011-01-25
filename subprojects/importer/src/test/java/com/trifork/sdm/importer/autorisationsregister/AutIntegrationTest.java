package com.trifork.sdm.importer.autorisationsregister;


import java.io.File;
import java.sql.SQLException;
import java.util.Map;

import org.junit.*;

import com.trifork.sdm.importer.TestHelper;
import com.trifork.stamdata.registre.autorisation.Autorisation;


public class AutIntegrationTest
{
	public static File initial = TestHelper.getFile("testdata/aut/valid/20090915AutDK.csv");
	public static File next = TestHelper.getFile("testdata/aut/valid/20090918AutDK.csv");
	public static File invalid = TestHelper.getFile("testdata/aut/invalid/20090915AutDK.csv");


	@Before
	@After
	public void cleanDb() throws SQLException
	{
		/*
		 * Connection con = MySQLConnectionManager.getConnection(); Statement stmt =
		 * con.createStatement(); stmt.executeUpdate("TRUNCATE TABLE " +
		 * Dataset.getEntityTypeDisplayName(Autorisation.class)); stmt.close(); con.close();
		 */
	}


	@Test
	public void testImport() throws Exception
	{
		/*
		 * Map<String, Autorisation> initialCompares = getInitialCompare();
		 * 
		 * List<File> files = new ArrayList<File>(); files.add(initial); AutImporter importer = new
		 * AutImporter(); importer.importFiles(files);
		 * 
		 * Connection con = MySQLConnectionManager.getConnection(); Statement stmt =
		 * con.createStatement();
		 * 
		 * ResultSet rs = stmt.executeQuery("select count(*) from " +
		 * Dataset.getEntityTypeDisplayName(Autorisation.class)); rs.next();
		 * assertEquals("Number of records in database", initialCompares.size(), rs.getInt(1));
		 * 
		 * rs = stmt.executeQuery("select * from " +
		 * Dataset.getEntityTypeDisplayName(Autorisation.class));
		 * 
		 * for (int i = 0; i < initialCompares.size(); i++) { rs.next(); Autorisation compare =
		 * initialCompares.get(rs.getString("Autorisationsnummer")); assertEquals(compare.getCpr(),
		 * rs.getString("cpr")); assertEquals(compare.getFornavn(), rs.getString("Fornavn"));
		 * assertEquals(compare.getEfternavn(), rs.getString("Efternavn"));
		 * assertEquals(compare.getUddannelsesKode(), rs.getString("UddannelsesKode")); }
		 * stmt.close(); con.close();
		 */
	}


	@Test
	public void testImportX2() throws Exception
	{
		/*
		 * Map<String, Autorisation> initialCompares = getInitialCompare(); List<File> files = new
		 * ArrayList<File>(); files.add(initial); AutImporter importer = new AutImporter();
		 * importer.importFiles(files); importer.importFiles(files);
		 * 
		 * Connection con = MySQLConnectionManager.getConnection(); Statement stmt =
		 * con.createStatement();
		 * 
		 * ResultSet rs = stmt.executeQuery("select count(*) from " +
		 * Dataset.getEntityTypeDisplayName(Autorisation.class)); rs.next();
		 * assertEquals("Number of records in database", initialCompares.size(), rs.getInt(1));
		 * 
		 * rs = stmt.executeQuery("select * from " +
		 * Dataset.getEntityTypeDisplayName(Autorisation.class));
		 * 
		 * for (int i = 0; i < initialCompares.size(); i++) { rs.next(); Autorisation compare =
		 * initialCompares.get(rs.getString("Autorisationsnummer")); assertEquals(compare.getCpr(),
		 * rs.getString("cpr")); assertEquals(compare.getFornavn(), rs.getString("Fornavn"));
		 * assertEquals(compare.getEfternavn(), rs.getString("Efternavn"));
		 * assertEquals(compare.getUddannelsesKode(), rs.getString("UddannelsesKode")); }
		 * stmt.close(); con.close();
		 */
	}


	/**
	 * Test adding a dataset on top of an existing one
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDelta() throws Exception
	{
		/*
		 * List<File> files = new ArrayList<File>(); files.add(initial); AutImporter importer = new
		 * AutImporter(); importer.importFiles(files);
		 * 
		 * List<File> nextFiles = new ArrayList<File>(); nextFiles.add(next); importer = new
		 * AutImporter(); importer.importFiles(nextFiles);
		 * 
		 * Map<String, Autorisation> nextCompares = getNextCompare();
		 * 
		 * Connection con = MySQLConnectionManager.getConnection(); Statement stmt =
		 * con.createStatement(); ResultSet rs = stmt.executeQuery("select count(*) from " +
		 * Dataset.getEntityTypeDisplayName(Autorisation.class) +
		 * " where validfrom < '2009-09-19' and validto > '2009-09-19'"); rs.next();
		 * assertEquals(nextCompares.size(), rs.getInt(1));
		 * 
		 * rs = stmt.executeQuery("select * from " +
		 * Dataset.getEntityTypeDisplayName(Autorisation.class) +
		 * " where validfrom < '2009-09-19' and validto > '2009-09-19'");
		 * 
		 * for (int i = 0; i < nextCompares.size(); i++) { rs.next(); Autorisation compare =
		 * nextCompares.get(rs.getString("Autorisationsnummer")); assertEquals(compare.getCpr(),
		 * rs.getString("cpr")); assertEquals(compare.getFornavn(), rs.getString("Fornavn"));
		 * assertEquals(compare.getEfternavn(), rs.getString("Efternavn"));
		 * assertEquals(compare.getUddannelsesKode(), rs.getString("UddannelsesKode")); }
		 * 
		 * stmt.close(); con.close();
		 */
	}


	@Test
	public void testInvalid() throws Exception
	{
		/*
		 * List<File> files = new ArrayList<File>(); files.add(invalid); AutImporter importer = new
		 * AutImporter(); try { importer.importFiles(files); fail(); } catch (FileImporterException
		 * e) { }
		 */
	}


	private Map<String, Autorisation> getInitialCompare()
	{
		return null;
		/*
		 * HashMap<String, Autorisation> initialCompares = new HashMap<String, Autorisation>();
		 * Autorisation ae =
		 * AutorisationsregisterParser.autorisationEntity("0013F;0101251489;Bondo;Jørgen;7170");
		 * initialCompares.put(ae.getAutorisationsnummer(), ae); ae =
		 * AutorisationsregisterParser.autorisationEntity
		 * ("0013H;0101280063;Johnsen;Tage Søgaard;7170");
		 * initialCompares.put(ae.getAutorisationsnummer(), ae); ae =
		 * AutorisationsregisterParser.autorisationEntity
		 * ("0013J;0101280551;Bertelsen;Svend Christian;7170");
		 * initialCompares.put(ae.getAutorisationsnummer(), ae); ae =
		 * AutorisationsregisterParser.autorisationEntity
		 * ("0013K;0101280896;Frederiksen;Lilian;7170");
		 * initialCompares.put(ae.getAutorisationsnummer(), ae); return initialCompares;
		 */
	}


	private Map<String, Autorisation> getNextCompare()
	{
		return null;
		/*
		 * HashMap<String, Autorisation> nextCompares = new HashMap<String, Autorisation>();
		 * Autorisation ae =
		 * AutorisationsregisterParser.autorisationEntity("0013H;0101280063;Johnsen;Tage Søgaard;7170"
		 * ); nextCompares.put(ae.getAutorisationsnummer(), ae); ae =
		 * AutorisationsregisterParser.autorisationEntity
		 * ("0013M;0101340074;Østerby;Ester Ruth;7170");
		 * nextCompares.put(ae.getAutorisationsnummer(), ae); ae =
		 * AutorisationsregisterParser.autorisationEntity
		 * ("0013J;0101280551;Bertelsen;Svend Christian;7170");
		 * nextCompares.put(ae.getAutorisationsnummer(), ae); ae =
		 * AutorisationsregisterParser.autorisationEntity
		 * ("0013K;0101280896;Frederiksen;Lilian;7170");
		 * nextCompares.put(ae.getAutorisationsnummer(), ae); ae =
		 * AutorisationsregisterParser.autorisationEntity("0013L;0101290565;Heering;Eli;7170");
		 * nextCompares.put(ae.getAutorisationsnummer(), ae); return nextCompares;
		 */
	}
}
