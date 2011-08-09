package com.trifork.stamdata.importer.jobs.sikrede;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.persistence.AuditingPersister;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


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
	}

	@After
	public void tearDown() throws Exception
	{
		con.rollback();
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

        rs = stmt.executeQuery("SELECT * FROM SikredeYderRelation WHERE CPR='1607769871' AND type='C'");
        int ydernummer = 4294;
        DateTime ydernummerIkraftDato = new DateTime(2009, 1, 1, 0, 0, 0, 0);
        DateTime gruppekodeRegistreringDato = new DateTime(2000, 12, 1, 0, 0, 0, 0);
        DateTime ydernummerRegistreringsDato = new DateTime(2008, 12, 1, 0, 0, 0, 0);
        String sikringsgruppekode = "1";
        DateTime gruppeKodeIkraftDato = new DateTime(2001, 1, 1, 0, 0, 0, 0);
        assertSikredeYderRelation(rs, ydernummer, ydernummerIkraftDato, ydernummerRegistreringsDato, sikringsgruppekode, gruppeKodeIkraftDato, gruppekodeRegistreringDato);
        rs.close();


        rs = stmt.executeQuery("SELECT * FROM SikredeYderRelation WHERE CPR='1607769871' AND type='F'");
        ydernummer = 4296;
        ydernummerIkraftDato = new DateTime(2007,1,1,0,0,0,0);
        ydernummerRegistreringsDato = new DateTime(2006,12,1,0,0,0,0);
        sikringsgruppekode = "1";
        gruppeKodeIkraftDato = new DateTime(2001,1,1,0,0,0,0);
        gruppekodeRegistreringDato = new DateTime(2000,12,1,0,0,0,0);
        assertSikredeYderRelation(rs, ydernummer, ydernummerIkraftDato, ydernummerRegistreringsDato, sikringsgruppekode, gruppeKodeIkraftDato, gruppekodeRegistreringDato);
        rs.close();

        rs = stmt.executeQuery("SELECT * FROM SikredeYderRelation WHERE CPR='1607769871' AND type='P'");
        ydernummer = 4295;
        ydernummerIkraftDato = new DateTime(2008, 1, 1, 0, 0, 0, 0);
        ydernummerRegistreringsDato = new DateTime(2007,12,1,0,0,0,0);
        sikringsgruppekode = "1";
        gruppeKodeIkraftDato = new DateTime(2001,1,1,0,0,0,0);
        gruppekodeRegistreringDato = new DateTime(2000,12,1,0,0,0,0);
        assertSikredeYderRelation(rs, ydernummer, ydernummerIkraftDato, ydernummerRegistreringsDato, sikringsgruppekode, gruppeKodeIkraftDato, gruppekodeRegistreringDato);
        rs.close();

        stmt.close();
	}

    private void assertSikredeYderRelation(ResultSet rs, int ydernummer, DateTime ydernummerIkraftDato, DateTime ydernummerRegistreringDato, String sikringsgruppeKode, DateTime gruppeKodeIkraftDato, DateTime gruppekodeRegistreringDato) throws SQLException {
        assertTrue("No current 'SikredeYderRelation' row found", rs.next());
        String type = rs.getString("type");
        assertEquals("SikredeYderRelation type '"+type+"' ydernummer matcher ikke", ydernummer, rs.getInt("ydernummer"));
        assertEquals("SikredeYderRelation type '"+type+"' ydernummerIkraftDato matcher ikke", ydernummerIkraftDato, new DateTime(rs.getTimestamp("ydernummerIkraftDato")));
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
		ResultSet rs = stmt.executeQuery("Select COUNT(*) from Sikrede");
		rs.next();
		assertEquals("Præcis én sygesikret forventes oprettet efter import", 1, rs.getInt(1));

		rs = stmt.executeQuery("SELECT COUNT(*) FROM SaerligSundhedskort");
		rs.next();
		assertEquals("Præcis ét sundhedskort forventes efter import", 1, rs.getInt(1));

		rs = stmt.executeQuery("SELECT COUNT(*) FROM SikredeYderRelation");
		rs.next();
		assertEquals("Præcis én YderRelation forventes efter import", 1, rs.getInt(1));
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
		parser.importFiles(new File[] { file }, new AuditingPersister(con));
        //con.commit();
	}
}
