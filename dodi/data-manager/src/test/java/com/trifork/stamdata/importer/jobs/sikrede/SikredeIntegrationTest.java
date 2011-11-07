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
package com.trifork.stamdata.importer.jobs.sikrede;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.persistence.AuditingPersister;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.*;

import static org.junit.Assert.*;


public class SikredeIntegrationTest
{
	private Connection con;

	@Before
	public void cleanDatabase() throws Exception
	{
		con = MySQLConnectionManager.getConnection();

		Statement statement = con.createStatement();
		statement.execute("truncate table Sikrede");
		statement.execute("truncate table SikredeYderRelation");
		statement.execute("truncate table SaerligSundhedskort");
		statement.execute("truncate table AssignedDoctor");
	}

	@After
	public void tearDown() throws Exception
	{
		con.rollback();
        //con.commit();
		con.close();
	}

	@Test
	public void should_extract_the_correct_from_the_data_file() throws Exception
	{
		importFile("data/sikrede/20110701.SIKREDE");

		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM Sikrede WHERE CPR='1607769871'");
		assertTrue("No 'Sikrede' row found for CPR 1607769871", rs.next());
		assertEquals("ValidFrom matcher ikke",new DateTime(2011, 7, 13, 0, 0, 0, 0), new DateTime(rs.getTimestamp("ValidFrom")));
        assertEquals("foelgeskabsPersonCpr matcher ikke","2108751234", rs.getString("foelgeskabsPersonCpr"));
        assertEquals("kommunekode matcher ikke","851", rs.getString("kommunekode"));
        assertEquals("kommunekodeIkraftDato matcher ikke", new DateTime(2001,2, 1, 0, 0, 0, 0), new DateTime(rs.getTimestamp("kommunekodeIkraftDato")));
        assertNull("Status", rs.getString("status"));
        assertEquals("bevisIkraftDato matcher ikke", new DateTime(2011, 2, 1, 0, 0, 0, 0), new DateTime(rs.getTimestamp("bevisIkraftDato")));
        assertEquals("forsikringsinstans matcher ikke", "Sygesikring", rs.getString("forsikringsinstans"));
        assertEquals("forsikringsinstansKode matcher ikke", "0123456789", rs.getString("forsikringsinstansKode"));
        assertEquals("forsikringsnummer matcher ikke", "010101010101010", rs.getString("forsikringsnummer"));
        assertEquals("sslGyldigFra matcher ikke", new DateTime(2000, 1, 1, 0,0,0,0), new DateTime(rs.getTimestamp("sslGyldigFra")));
        assertEquals("sslGyldigTil matcher ikke", new DateTime(9999,12,31, 0,0,0,0), new DateTime(rs.getTimestamp("sslGyldigTil")));
        assertEquals("socialLand matcher ikke", "Danmark", rs.getString("socialLand"));
        assertEquals("socialLandKode matcher ikke", "DK", rs.getString("socialLandKode"));
        rs.close();

        rs = stmt.executeQuery("SELECT * FROM SikredeYderRelation WHERE CPR='1607769871' AND type='F'");
        int fremtidigYdernummer = 4296;
        DateTime fremtidigYdernummerIkraftDato = new DateTime(2012,1,1,0,0,0,0);
        DateTime fremtidigYdernummerUdlobDato = null; // Fremtidigt valg af læge har aldrig udløbsdato
        DateTime fremtidigYdernummerRegistreringsDato = new DateTime(2006,12,1,0,0,0,0);
        String fremtidigSikringsgruppekode = "1";
        DateTime fremtidigGruppeKodeIkraftDato = new DateTime(2001,1,1,0,0,0,0);
        DateTime fremtidigGruppekodeRegistreringDato = new DateTime(2000,12,1,0,0,0,0);
        assertSikredeYderRelation(rs, fremtidigYdernummer, fremtidigYdernummerIkraftDato, fremtidigYdernummerUdlobDato, fremtidigYdernummerRegistreringsDato, fremtidigSikringsgruppekode, fremtidigGruppeKodeIkraftDato, fremtidigGruppekodeRegistreringDato);
        rs.close();

        rs = stmt.executeQuery("SELECT * FROM SikredeYderRelation WHERE CPR='1607769871' AND type='C'");
        int nuvaerendeYdernummer = 4294;
        DateTime nuvaerendeYdernummerUdlobDato = fremtidigYdernummerIkraftDato; //Nuværende valg af læge har udløbsdato i dette tilfælde, fordi der er et fremtidigt valg af læge.
        DateTime nuvaerendeYdernummerIkraftDato = new DateTime(2009, 1, 1, 0, 0, 0, 0);
        DateTime nuvaerendeGruppekodeRegistreringDato = new DateTime(2000, 12, 1, 0, 0, 0, 0);
        DateTime nuvaerendeYdernummerRegistreringsDato = new DateTime(2008, 12, 1, 0, 0, 0, 0);
        String nuvaerendeSikringsgruppekode = "1";
        DateTime nuvaerendeGruppeKodeIkraftDato = new DateTime(2001, 1, 1, 0, 0, 0, 0);
        assertSikredeYderRelation(rs, nuvaerendeYdernummer, nuvaerendeYdernummerIkraftDato, nuvaerendeYdernummerUdlobDato, nuvaerendeYdernummerRegistreringsDato, nuvaerendeSikringsgruppekode, nuvaerendeGruppeKodeIkraftDato, nuvaerendeGruppekodeRegistreringDato);
        rs.close();

        rs = stmt.executeQuery("SELECT * FROM SikredeYderRelation WHERE CPR='1607769871' AND type='P'");
        int tidligereYdernummer = 4295;
        DateTime tidligereYdernummerIkraftDato = new DateTime(2008, 1, 1, 0, 0, 0, 0);
        DateTime tidligereYdernummerUdlobDato = nuvaerendeYdernummerIkraftDato; //Tidligere lægevalg har altid udløbsdato = nuværende lægevalgs ikraftdato.
        DateTime tidligereYdernummerRegistreringsDato = new DateTime(2007,12,1,0,0,0,0);
        String tidligereSikringsgruppekode = "1";
        DateTime tidligereGruppeKodeIkraftDato = new DateTime(2001,1,1,0,0,0,0);
        DateTime tidligereGruppekodeRegistreringDato = new DateTime(2000,12,1,0,0,0,0);
        assertSikredeYderRelation(rs, tidligereYdernummer, tidligereYdernummerIkraftDato, tidligereYdernummerUdlobDato, tidligereYdernummerRegistreringsDato, tidligereSikringsgruppekode, tidligereGruppeKodeIkraftDato, tidligereGruppekodeRegistreringDato);
        rs.close();

        rs = stmt.executeQuery("SELECT COUNT(*) FROM AssignedDoctor WHERE patientCpr=UPPER(SHA1('1607769871'))");
        rs.next();
        assertEquals("AssignedDoctor rows for CPR '1607769871' should be 3", 3, rs.getInt(1));
        rs.close();

        stmt.close();
	}

    private void assertSikredeYderRelation(ResultSet rs, int ydernummer, DateTime ydernummerIkraftDato, DateTime ydernummerUdlobDato, DateTime ydernummerRegistreringDato, String sikringsgruppeKode, DateTime gruppeKodeIkraftDato, DateTime gruppekodeRegistreringDato) throws SQLException {
        assertTrue("No current 'SikredeYderRelation' row found", rs.next());
        String type = rs.getString("type");
        assertEquals("SikredeYderRelation type '"+type+"' ydernummer matcher ikke", ydernummer, rs.getInt("ydernummer"));
        assertEquals("SikredeYderRelation type '"+type+"' ydernummerIkraftDato matcher ikke", ydernummerIkraftDato, new DateTime(rs.getTimestamp("ydernummerIkraftDato")));
        Timestamp udlobDato = rs.getTimestamp("ydernummerUdlobDato");
        assertEquals("SikredeYderRelation type '"+type+"' ydernummerUdlobDato matcher ikke", ydernummerUdlobDato,  udlobDato == null ? null : new DateTime(udlobDato));
        assertEquals("SikredeYderRelation type '"+type+"' ydernummerRegistreringDato matcher ikke", ydernummerRegistreringDato, new DateTime(rs.getTimestamp("ydernummerRegistreringDato")));
        assertEquals("SikredeYderRelation type '"+type+"' sikringsgruppeKode matcher ikke", sikringsgruppeKode, rs.getString("sikringsgruppeKode"));
        assertEquals("SikredeYderRelation type '"+type+"' gruppeKodeIkraftDato matcher ikke", gruppeKodeIkraftDato, new DateTime(rs.getTimestamp("gruppeKodeIkraftDato")));
        assertEquals("SikredeYderRelation type '" + type + "' gruppekodeRegistreringDato matcher ikke", gruppekodeRegistreringDato, new DateTime(rs.getTimestamp("gruppekodeRegistreringDato")));
    }

    @Test
	public void canImportPersonWithSaerligSundhedskort() throws Exception
	{
		importFile("data/sikrede/20110701.SIKREDE_SSK");

		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("Select * from Sikrede where cpr='1607769871'");
		assertTrue("Der forventes en sygesikret oprettet efter import", rs.next());
        assertEquals("ValidFrom matcher ikke",new DateTime(2011, 7, 13, 0, 0, 0, 0), new DateTime(rs.getTimestamp("ValidFrom")));
        assertNull("foelgeskabsPersonCpr skal være null", rs.getString("foelgeskabsPersonCpr"));
        assertEquals("kommunekode matcher ikke","851", rs.getString("kommunekode"));
        assertEquals("kommunekodeIkraftDato matcher ikke", new DateTime(2001,2, 1, 0, 0, 0, 0), new DateTime(rs.getTimestamp("kommunekodeIkraftDato")));

        assertEquals("Status", "G", rs.getString("status"));
        assertEquals("bevisIkraftDato matcher ikke", new DateTime(2011, 2, 1, 0, 0, 0, 0), new DateTime(rs.getTimestamp("bevisIkraftDato")));

        assertEquals("forsikringsinstans matcher ikke", "Sygesikringen", rs.getString("forsikringsinstans"));
        assertEquals("forsikringsinstansKode matcher ikke", "9876543210", rs.getString("forsikringsinstansKode"));
        assertEquals("forsikringsnummer matcher ikke", "999999999999999", rs.getString("forsikringsnummer"));
        assertEquals("sslGyldigFra matcher ikke", new DateTime(2009, 1, 1, 0,0,0,0), new DateTime(rs.getTimestamp("sslGyldigFra")));
        assertEquals("sslGyldigTil matcher ikke", new DateTime(9999,12,31, 0,0,0,0), new DateTime(rs.getTimestamp("sslGyldigTil")));
        assertEquals("socialLand matcher ikke", "Sverige", rs.getString("socialLand"));
        assertEquals("socialLandKode matcher ikke", "SE", rs.getString("socialLandKode"));
        rs.close();

        rs = stmt.executeQuery("SELECT * FROM SikredeYderRelation WHERE CPR='1607769871' AND type='P'");
        assertFalse("Der skal ikke være importeret noget tidligere valg af ydernummer", rs.next());
        rs.close();

        rs = stmt.executeQuery("SELECT * FROM SikredeYderRelation WHERE CPR='1607769871' AND type='F'");
        assertFalse("Der skal ikke være importeret noget fremtidigt valg af ydernummer", rs.next());
        rs.close();

		rs = stmt.executeQuery("SELECT COUNT(*) FROM SikredeYderRelation");
		rs.next();
		assertEquals("Præcis én YderRelation forventes efter import", 1, rs.getInt(1));
        rs.close();

        rs = stmt.executeQuery("SELECT * FROM SikredeYderRelation WHERE CPR='1607769871' AND type='C'");
        int ydernummer = 4294;
        DateTime ydernummerIkraftDato = new DateTime(2001, 1, 1, 0, 0, 0, 0);
        DateTime ydernummerUdlobDato = null; //Der er ingen fremtidige yderrelationer, og derfor ingen udløbsdato for valg af nuværende yder
        DateTime gruppekodeRegistreringDato = new DateTime(2000, 12, 1, 0, 0, 0, 0);
        DateTime ydernummerRegistreringsDato = new DateTime(2000, 12, 1, 0, 0, 0, 0);
        String sikringsgruppekode = "1";
        DateTime gruppeKodeIkraftDato = new DateTime(2001, 1, 1, 0, 0, 0, 0);
        assertSikredeYderRelation(rs, ydernummer, ydernummerIkraftDato, ydernummerUdlobDato, ydernummerRegistreringsDato, sikringsgruppekode, gruppeKodeIkraftDato, gruppekodeRegistreringDato);
        rs.close();

        rs = stmt.executeQuery("SELECT COUNT(*) FROM AssignedDoctor WHERE patientCpr=UPPER(SHA1('1607769871'))");
        rs.next();
        assertEquals("AssignedDoctor rows for CPR '1607769871' should be 1", 1, rs.getInt(1));
        rs.close();

		rs = stmt.executeQuery("SELECT * FROM SaerligSundhedskort where cpr='1607769871'");
		assertTrue("Præcis ét sundhedskort forventes efter import", rs.next());
        assertEquals("adresseLinje1", "Hovedgatan 1024", rs.getString("adresseLinje1"));
        assertEquals("adresseLinje2", "2.tv", rs.getString("adresseLinje2"));
        assertEquals("bopelsLand", "Sverige", rs.getString("bopelsLand"));
        assertEquals("bopelsLandKode", "SE", rs.getString("bopelsLandKode"));
        assertEquals("emailAdresse", "frj@trifork.com", rs.getString("emailAdresse"));
        assertEquals("familieRelationCpr", "2108751234", rs.getString("familieRelationCpr"));
        assertEquals("foedselsDato", new DateTime(1976, 7, 16, 0, 0, 0, 0), new DateTime(rs.getTimestamp("foedselsDato")));
        assertEquals("sskGyldigFra", new DateTime(2009, 1, 1, 0, 0, 0, 0), new DateTime(rs.getTimestamp("sskGyldigFra")));
        assertEquals("sskGyldigTil", new DateTime(9999, 12, 31, 0, 0, 0, 0), new DateTime(rs.getTimestamp("sskGyldigTil")));
        assertEquals("mobilNummer", "0044123456789", rs.getString("mobilNummer"));
        assertEquals("postnummerBy", "21120 MAlMO", rs.getString("postnummerBy"));
        rs.close();
	}

	@Test
	public void canImportSikredeWithoutSaerligSundhedskort() throws Exception
	{
		importFile("data/sikrede/20110701.SIKREDE");

		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("Select COUNT(*) from Sikrede");
		rs.next();
		assertEquals("Én sygesikret forventes oprettet efter import", 1, rs.getInt(1));

		rs = stmt.executeQuery("SELECT COUNT(*) FROM SaerligSundhedskort");
		rs.next();
		assertEquals("Intet sundhedskort skal oprettes", 0, rs.getInt(1));

		rs = stmt.executeQuery("SELECT COUNT(*) FROM SikredeYderRelation");
		rs.next();
		assertEquals("current- previous and future YderRelation forventet efter import", 3, rs.getInt(1));
	}

	private void importFile(String fileName) throws Exception
	{
		SikredeParser parser = new SikredeParser();
		File file = FileUtils.toFile(getClass().getClassLoader().getResource(fileName));
		parser.parse(new File[] { file }, new AuditingPersister(con), null);
        //con.commit();
	}
}
