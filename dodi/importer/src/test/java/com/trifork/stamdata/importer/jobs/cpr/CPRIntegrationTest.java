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

package com.trifork.stamdata.importer.jobs.cpr;

import static com.trifork.stamdata.Helpers.*;
import static com.trifork.stamdata.importer.util.Dates.*;
import static org.junit.Assert.*;

import java.io.File;
import java.sql.*;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.junit.*;

import com.trifork.stamdata.Helpers;
import com.trifork.stamdata.importer.persistence.*;


public class CPRIntegrationTest
{
	private Connection connection;

	@Before
	public void cleanDatabase() throws Exception
	{
		connection = Helpers.getConnection();

		Statement statement = connection.createStatement();
		statement.execute("truncate table Person");
		statement.execute("truncate table BarnRelation");
		statement.execute("truncate table ForaeldreMyndighedRelation");
		statement.execute("truncate table UmyndiggoerelseVaergeRelation");
		statement.execute("truncate table PersonIkraft");
	}

	@After
	public void tearDown() throws Exception
	{
		connection.rollback();
		connection.close();
	}

	@Test
	public void canEstablishData() throws Exception
	{
		parseFile("data/cpr/testEtablering/D100313.L431102");

		// When running a full load (file doesn't ends on 01) of CPR no
		// LatestIkraft should be written to the db.

		Date latestIkraft = CPRParser.getLatestVersion(connection);
		assertNull(latestIkraft);
	}

	@Test
	public void canImportAnUpdate() throws Exception
	{
		parseFile("data/cpr/D100315.L431101");

		Statement stmt = connection.createStatement();
		
		// Persist the first set.
		
		ResultSet rs = stmt.executeQuery("SELECT Fornavn, ValidFrom, ValidTo FROM Person WHERE CPR='1312095098'");
		
		assertTrue(rs.next());
		assertEquals("Hjalte", rs.getString("Fornavn"));
		assertEquals("2010-03-15 00:00:00.0", rs.getString("validFrom"));
		assertEquals("2999-12-31 00:00:00.0", rs.getString("validTo"));
		assertFalse(rs.next());

		// Persist the next set.
		
		parseFile("data/cpr/D100317.L431101");

		rs = stmt.executeQuery("SELECT Fornavn, validFrom, validTo FROM Person WHERE cpr='1312095098' ORDER BY ValidFrom");
		
		assertTrue(rs.next());
		assertEquals("Hjalte", rs.getString("Fornavn"));
		assertEquals("2010-03-15 00:00:00.0", rs.getString("validFrom"));
		assertEquals("2010-03-17 00:00:00.0", rs.getString("validTo"));
		assertTrue(rs.next());
		assertEquals("Hjalts", rs.getString("Fornavn"));
		assertEquals("2010-03-17 00:00:00.0", rs.getString("validFrom"));
		assertEquals("2999-12-31 00:00:00.0", rs.getString("validTo"));
		assertFalse(rs.next());
	}

	@Test(expected = Exception.class)
	public void failsWhenDatesAreNotInSequence() throws Exception
	{
		parseFile("data/cpr/testSequence1/D100314.L431101");

		Date latestIkraft = CPRParser.getLatestVersion(connection);
		assertEquals(DK_yyyy_MM_dd.parseDateTime("2001-11-16").toDate(), latestIkraft);

		parseFile("data/cpr/testSequence2/D100314.L431101");

		latestIkraft = CPRParser.getLatestVersion(connection);
		assertEquals(DK_yyyy_MM_dd.parseDateTime("2001-11-19").toDate(), latestIkraft);

		parseFile("data/cpr/testOutOfSequence/D100314.L431101");
	}

	@Test(expected = Exception.class)
	public void failsWhenEndRecordAppearsBeforeEndOfFile() throws Exception
	{
		parseFile("data/cpr/endRecords/D100314.L431101");
	}

	@Test(expected = Exception.class)
	public void failsWhenNoEndRecordExists() throws Exception
	{
		parseFile("data/cpr/endRecords/D100315.L431101");
	}

	@Test
	public void canImportPersonNavnebeskyttelse() throws Exception
	{
		parseFile("data/cpr/testCPR1/D100314.L431101");

		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM Person WHERE CPR = '0101965058'");
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
		assertEquals(DK_yyyy_MM_dd.parseDateTime("1997-09-09").toDate(), rs.getDate("NavneBeskyttelseStartDato"));
		assertEquals(DK_yyyy_MM_dd.parseDateTime("2001-02-20").toDate(), rs.getDate("NavneBeskyttelseSletteDato"));
		assertEquals("", rs.getString("GaeldendeCPR"));
		assertEquals(DK_yyyy_MM_dd.parseDateTime("1896-01-01").toDate(), rs.getDate("Foedselsdato"));
		assertEquals("Pensionist", rs.getString("Stilling"));
		assertEquals("8511", rs.getString("VejKode"));
		assertEquals("851", rs.getString("KommuneKode"));
		assertEquals(DK_yyyy_MM_dd.parseDateTime("2001-11-16").toDate(), rs.getDate("ValidFrom"));
		assertEquals(DK_yyyy_MM_dd.parseDateTime("2999-12-31").toDate(), rs.getDate("ValidTo"));
		assertTrue(rs.last());
	}

	@Test
	public void canImportForaeldreMyndighedBarn() throws Exception
	{
		parseFile("data/cpr/testForaeldremyndighed/D100314.L431101");

		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("Select * from Person where CPR='3112970028'");

		rs.next();

		assertTrue(rs.last());

		rs = stmt.executeQuery("SELECT * FROM BarnRelation WHERE BarnCPR='3112970028'");

		rs.next();

		assertEquals("0702614082", rs.getString("CPR"));
		assertTrue(rs.last());

		rs = stmt.executeQuery("SELECT * FROM ForaeldreMyndighedRelation WHERE CPR='3112970028' ORDER BY TypeKode");

		rs.next();

		assertEquals("0003", rs.getString("TypeKode"));
		assertEquals("Mor", rs.getString("TypeTekst"));
		assertEquals("", rs.getString("RelationCpr"));
		assertEquals(DK_yyyy_MM_dd.parseDateTime("2008-01-01").toDate(), rs.getDate("ValidFrom"));
		assertEquals(DK_yyyy_MM_dd.parseDateTime("2999-12-31").toDate(), rs.getDate("ValidTo"));

		rs.next();

		assertEquals("0004", rs.getString("TypeKode"));
		assertEquals("Far", rs.getString("TypeTekst"));
		assertEquals("", rs.getString("RelationCpr"));
		assertEquals(DK_yyyy_MM_dd.parseDateTime("2008-01-01").toDate(), rs.getDate("ValidFrom"));
		assertEquals(DK_yyyy_MM_dd.parseDateTime("2999-12-31").toDate(), rs.getDate("ValidTo"));

		assertTrue(rs.last());
	}

	@Test
	public void canImportUmyndighedVaerge() throws Exception
	{
		parseFile("data/cpr/testUmyndigVaerge/D100314.L431101");

		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM UmyndiggoerelseVaergeRelation WHERE CPR='0709614126'");

		rs.next();

		assertEquals("0001", rs.getString("TypeKode"));
		assertEquals("Værges CPR findes", rs.getString("TypeTekst"));
		assertEquals("0904414131", rs.getString("RelationCpr"));
		assertEquals(DK_yyyy_MM_dd.parseDateTime("2000-02-28").toDate(), rs.getDate("RelationCprStartDato"));
		assertEquals("", rs.getString("VaergesNavn"));
		assertEquals(null, rs.getDate("VaergesNavnStartDato"));
		assertEquals("", rs.getString("RelationsTekst1"));
		assertEquals("", rs.getString("RelationsTekst2"));
		assertEquals("", rs.getString("RelationsTekst3"));
		assertEquals("", rs.getString("RelationsTekst4"));
		assertEquals("", rs.getString("RelationsTekst5"));
		assertEquals(DK_yyyy_MM_dd.parseDateTime("2001-11-19").toDate(), rs.getDate("ValidFrom"));
		assertEquals(DK_yyyy_MM_dd.parseDateTime("2999-12-31").toDate(), rs.getDate("ValidTo"));
		assertTrue(rs.last());
	}

	@Test
	public void ImportU12160Test() throws Exception
	{
		parseFile("data/cpr/D100312.L431101");

		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("Select COUNT(*) from Person");
		rs.next();
		assertEquals(100, rs.getInt(1));

		rs = stmt.executeQuery("SELECT COUNT(*) from BarnRelation");
		rs.next();
		assertEquals(30, rs.getInt(1));

		rs = stmt.executeQuery("SELECT COUNT(*) from ForaeldreMyndighedRelation");
		rs.next();
		assertEquals(4, rs.getInt(1));

		rs = stmt.executeQuery("SELECT COUNT(*) from UmyndiggoerelseVaergeRelation");
		rs.next();
		assertEquals(1, rs.getInt(1));

		// Check Address protection

		rs = stmt.executeQuery("SELECT COUNT(*) FROM Person WHERE NavneBeskyttelseStartDato < NOW() AND NavneBeskyttelseSletteDato > NOW()");
		rs.next();
		assertEquals(1, rs.getInt(1));
	}

	@Test
	public void ImportU12170Test() throws Exception
	{
		parseFile("data/cpr/D100313.L431101");

		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("Select COUNT(*) from Person");
		rs.next();
		assertEquals(80, rs.getInt(1));

		rs = stmt.executeQuery("SELECT COUNT(*) FROM BarnRelation");
		rs.next();
		assertEquals(29, rs.getInt(1));

		rs = stmt.executeQuery("SELECT COUNT(*) FROM ForaeldreMyndighedRelation");
		rs.next();
		assertEquals(5, rs.getInt(1));

		rs = stmt.executeQuery("SELECT COUNT(*) FROM UmyndiggoerelseVaergeRelation");
		rs.next();
		assertEquals(2, rs.getInt(1));

		// Check address protection.

		rs = stmt.executeQuery("SELECT COUNT(*) FROM Person WHERE NavneBeskyttelseStartDato < NOW() AND NavneBeskyttelseSletteDato > NOW()");
		rs.next();
		assertEquals(1, rs.getInt(1));
	}

	private void parseFile(String fileName) throws Exception
	{
		File file = FileUtils.toFile(getClass().getClassLoader().getResource(fileName));
		new CPRParser(FAKE_TIME_GAP).run(new File[] { file }, new AuditingPersister(connection), null, 0);
	}
}
