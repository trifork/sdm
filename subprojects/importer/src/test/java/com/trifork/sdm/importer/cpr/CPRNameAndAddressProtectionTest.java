package com.trifork.sdm.importer.cpr;


import org.junit.*;


public class CPRNameAndAddressProtectionTest
{

	private static final String TESTDATA_DIR = "testdata/cpr/NameAndAddressProtection";


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
		File fInitial = TestHelper.getFile(TESTDATA_DIR + "/D100312.L431101");

		// Act and assert
		new CPRImporter().importFiles(Arrays.asList(fInitial));

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("Select * from Person where CPR='0709614452'");
		rs.next();
		assertEquals("K", rs.getString("Koen"));
		assertEquals("Navnebeskyttet", rs.getString("Fornavn"));
		assertEquals("Navnebeskyttet", rs.getString("Mellemnavn"));
		assertEquals("Navnebeskyttet", rs.getString("Efternavn"));
		assertEquals("Navnebeskyttet", rs.getString("CoNavn"));
		assertEquals("Adressebeskyttet", rs.getString("Lokalitet"));
		assertEquals("Adressebeskyttet", rs.getString("Vejnavn"));
		assertEquals("99", rs.getString("Bygningsnummer"));
		assertEquals("99", rs.getString("Husnummer"));
		assertEquals("99", rs.getString("Etage"));
		assertEquals("", rs.getString("SideDoerNummer"));
		assertEquals("Adressebeskyttet", rs.getString("Bynavn"));
		assertEquals("9999", rs.getString("Postnummer"));
		assertEquals("Adressebeskyttet", rs.getString("PostDistrikt"));
		assertEquals("01", rs.getString("Status"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("1997-12-31"), rs.getDate("NavneBeskyttelseStartDato"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2110-12-31"), rs.getDate("NavneBeskyttelseSletteDato"));
		assertEquals("", rs.getString("GaeldendeCPR"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("1961-09-07"), rs.getDate("Foedselsdato"));
		assertEquals("", rs.getString("Stilling"));
		assertEquals("99", rs.getString("VejKode"));
		assertEquals("999", rs.getString("KommuneKode"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2001-11-16"), rs.getDate("ValidFrom"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2999-12-31"), rs.getDate("ValidTo"));
		assertTrue(rs.last());
		rs.close();
		rs = stmt.executeQuery("Select * from " + MySQLConnectionManager.getHousekeepingDBName() + ".AdresseBeskyttelse where CPR='0709614452'");
		rs.next();
		assertEquals("Vibeke", rs.getString("Fornavn"));
		assertEquals("", rs.getString("Mellemnavn"));
		assertEquals("Sejersen", rs.getString("Efternavn"));
		assertEquals("", rs.getString("CoNavn"));
		assertEquals("", rs.getString("Lokalitet"));
		assertEquals("Søgade", rs.getString("Vejnavn"));
		assertEquals("", rs.getString("Bygningsnummer"));
		assertEquals("16", rs.getString("Husnummer"));
		assertEquals("", rs.getString("Etage"));
		assertEquals("", rs.getString("SideDoerNummer"));
		assertEquals("Vodskov", rs.getString("Bynavn"));
		assertEquals("9000", rs.getString("Postnummer"));
		assertEquals("Aalborg", rs.getString("PostDistrikt"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("1997-12-31"), rs.getDate("NavneBeskyttelseStartDato"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2110-12-31"), rs.getDate("NavneBeskyttelseSletteDato"));
		assertEquals("8511", rs.getString("VejKode"));
		assertEquals("851", rs.getString("KommuneKode"));
		assertTrue(rs.last());
		stmt.close();
		con.close();
		*/
	}


	@Test
	public void ImportPersonNavnebeskyttelsesUpdateWhenDayIsHereTest() throws Exception
	{
		/*
		// Arrange
		File fInitial = TestHelper.getFile(TESTDATA_DIR + "/D100312.L431101");
		File fEmpty = TestHelper.getFile(TESTDATA_DIR + "/D100313.L431101");
		File fNewNameAdress = TestHelper.getFile(TESTDATA_DIR + "/D100314.L431101");

		// Act and assert
		new CPRImporter().importFiles(Arrays.asList(fInitial));

		// Check that we have imported our test person correctly
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
		assertEquals("", rs.getString("Etage"));
		assertEquals("", rs.getString("SideDoerNummer"));
		assertEquals("Vodskov", rs.getString("Bynavn"));
		assertEquals("9000", rs.getString("Postnummer"));
		assertEquals("Aalborg", rs.getString("PostDistrikt"));
		assertEquals("01", rs.getString("Status"));
		assertNull(rs.getDate("NavneBeskyttelseStartDato"));
		assertNull(rs.getDate("NavneBeskyttelseSletteDato"));
		assertEquals("", rs.getString("GaeldendeCPR"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("1896-01-01"), rs.getDate("Foedselsdato"));
		assertEquals("Pensionist", rs.getString("Stilling"));
		assertEquals("8511", rs.getString("VejKode"));
		assertEquals("851", rs.getString("KommuneKode"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2001-11-16"), rs.getDate("ValidFrom"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2999-12-31"), rs.getDate("ValidTo"));
		assertTrue(rs.last());

		// Set 'NavneBeskyttelseStartDato' and make sure its moved when
		// 'NavneBeskyttelseStartDato' is reached
		stmt.execute("UPDATE Person SET NavneBeskyttelseStartDato='2010-07-28' WHERE CPR='0101965058'");

		// Import empty CPR
		new CPRImporter().importFiles(Arrays.asList(fEmpty));

		// Check protection of name and address
		rs = stmt.executeQuery("Select * from Person where CPR='0101965058'");
		rs.next();
		assertEquals("K", rs.getString("Koen"));
		assertEquals("Navnebeskyttet", rs.getString("Fornavn"));
		assertEquals("Navnebeskyttet", rs.getString("Mellemnavn"));
		assertEquals("Navnebeskyttet", rs.getString("Efternavn"));
		assertEquals("Navnebeskyttet", rs.getString("CoNavn"));
		assertEquals("Adressebeskyttet", rs.getString("Lokalitet"));
		assertEquals("Adressebeskyttet", rs.getString("Vejnavn"));
		assertEquals("99", rs.getString("Bygningsnummer"));
		assertEquals("99", rs.getString("Husnummer"));
		assertEquals("99", rs.getString("Etage"));
		assertEquals("", rs.getString("SideDoerNummer"));
		assertEquals("Adressebeskyttet", rs.getString("Bynavn"));
		assertEquals("9999", rs.getString("Postnummer"));
		assertEquals("Adressebeskyttet", rs.getString("PostDistrikt"));
		assertEquals("01", rs.getString("Status"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2010-07-28"), rs.getDate("NavneBeskyttelseStartDato"));
		assertNull(rs.getDate("NavneBeskyttelseSletteDato"));
		assertEquals("", rs.getString("GaeldendeCPR"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("1896-01-01"), rs.getDate("Foedselsdato"));
		assertEquals("Pensionist", rs.getString("Stilling"));
		assertEquals("99", rs.getString("VejKode"));
		assertEquals("999", rs.getString("KommuneKode"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2001-11-16"), rs.getDate("ValidFrom"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2999-12-31"), rs.getDate("ValidTo"));
		assertTrue(rs.last());

		// Check that we have name and address in the 'AdresseBeskyttelse'
		rs = stmt.executeQuery("Select * from " + MySQLConnectionManager.getHousekeepingDBName() + ".AdresseBeskyttelse where CPR='0101965058'");
		rs.next();
		assertEquals("Ude Ulrike", rs.getString("Fornavn"));
		assertEquals("", rs.getString("Mellemnavn"));
		assertEquals("Udtzen", rs.getString("Efternavn"));
		assertEquals("", rs.getString("CoNavn"));
		assertEquals("", rs.getString("Lokalitet"));
		assertEquals("Søgade", rs.getString("Vejnavn"));
		assertEquals("", rs.getString("Bygningsnummer"));
		assertEquals("16", rs.getString("Husnummer"));
		assertEquals("", rs.getString("Etage"));
		assertEquals("", rs.getString("SideDoerNummer"));
		assertEquals("Vodskov", rs.getString("Bynavn"));
		assertEquals("9000", rs.getString("Postnummer"));
		assertEquals("Aalborg", rs.getString("PostDistrikt"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2010-07-28"), rs.getDate("NavneBeskyttelseStartDato"));
		assertNull(rs.getDate("NavneBeskyttelseSletteDato"));
		assertEquals("8511", rs.getString("VejKode"));
		assertEquals("851", rs.getString("KommuneKode"));
		assertTrue(rs.last());

		// Change the name and address in the next import
		new CPRImporter().importFiles(Arrays.asList(fNewNameAdress));

		// Check protection of name and address but not the other informations
		// (Stilling=Bensionist)
		rs = stmt.executeQuery("Select * from Person where CPR='0101965058' ORDER BY ValidTo");
		rs.next();
		rs.next();
		assertEquals("K", rs.getString("Koen"));
		assertEquals("Navnebeskyttet", rs.getString("Fornavn"));
		assertEquals("Navnebeskyttet", rs.getString("Mellemnavn"));
		assertEquals("Navnebeskyttet", rs.getString("Efternavn"));
		assertEquals("Navnebeskyttet", rs.getString("CoNavn"));
		assertEquals("Adressebeskyttet", rs.getString("Lokalitet"));
		assertEquals("Adressebeskyttet", rs.getString("Vejnavn"));
		assertEquals("99", rs.getString("Bygningsnummer"));
		assertEquals("99", rs.getString("Husnummer"));
		assertEquals("99", rs.getString("Etage"));
		assertEquals("", rs.getString("SideDoerNummer"));
		assertEquals("Adressebeskyttet", rs.getString("Bynavn"));
		assertEquals("9999", rs.getString("Postnummer"));
		assertEquals("Adressebeskyttet", rs.getString("PostDistrikt"));
		assertEquals("01", rs.getString("Status"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2010-07-28"), rs.getDate("NavneBeskyttelseStartDato"));
		assertNull(rs.getDate("NavneBeskyttelseSletteDato"));
		assertEquals("", rs.getString("GaeldendeCPR"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("1896-01-01"), rs.getDate("Foedselsdato"));
		assertEquals("Bensionist", rs.getString("Stilling"));
		assertEquals("99", rs.getString("VejKode"));
		assertEquals("999", rs.getString("KommuneKode"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2001-11-18"), rs.getDate("ValidFrom"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2999-12-31"), rs.getDate("ValidTo"));
		assertTrue(rs.last());

		// Check that we have name and address in the 'AdresseBeskyttelse'
		rs = stmt.executeQuery("Select * from " + MySQLConnectionManager.getHousekeepingDBName() + ".AdresseBeskyttelse where CPR='0101965058'");
		rs.next();
		assertEquals("Bde Blrike", rs.getString("Fornavn"));
		assertEquals("", rs.getString("Mellemnavn"));
		assertEquals("Bdtzen", rs.getString("Efternavn"));
		assertEquals("", rs.getString("CoNavn"));
		assertEquals("", rs.getString("Lokalitet"));
		assertEquals("Bøgade", rs.getString("Vejnavn"));
		assertEquals("", rs.getString("Bygningsnummer"));
		assertEquals("16", rs.getString("Husnummer"));
		assertEquals("", rs.getString("Etage"));
		assertEquals("", rs.getString("SideDoerNummer"));
		assertEquals("Vodskov", rs.getString("Bynavn"));
		assertEquals("9000", rs.getString("Postnummer"));
		assertEquals("Aalborg", rs.getString("PostDistrikt"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2010-07-28"), rs.getDate("NavneBeskyttelseStartDato"));
		assertNull(rs.getDate("NavneBeskyttelseSletteDato"));
		assertEquals("8511", rs.getString("VejKode"));
		assertEquals("851", rs.getString("KommuneKode"));
		assertTrue(rs.last());

		stmt.close();
		con.close();
		
		*/
	}


	@Test
	public void RestoreNavneBeskyttelseTest() throws Exception
	{
		/*
		// Arrange
		File fInitial = TestHelper.getFile(TESTDATA_DIR + "/D100312.L431101");
		File fEmpty = TestHelper.getFile(TESTDATA_DIR + "/D100313.L431101");

		// Act and assert
		new CPRImporter().importFiles(Arrays.asList(fInitial));

		// Check that we have imported our test person correctly
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();

		// Set 'NavneBeskyttelseStartDato' and make sure its moved when
		// 'NavneBeskyttelseStartDato' is reached
		stmt.execute("UPDATE Person SET NavneBeskyttelseStartDato='2010-07-28' WHERE CPR='0101965058'");

		// Import empty CPR
		new CPRImporter().importFiles(Arrays.asList(fEmpty));

		// Check protection of name and address
		ResultSet rs = stmt.executeQuery("Select * from Person where CPR='0101965058'");
		rs.next();
		assertEquals("K", rs.getString("Koen"));
		assertEquals("Navnebeskyttet", rs.getString("Fornavn"));
		assertEquals("Navnebeskyttet", rs.getString("Mellemnavn"));
		assertEquals("Navnebeskyttet", rs.getString("Efternavn"));
		assertEquals("Navnebeskyttet", rs.getString("CoNavn"));
		assertEquals("Adressebeskyttet", rs.getString("Lokalitet"));
		assertEquals("Adressebeskyttet", rs.getString("Vejnavn"));
		assertEquals("99", rs.getString("Bygningsnummer"));
		assertEquals("99", rs.getString("Husnummer"));
		assertEquals("99", rs.getString("Etage"));
		assertEquals("", rs.getString("SideDoerNummer"));
		assertEquals("Adressebeskyttet", rs.getString("Bynavn"));
		assertEquals("9999", rs.getString("Postnummer"));
		assertEquals("Adressebeskyttet", rs.getString("PostDistrikt"));
		assertEquals("01", rs.getString("Status"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2010-07-28"), rs.getDate("NavneBeskyttelseStartDato"));
		assertNull(rs.getDate("NavneBeskyttelseSletteDato"));
		assertEquals("", rs.getString("GaeldendeCPR"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("1896-01-01"), rs.getDate("Foedselsdato"));
		assertEquals("Pensionist", rs.getString("Stilling"));
		assertEquals("99", rs.getString("VejKode"));
		assertEquals("999", rs.getString("KommuneKode"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2001-11-16"), rs.getDate("ValidFrom"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2999-12-31"), rs.getDate("ValidTo"));
		assertTrue(rs.last());

		// Set 'NavneBeskyttelseSletteDato' to past date
		stmt.execute("UPDATE " + MySQLConnectionManager.getHousekeepingDBName() + ".AdresseBeskyttelse SET NavneBeskyttelseSletteDato='2010-08-02' WHERE CPR='0101965058'");

		// Execute the restore job that will restore the name and addresses
		NavnebeskyttelseRestrukt job = new NavnebeskyttelseRestrukt();
		job.run();

		// Check that the data is restored correctly
		rs = stmt.executeQuery("Select * from Person where CPR='0101965058' ORDER BY ValidTo DESC");
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
		assertEquals("", rs.getString("Etage"));
		assertEquals("", rs.getString("SideDoerNummer"));
		assertEquals("Vodskov", rs.getString("Bynavn"));
		assertEquals("9000", rs.getString("Postnummer"));
		assertEquals("Aalborg", rs.getString("PostDistrikt"));
		assertEquals("01", rs.getString("Status"));
		assertEquals("", rs.getString("GaeldendeCPR"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("1896-01-01"), rs.getDate("Foedselsdato"));
		assertEquals("Pensionist", rs.getString("Stilling"));
		assertEquals("8511", rs.getString("VejKode"));
		assertEquals("851", rs.getString("KommuneKode"));
		assertEquals(CPRParser.yyyy_MM_dd.parse("2999-12-31"), rs.getDate("ValidTo"));
		assertTrue(rs.next()); // The protected record
		assertTrue(rs.last());

		// Check that the record is removed from 'AdresseBeskyttelse"
		rs = stmt.executeQuery("Select * from " + MySQLConnectionManager.getHousekeepingDBName() + ".AdresseBeskyttelse where CPR='0101965058'");
		assertFalse(rs.next());

		stmt.close();
		con.close();
		*/
	}
}
