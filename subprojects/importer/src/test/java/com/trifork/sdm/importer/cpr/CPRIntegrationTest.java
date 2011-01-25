package com.trifork.sdm.importer.cpr;


import org.junit.*;


public class CPRIntegrationTest
{

	private static final String TESTDATA_DIR = "testdata/cpr/";


	@Before
	@After
	public void cleanDatabase() throws Exception
	{
		/*
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
		*/
	}


	@Test
	public void ImportPersonNavnebeskyttelsesTest() throws Exception
	{
		/*
		// Arrange
		File fInitial = TestHelper.getFile(TESTDATA_DIR + "testCPR1/D100314.L431101");

		// Act and assert
		new CPRImporter().importFiles(Arrays.asList(fInitial));

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
		assertEquals(CPRParser.yyyy_MM_dd.parse("1997-09-09"), rs.getDate("NavneBeskyttelseStartDato"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2001-02-20"), rs.getDate("NavneBeskyttelseSletteDato"));
		assertEquals("", rs.getString("GaeldendeCPR"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("1896-01-01"), rs.getDate("Foedselsdato"));
		assertEquals("Pensionist", rs.getString("Stilling"));
		assertEquals("8511", rs.getString("VejKode"));
		assertEquals("851", rs.getString("KommuneKode"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2001-11-16"), rs.getDate("ValidFrom"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2999-12-31"), rs.getDate("ValidTo"));
		assertTrue(rs.last());
		stmt.close();
		con.close();
		*/
	}


	@Test
	public void ImportForaeldreMyndighedBarnTest() throws Exception
	{
		/*
		// Arrange
		File fInitial = TestHelper.getFile(TESTDATA_DIR + "testForaeldremyndighed/D100314.L431101");

		// Act and assert
		new CPRImporter().importFiles(Arrays.asList(fInitial));

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
		assertEquals(CPRParser.yyyy_MM_dd.parse("2008-01-01"), rs.getDate("ValidFrom"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2999-12-31"), rs.getDate("ValidTo"));
		rs.next();
		assertEquals("0004", rs.getString("TypeKode"));
		assertEquals("Far", rs.getString("TypeTekst"));
		assertEquals("", rs.getString("RelationCpr"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2008-01-01"), rs.getDate("ValidFrom"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2999-12-31"), rs.getDate("ValidTo"));

		assertTrue(rs.last());
		stmt.close();
		con.close();
		*/
	}


	@Test
	public void ImportUmyndighedVaergeTest() throws Exception
	{
		/*
		// Arrange
		File fInitial = TestHelper.getFile(TESTDATA_DIR + "testUmyndigVaerge/D100314.L431101");

		// Act and assert
		new CPRImporter().importFiles(Arrays.asList(fInitial));

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("Select * from UmyndiggoerelseVaergeRelation where CPR='0709614126'");
		rs.next();
		assertEquals("0001", rs.getString("TypeKode"));
		assertEquals("Værges CPR findes", rs.getString("TypeTekst"));
		assertEquals("0904414131", rs.getString("RelationCpr"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2000-02-28"), rs.getDate("RelationCprStartDato"));
		assertEquals("", rs.getString("VaergesNavn"));
		assertEquals(null, rs.getDate("VaergesNavnStartDato"));
		assertEquals("", rs.getString("RelationsTekst1"));
		assertEquals("", rs.getString("RelationsTekst2"));
		assertEquals("", rs.getString("RelationsTekst3"));
		assertEquals("", rs.getString("RelationsTekst4"));
		assertEquals("", rs.getString("RelationsTekst5"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2001-11-19"), rs.getDate("ValidFrom"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2999-12-31"), rs.getDate("ValidTo"));
		assertTrue(rs.last());
		stmt.close();
		con.close();
		*/
	}


	@Test
	public void ImportU12160Test() throws Exception
	{
		/*
		// Arrange
		File fInitial = TestHelper.getFile(TESTDATA_DIR + "D100312.L431101");

		// Act and assert
		new CPRImporter().importFiles(Arrays.asList(fInitial));

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
		*/
	}


	@Test
	public void ImportU12170Test() throws Exception
	{
		/*
		// Arrange
		File fInitial = TestHelper.getFile(TESTDATA_DIR + "D100313.L431101");

		// Act and assert
		new CPRImporter().importFiles(Arrays.asList(fInitial));

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
		*/
	}


	@Test//(expected = FileImporterException.class)
	public void SequenceTest() throws Exception
	{
		/*
		// Arrange
		File fInitial = TestHelper.getFile(TESTDATA_DIR + "testSequence1/D100314.L431101");

		// Act and assert
		new CPRImporter().importFiles(Arrays.asList(fInitial));

		Connection con = MySQLConnectionManager.getAutoCommitConnection();

		Date latestIkraft = CPRImporter.getLatestIkraft(con);
		assertEquals(CPRParser.yyyy_MM_dd.parse("2001-11-16"), latestIkraft);

		fInitial = TestHelper.getFile(TESTDATA_DIR + "testSequence2/D100314.L431101");
		new CPRImporter().importFiles(Arrays.asList(fInitial));

		latestIkraft = CPRImporter.getLatestIkraft(con);
		assertEquals(CPRParser.yyyy_MM_dd.parse("2001-11-19"), latestIkraft);

		fInitial = TestHelper.getFile(TESTDATA_DIR + "testOutOfSequence/D100314.L431101");
		new CPRImporter().importFiles(Arrays.asList(fInitial));
		con.close();
		*/
	}


	@Test
	public void EtableringTest() throws Exception
	{
		/*
		// Arrange
		File fInitial = TestHelper.getFile(TESTDATA_DIR + "testEtablering/D100313.L431102");

		// Act and assert
		new CPRImporter().importFiles(Arrays.asList(fInitial));

		Connection con = MySQLConnectionManager.getAutoCommitConnection();

		// When running a full load (file doesn't ends on 01) of CPR no
		// LatestIkraft should be written to the db
		Date latestIkraft = CPRImporter.getLatestIkraft(con);
		assertNull(latestIkraft);
		con.close();
		*/
	}


	@Test
	public void UpdateTest() throws Exception
	{
		/*
		// Arrange
		File fInitial = TestHelper.getFile(TESTDATA_DIR + "D100315.L431101");

		// Act and assert
		new CPRImporter().importFiles(Arrays.asList(fInitial));

		Connection con = MySQLConnectionManager.getAutoCommitConnection();

		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("Select Fornavn, validFrom, validTo from Person WHERE cpr='1312095098'");
		assertTrue(rs.next());
		assertEquals("Hjalte", rs.getString("Fornavn"));
		assertEquals("2010-03-15 00:00:00.0", rs.getString("validFrom"));
		assertEquals("2999-12-31 00:00:00.0", rs.getString("validTo"));
		assertFalse(rs.next());

		fInitial = TestHelper.getFile(TESTDATA_DIR + "D100317.L431101");
		new CPRImporter().importFiles(Arrays.asList(fInitial));

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
		*/
	}

}
