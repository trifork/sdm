package dk.trifork.sdm.integration.cpr;

import static dk.trifork.sdm.util.DateUtils.yyyy_MM_dd;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.trifork.sdm.config.MySQLConnectionManager;
import dk.trifork.sdm.importer.cpr.CPRImporter;
import dk.trifork.sdm.importer.exceptions.FileImporterException;


public class CPRIntegrationTest {

	@Before
	@After
	public void cleanDatabase() throws Exception {

		Connection con = MySQLConnectionManager.getConnection();
		Statement statement = con.createStatement();
		statement.execute("truncate table Person");
		statement.execute("truncate table BarnRelation");
		statement.execute("truncate table ForaeldreMyndighedRelation");
		statement.execute("truncate table UmyndiggoerelseVaergeRelation");
		statement.execute("truncate table PersonIkraft");
		statement.execute("truncate table " + MySQLConnectionManager.getHousekeepingDBName() + ".AdresseBeskyttelse");
		statement.close();
		con.close();
	}

	public File getFile(String file) {

		return FileUtils.toFile(getClass().getClassLoader().getResource(file));
	}

	@Test
	public void EtableringTest() throws Exception {

		// Arrange
		File fInitial = getFile("data/cpr/testEtablering/D100313.L431102");

		// Act and assert
		new CPRImporter().run(Arrays.asList(fInitial));

		Connection con = MySQLConnectionManager.getAutoCommitConnection();

		// When running a full load (file doesn't ends on 01) of CPR no
		// LatestIkraft should be written to the db
		Calendar latestIkraft = CPRImporter.getLatestIkraft(con);
		assertNull(latestIkraft);
		con.close();
	}

	@Test
	public void UpdateTest() throws Exception {

		// Arrange
		File fInitial = getFile("data/cpr/D100315.L431101");

		// Act and assert
		new CPRImporter().run(Arrays.asList(fInitial));

		Connection con = MySQLConnectionManager.getAutoCommitConnection();

		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("Select Fornavn, validFrom, validTo from Person WHERE cpr='1312095098'");
		assertTrue(rs.next());
		assertEquals("Hjalte", rs.getString("Fornavn"));
		assertEquals("2010-03-15 00:00:00.0", rs.getString("validFrom"));
		assertEquals("2999-12-31 00:00:00.0", rs.getString("validTo"));
		assertFalse(rs.next());

		fInitial = getFile("data/cpr/D100317.L431101");
		new CPRImporter().run(Arrays.asList(fInitial));

		rs = stmt.executeQuery("Select Fornavn, validFrom, validTo from Person WHERE cpr='1312095098' ORDER BY validFrom");
		assertTrue(rs.next());
		assertEquals("Hjalte", rs.getString("Fornavn"));
		assertEquals("2010-03-15 00:00:00.0", rs.getString("validFrom"));
		assertEquals("2010-03-17 00:00:00.0", rs.getString("validTo"));
		assertTrue(rs.next());
		assertEquals("Hjalts", rs.getString("Fornavn"));
		assertEquals("2010-03-17 00:00:00.0", rs.getString("validFrom"));
		assertEquals("2999-12-31 00:00:00.0", rs.getString("validTo"));
		assertFalse(rs.next());
		stmt.close();
		con.close();
	}

	@Test(expected = FileImporterException.class)
	public void SequenceTest() throws Exception {

		// Arrange
		File fInitial = getFile("data/cpr/testSequence1/D100314.L431101");

		// Act and assert
		new CPRImporter().run(Arrays.asList(fInitial));

		Connection con = MySQLConnectionManager.getAutoCommitConnection();

		Calendar latestIkraft = CPRImporter.getLatestIkraft(con);
		assertEquals(yyyy_MM_dd.parse("2001-11-16"), latestIkraft.getTime());

		fInitial = getFile("data/cpr/testSequence2/D100314.L431101");
		new CPRImporter().run(Arrays.asList(fInitial));

		latestIkraft = CPRImporter.getLatestIkraft(con);
		assertEquals(yyyy_MM_dd.parse("2001-11-19"), latestIkraft.getTime());

		fInitial = getFile("data/cpr/testOutOfSequence/D100314.L431101");
		new CPRImporter().run(Arrays.asList(fInitial));
		con.close();
	}

	@Test
	public void ImportPersonNavnebeskyttelsesTest() throws Exception {

		// Arrange
		File fInitial = getFile("data/cpr/testCPR1/D100314.L431101");

		// Act and assert
		new CPRImporter().run(Arrays.asList(fInitial));

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("Select * from Person where CPR='0101965058'");
		rs.next();
		assertEquals("K", rs.getString("Koen"));
		assertEquals("Ude Ulrike", rs.getString("Fornavn"));
		assertEquals("", rs.getString("Mellemnavn"));
		assertEquals("Udtzen", rs.getString("Efternavn"));
		assertEquals("", rs.getString("CoNavn"));
		assertEquals("", rs.getString("Lokalitet"));
		assertEquals("Søgade", rs.getString("Vejnavn"));
		assertEquals("", rs.getString("Bygningsnummer"));
		assertEquals("16", rs.getString("Husnummer"));
		assertEquals("1", rs.getString("Etage"));
		assertEquals("", rs.getString("SideDoerNummer"));
		assertEquals("Vodskov", rs.getString("Bynavn"));
		assertEquals("9000", rs.getString("Postnummer"));
		assertEquals("Aalborg", rs.getString("PostDistrikt"));
		assertEquals("01", rs.getString("Status"));
		assertEquals(yyyy_MM_dd.parse("1997-09-09"), rs.getDate("NavneBeskyttelseStartDato"));
		assertEquals(yyyy_MM_dd.parse("2001-02-20"), rs.getDate("NavneBeskyttelseSletteDato"));
		assertEquals("", rs.getString("GaeldendeCPR"));
		assertEquals(yyyy_MM_dd.parse("1896-01-01"), rs.getDate("Foedselsdato"));
		assertEquals("Pensionist", rs.getString("Stilling"));
		assertEquals("8511", rs.getString("VejKode"));
		assertEquals("851", rs.getString("KommuneKode"));
		assertEquals(yyyy_MM_dd.parse("2001-11-16"), rs.getDate("ValidFrom"));
		assertEquals(yyyy_MM_dd.parse("2999-12-31"), rs.getDate("ValidTo"));
		assertTrue(rs.last());
		stmt.close();
		con.close();
	}

	@Test
	public void ImportForaeldreMyndighedBarnTest() throws Exception {

		// Arrange
		File fInitial = getFile("data/cpr/testForaeldremyndighed/D100314.L431101");

		// Act and assert
		new CPRImporter().run(Arrays.asList(fInitial));

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("Select * from Person where CPR='3112970028'");
		rs.next();
		assertTrue(rs.last());

		rs = stmt.executeQuery("Select * from BarnRelation where BarnCPR='3112970028'");
		rs.next();
		assertEquals("0702614082", rs.getString("CPR"));
		assertTrue(rs.last());

		rs = stmt.executeQuery("Select * from ForaeldreMyndighedRelation where CPR='3112970028' order by TypeKode");
		rs.next();
		assertEquals("0003", rs.getString("TypeKode"));
		assertEquals("Mor", rs.getString("TypeTekst"));
		assertEquals("", rs.getString("RelationCpr"));
		assertEquals(yyyy_MM_dd.parse("2008-01-01"), rs.getDate("ValidFrom"));
		assertEquals(yyyy_MM_dd.parse("2999-12-31"), rs.getDate("ValidTo"));
		rs.next();
		assertEquals("0004", rs.getString("TypeKode"));
		assertEquals("Far", rs.getString("TypeTekst"));
		assertEquals("", rs.getString("RelationCpr"));
		assertEquals(yyyy_MM_dd.parse("2008-01-01"), rs.getDate("ValidFrom"));
		assertEquals(yyyy_MM_dd.parse("2999-12-31"), rs.getDate("ValidTo"));

		assertTrue(rs.last());
		stmt.close();
		con.close();
	}

	@Test
	public void ImportUmyndighedVaergeTest() throws Exception {

		// Arrange
		File fInitial = getFile("data/cpr/testUmyndigVaerge/D100314.L431101");

		// Act and assert
		new CPRImporter().run(Arrays.asList(fInitial));

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("Select * from UmyndiggoerelseVaergeRelation where CPR='0709614126'");
		rs.next();
		assertEquals("0001", rs.getString("TypeKode"));
		assertEquals("Værges CPR findes", rs.getString("TypeTekst"));
		assertEquals("0904414131", rs.getString("RelationCpr"));
		assertEquals(yyyy_MM_dd.parse("2000-02-28"), rs.getDate("RelationCprStartDato"));
		assertEquals("", rs.getString("VaergesNavn"));
		assertEquals(null, rs.getDate("VaergesNavnStartDato"));
		assertEquals("", rs.getString("RelationsTekst1"));
		assertEquals("", rs.getString("RelationsTekst2"));
		assertEquals("", rs.getString("RelationsTekst3"));
		assertEquals("", rs.getString("RelationsTekst4"));
		assertEquals("", rs.getString("RelationsTekst5"));
		assertEquals(yyyy_MM_dd.parse("2001-11-19"), rs.getDate("ValidFrom"));
		assertEquals(yyyy_MM_dd.parse("2999-12-31"), rs.getDate("ValidTo"));
		assertTrue(rs.last());
		stmt.close();
		con.close();
	}

	@Test
	public void kanImportereFolkekirkeoplysninger() throws Exception {
		File file = getFile("data/cpr/folkekirkeoplysninger/D100314.L431101");

		new CPRImporter().run(Arrays.asList(file));

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("Select * from Folkekirkeoplysninger where CPR='0709614126'");
		rs.next();
		assertEquals("0709614126", rs.getString("CPR"));
		assertEquals("F", rs.getString("Forholdskode"));
		assertEquals(yyyy_MM_dd.parse("1961-09-07"), rs.getDate("Startdato"));
		assertTrue(rs.last());
		stmt.close();
		con.close();
	}

	@Test
	public void kanImportereKommunaleForhold() throws Exception {
		File file = getFile("data/cpr/kommunaleForhold/D100314.L431101");

		new CPRImporter().run(Arrays.asList(file));

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("Select * from KommunaleForhold where CPR='2802363039'");
		rs.next();
		assertEquals("2802363039", rs.getString("CPR"));
		assertEquals("3", rs.getString("Kommunalforholdstypekode"));
		assertEquals("N", rs.getString("Kommunalforholdskode"));
		assertEquals(yyyy_MM_dd.parse("2000-06-30"), rs.getDate("Startdato"));
		assertEquals("Tekst til komforh3/pension", rs.getString("Bemaerkninger"));
		assertTrue(rs.last());
		stmt.close();
		con.close();
	}

	@Test
	public void kanImportereValgoplysninger() throws Exception {
		File file = getFile("data/cpr/valgoplysninger/D100314.L431101");

		new CPRImporter().run(Arrays.asList(file));

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("Select * from Valgoplysninger where CPR='0708614335'");
		rs.next();
		assertEquals("0708614335", rs.getString("CPR"));
		assertEquals("1", rs.getString("Valgkode"));
		assertEquals(yyyy_MM_dd.parse("1999-03-10"), rs.getDate("Valgretsdato"));
		assertEquals(yyyy_MM_dd.parse("1999-02-01"), rs.getDate("Startdato"));
		assertEquals(yyyy_MM_dd.parse("2001-03-10"), rs.getDate("Slettedato"));
		assertTrue(rs.last());
		stmt.close();
		con.close();
	}

	@Test
	public void ImportU12160Test() throws Exception {

		// Arrange
		File fInitial = getFile("data/cpr/D100312.L431101");

		// Act and assert
		new CPRImporter().run(Arrays.asList(fInitial));

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("Select count(*) from Person");
		rs.next();
		assertEquals(100, rs.getInt(1));

		rs = stmt.executeQuery("Select count(*) from BarnRelation");
		rs.next();
		assertEquals(30, rs.getInt(1));

		rs = stmt.executeQuery("Select count(*) from ForaeldreMyndighedRelation");
		rs.next();
		assertEquals(4, rs.getInt(1));

		rs = stmt.executeQuery("Select count(*) from UmyndiggoerelseVaergeRelation");
		rs.next();
		assertEquals(1, rs.getInt(1));

		// Check Address protection
		rs = stmt.executeQuery("Select count(*) from Person WHERE NavneBeskyttelseStartDato < now() AND NavneBeskyttelseSletteDato > now()");
		rs.next();
		assertEquals(1, rs.getInt(1));

		rs = stmt.executeQuery("Select count(*) from Person WHERE Fornavn = 'Navnebeskyttet'");
		rs.next();
		assertEquals(1, rs.getInt(1));

		rs = stmt.executeQuery("Select count(*) from " + MySQLConnectionManager.getHousekeepingDBName() + ".AdresseBeskyttelse");
		rs.next();
		assertEquals(1, rs.getInt(1));
		stmt.close();
		con.close();
	}

	@Test
	public void ImportU12170Test() throws Exception {

		// Arrange
		File fInitial = getFile("data/cpr/D100313.L431101");

		// Act and assert
		new CPRImporter().run(Arrays.asList(fInitial));

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("Select count(*) from Person");
		rs.next();
		assertEquals(80, rs.getInt(1));

		rs = stmt.executeQuery("Select count(*) from BarnRelation");
		rs.next();
		assertEquals(29, rs.getInt(1));

		rs = stmt.executeQuery("Select count(*) from ForaeldreMyndighedRelation");
		rs.next();
		assertEquals(5, rs.getInt(1));

		rs = stmt.executeQuery("Select count(*) from UmyndiggoerelseVaergeRelation");
		rs.next();
		assertEquals(2, rs.getInt(1));

		// Check Address protection
		rs = stmt.executeQuery("Select count(*) from Person WHERE NavneBeskyttelseStartDato < now() AND NavneBeskyttelseSletteDato > now()");
		rs.next();
		assertEquals(1, rs.getInt(1));

		rs = stmt.executeQuery("Select count(*) from Person WHERE Fornavn = 'Navnebeskyttet'");
		rs.next();
		assertEquals(1, rs.getInt(1));

		rs = stmt.executeQuery("Select count(*) from " + MySQLConnectionManager.getHousekeepingDBName() + ".AdresseBeskyttelse");
		rs.next();
		assertEquals(1, rs.getInt(1));
		stmt.close();
		con.close();
	}

}
