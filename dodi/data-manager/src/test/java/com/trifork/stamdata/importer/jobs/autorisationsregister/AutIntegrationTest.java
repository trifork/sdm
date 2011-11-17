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


package com.trifork.stamdata.importer.jobs.autorisationsregister;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.*;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.junit.*;

import com.trifork.stamdata.Entities;
import com.trifork.stamdata.importer.config.ConnectionManager;
import com.trifork.stamdata.importer.persistence.*;
import com.trifork.stamdata.importer.util.Dates;
import com.trifork.stamdata.models.autorisationsregister.Autorisation;


public class AutIntegrationTest
{
	private static File initial;
	private static File next;
	private static File invalid;

	private Connection connection;

	private static Autorisationsregisterudtraek initialCompares;
	
	static
	{
		Date now = new Date();
		
		initialCompares = new Autorisationsregisterudtraek(now);
		
		initialCompares.add(createAutorisation("0013F", "0101251489", "Jørgen", "Bondo", "7170", now));
		initialCompares.add(createAutorisation("0013H", "0101280063", "Tage Søgaard", "Johnsen", "7170", now));
		initialCompares.add(createAutorisation("0013J", "0101280551", "Svend Christian", "Bertelsen", "7170", now));
		initialCompares.add(createAutorisation("0013K", "0101280896", "Lilian", "Frederiksen", "7170", now));
	}
	
	public static Autorisation createAutorisation(String nummer, String cpr, String fornavn, String efternavn, String uddKode, Date validFrom)
	{
		Autorisation a = new Autorisation();
		
		a.setAutorisationnummer(nummer);
		a.setCpr(cpr);
		a.setFornavn(fornavn);
		a.setEfternavn(efternavn);
		a.setUddannelsesKode(uddKode);
		a.setValidFrom(validFrom);
		a.setValidTo(Dates.THE_END_OF_TIME);
		
		return a;
	}

	@BeforeClass
	public static void init()
	{
		ClassLoader classLoader = AutIntegrationTest.class.getClassLoader();

		initial = FileUtils.toFile(classLoader.getResource("data/aut/valid/20090915AutDK.csv"));
		next = FileUtils.toFile(classLoader.getResource("data/aut/valid/20090918AutDK.csv"));
		invalid = FileUtils.toFile(classLoader.getResource("data/aut/invalid/20090915AutDK.csv"));
	}

	@Before
	public void setUp() throws SQLException
	{
		connection = new ConnectionManager().getConnection();
		connection.createStatement().execute("TRUNCATE TABLE Autorisation");
		connection.commit();
	}

	@After
	public void tearDown() throws SQLException
	{
		connection.rollback();
		connection.close();
	}

	@Test
	public void testImport() throws Exception
	{
		new AutorisationImporter().parse(new File[] { initial }, new AuditingPersister(connection), null);

		Statement stmt = connection.createStatement();

		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + Entities.getEntityTypeDisplayName(Autorisation.class));
		rs.next();

		assertEquals("Number of records in database.", initialCompares.getEntities().size(), rs.getInt(1));

		rs = stmt.executeQuery("SELECT * FROM " + Entities.getEntityTypeDisplayName(Autorisation.class));

		for (int i = 0; i < initialCompares.getEntities().size(); i++)
		{
			rs.next();
			Autorisation compare = initialCompares.getEntityById(rs.getString("Autorisationsnummer"));
			assertEquals(compare.getCpr(), rs.getString("cpr"));
			assertEquals(compare.getFornavn(), rs.getString("Fornavn"));
			assertEquals(compare.getEfternavn(), rs.getString("Efternavn"));
			assertEquals(compare.getUddannelsesKode(), rs.getString("UddannelsesKode"));
		}
	}

	@Test(expected = Exception.class)
	public void should_not_allow_the_same_version_to_be_imported_twice() throws Exception
	{
		AutorisationImporter importer = new AutorisationImporter();
		AuditingPersister persister = new AuditingPersister(connection);

		File[] files = new File[] { initial };

		importer.parse(files, persister, null);
		importer.parse(files, persister, null);
	}

	@Test
	public void testDelta() throws Exception
	{
		AutorisationImporter importer = new AutorisationImporter();
		AuditingPersister persister = new AuditingPersister(connection);

		importer.parse(new File[] { initial }, persister, null);
		importer.parse(new File[] { next }, persister, null);

		Date now = new Date();
		
		Dataset<Autorisation> nextCompares = new Dataset<Autorisation>(Autorisation.class);
		nextCompares.add(createAutorisation("0013H", "0101280063", "Tage Søgaard", "Johnsen", "7170", now));
		nextCompares.add(createAutorisation("0013M", "0101340074", "Ester Ruth", "Østerby", "7170", now));
		nextCompares.add(createAutorisation("0013J", "0101280551", "Svend Christian", "Bertelsen", "7170", now));
		nextCompares.add(createAutorisation("0013K", "0101280896", "Lilian", "Frederiksen", "7170", now));
		nextCompares.add(createAutorisation("0013L", "0101290565", "Eli", "Heering","7170", now));

		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + Entities.getEntityTypeDisplayName(Autorisation.class) + " WHERE validfrom < '2009-09-19' and validto > '2009-09-19'");
		rs.next();
		assertEquals(nextCompares.getEntities().size(), rs.getInt(1));

		rs = stmt.executeQuery("SELECT * FROM " + Entities.getEntityTypeDisplayName(Autorisation.class) + " WHERE validfrom < '2009-09-19' AND validto > '2009-09-19'");

		for (int i = 0; i < nextCompares.getEntities().size(); i++)
		{
			rs.next();
			Autorisation compare = nextCompares.getEntityById(rs.getString("Autorisationsnummer"));
			assertEquals(compare.getCpr(), rs.getString("cpr"));
			assertEquals(compare.getFornavn(), rs.getString("Fornavn"));
			assertEquals(compare.getEfternavn(), rs.getString("Efternavn"));
			assertEquals(compare.getUddannelsesKode(), rs.getString("UddannelsesKode"));
		}

		stmt.close();
	}

	@Test(expected = Exception.class)
	public void testInvalid() throws Exception
	{
		File[] files = new File[] { invalid };
		new AutorisationImporter().parse(files, new AuditingPersister(connection), null);
	}
}
