/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */


package com.trifork.stamdata.importer.jobs.cpr;

import static com.trifork.stamdata.importer.util.Dates.yyyy_MM_dd;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.importer.config.ConnectionManager;
import com.trifork.stamdata.importer.persistence.AuditingPersister;
import com.trifork.stamdata.importer.util.Dates;


public class CPRIntegrationTest
{
	private Connection connection;


	@Before
	public void cleanDatabase() throws Exception
	{
		connection = new ConnectionManager().getConnection();

		Statement statement = connection.createStatement();
		statement.execute("truncate table Person");
		statement.execute("truncate table BarnRelation");
		statement.execute("truncate table ForaeldreMyndighedRelation");
		statement.execute("truncate table UmyndiggoerelseVaergeRelation");
		statement.execute("truncate table PersonIkraft");
		statement.execute("truncate table ChangesToCPR");
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
		importFile("data/cpr/testEtablering/D100313.L431102");

		// When running a full load (file doesn't ends on 01) of CPR no
		// LatestIkraft should be written to the db.

		Date latestIkraft = CPRImporter.getLatestVersion(connection);
		assertNull(latestIkraft);
	}


	@Test
	public void canImportAnUpdate() throws Exception
	{
		importFile("data/cpr/D100315.L431101");

		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT Fornavn, validFrom, validTo from Person WHERE cpr='1312095098'");
		assertTrue(rs.next());
		assertEquals("Hjalte", rs.getString("Fornavn"));
		assertEquals("2010-03-15 00:00:00.0", rs.getString("validFrom"));
		assertEquals("2999-12-31 00:00:00.0", rs.getString("validTo"));
		assertFalse(rs.next());

		importFile("data/cpr/D100317.L431101");

		rs = stmt.executeQuery("SELECT Fornavn, validFrom, validTo from Person WHERE cpr='1312095098' ORDER BY validFrom");
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
	public void failsWhenEndRecordAppearsBeforeEndOfFile() throws Exception
	{
		importFile("data/cpr/endRecords/D100314.L431101");
	}


	@Test(expected = Exception.class)
	public void failsWhenNoEndRecordExists() throws Exception
	{
		importFile("data/cpr/endRecords/D100315.L431101");
	}


	@Test
	public void canImportPersonNavnebeskyttelse() throws Exception
	{
		importFile("data/cpr/testCPR1/D100314.L431101");

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
	}


	@Test
	public void canImportForaeldreMyndighedBarn() throws Exception
	{
		importFile("data/cpr/testForaeldremyndighed/D100314.L431101");

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
		assertEquals(yyyy_MM_dd.parse("2008-01-01"), rs.getDate("ValidFrom"));
		assertEquals(yyyy_MM_dd.parse("2999-12-31"), rs.getDate("ValidTo"));

		rs.next();

		assertEquals("0004", rs.getString("TypeKode"));
		assertEquals("Far", rs.getString("TypeTekst"));
		assertEquals("", rs.getString("RelationCpr"));
		assertEquals(yyyy_MM_dd.parse("2008-01-01"), rs.getDate("ValidFrom"));
		assertEquals(yyyy_MM_dd.parse("2999-12-31"), rs.getDate("ValidTo"));

		assertTrue(rs.last());
	}


	@Test
	public void canImportUmyndighedVaerge() throws Exception
	{
		importFile("data/cpr/testUmyndigVaerge/D100314.L431101");

		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM UmyndiggoerelseVaergeRelation WHERE CPR='0709614126'");

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
		assertEquals(yyyy_MM_dd.parse("2000-02-28"), rs.getDate("ValidFrom"));
		assertEquals(Dates.THE_END_OF_TIME, rs.getDate("ValidTo"));
		assertTrue(rs.last());
	}


	@Test
	public void ImportU12160Test() throws Exception
	{
		importFile("data/cpr/D100312.L431101");

		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("Select COUNT(*) from Person");
		rs.next();
		assertEquals(100, rs.getInt(1));

		rs = stmt.executeQuery("Select COUNT(*) from BarnRelation");
		rs.next();
		assertEquals(30, rs.getInt(1));

		rs = stmt.executeQuery("Select COUNT(*) from ForaeldreMyndighedRelation");
		rs.next();
		assertEquals(4, rs.getInt(1));

		rs = stmt.executeQuery("Select COUNT(*) from UmyndiggoerelseVaergeRelation");
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
		importFile("data/cpr/D100313.L431101");

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

		// Check Address protection
		rs = stmt.executeQuery("SELECT COUNT(*) FROM Person WHERE NavneBeskyttelseStartDato < NOW() AND NavneBeskyttelseSletteDato > NOW()");
		rs.next();
		assertEquals(1, rs.getInt(1));
	}

	
	@Test
	public void shouldUpdateTheCPRChangesTableForTheCPRGOSService() throws Exception
	{
		importFile("data/cpr/PVIT/D100314.L431101");
		
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM ChangesToCPR ORDER BY CPR");
		rs.next();
		
		assertThat(rs.getString("CPR"), is("0705314543"));
		assertThat(rs.getTimestamp("ModifiedDate"), is(notNullValue()));
		Timestamp t1 = rs.getTimestamp("ModifiedDate");
		
		rs.next();
		
		assertThat(rs.getString("CPR"), is("0705314545"));
		assertThat(rs.getTimestamp("ModifiedDate"), is(notNullValue()));
		Timestamp t2 = rs.getTimestamp("ModifiedDate");
		
		// To make sure the timestamp is updated we wait a second
		// since the granularity is 1 sec.
		
		Thread.sleep(1000);
		
		importFile("data/cpr/PVIT/D100315.L431101");
		
		rs = stmt.executeQuery("SELECT * FROM ChangesToCPR ORDER BY CPR");
		rs.next();
		
		assertThat(rs.getString("CPR"), is("0705314543"));
		assertThat(rs.getTimestamp("ModifiedDate"), is(t1));
		
		rs.next();
		
		assertThat(rs.getString("CPR"), is("0705314545"));
		assertTrue(rs.getTimestamp("ModifiedDate").after(t2));
		
		rs.next();

		assertThat(rs.getString("CPR"), is("0705314547"));
		assertThat(rs.getTimestamp("ModifiedDate"), is(notNullValue()));
		
		assertFalse(rs.next());
	}


	private void importFile(String fileName) throws Exception
	{
		File file = FileUtils.toFile(getClass().getClassLoader().getResource(fileName));
		new CPRImporter().parse(new File[] { file }, new AuditingPersister(connection), null);
	}
}
