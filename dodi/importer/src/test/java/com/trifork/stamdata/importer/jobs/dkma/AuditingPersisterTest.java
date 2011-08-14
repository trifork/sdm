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

package com.trifork.stamdata.importer.jobs.dkma;

import static com.trifork.stamdata.Helpers.*;
import static junit.framework.Assert.*;

import java.io.File;
import java.net.URL;
import java.sql.*;

import org.apache.commons.io.FileUtils;
import org.junit.*;

import com.trifork.stamdata.Helpers;
import com.trifork.stamdata.importer.persistence.*;
import com.trifork.stamdata.importer.util.Dates;


/**
 * Integration test of the database access layer. Tests that a dataset can be
 * written to the database
 * 
 * @author Anders Bo Christensen
 */
public class AuditingPersisterTest
{
	private Connection connection;

	@Before
	public void setUp() throws Exception
	{
		connection = Helpers.getConnection();
	}

	@After
	public void tearDown() throws Exception
	{
		connection.rollback();
		connection.close();
	}

	@Test
	public void ImportTest() throws Exception
	{
		TakstVersion takst = parseTakst("data/takst/initial");

		Statement statement = connection.createStatement();
		AuditingPersister persister = new AuditingPersister(connection);

		persister.persist(takst.getDatasets());

		assertEquals(92, getRecordCount(persister));

		ResultSet rs = statement.executeQuery("SELECT * FROM Laegemiddel WHERE DrugName LIKE 'Kemadrin';");

		assertTrue("Did not find expected Laegemiddel Kemadrin", rs.next());

		assertEquals(Dates.THE_END_OF_TIME, rs.getTimestamp("ValidTo", Dates.DK_CALENDAR));

		statement.close();
	}

	@Test
	public void UpdateTest() throws Exception
	{
		TakstVersion takstinit = parseTakst("data/takst/initial");
		TakstVersion takstupd = parseTakst("data/takst/update");

		Statement statement = connection.createStatement();
		AuditingPersister persister = new AuditingPersister(connection);

		persister.persist(takstinit.getDatasets());
		persister.persist(takstupd.getDatasets());

		assertEquals(93, getRecordCount(persister));

		ResultSet rs = statement.executeQuery("SELECT * FROM Laegemiddel WHERE DrugName LIKE 'Kemadrin'");

		assertTrue("Did not find expected Laegemiddel Kemadrin", rs.next());

		assertEquals(Dates.newTimestampDK(2009,07,30), rs.getTimestamp("ValidTo", Dates.DK_CALENDAR));

		rs = statement.executeQuery("SELECT * FROM Laegemiddel WHERE DrugName LIKE 'Kemadron'");

		assertTrue("Did not find expected Laegemiddel Kemadron", rs.next());

		assertEquals(Dates.newTimestampDK(2999, 12,31), rs.getTimestamp("ValidTo", Dates.DK_CALENDAR));

		statement.close();
	}

	@Test
	public void DeleteTest() throws Exception
	{
		TakstVersion takstinit = parseTakst("data/takst/initial");
		TakstVersion deleteupd = parseTakst("data/takst/delete");

		Statement statement = connection.createStatement();
		AuditingPersister persister = new AuditingPersister(connection);

		persister.persist(takstinit.getDatasets());
		persister.persist(deleteupd.getDatasets());

		assertEquals(92, getRecordCount(persister));

		ResultSet rs = statement.executeQuery("SELECT * FROM Laegemiddel WHERE DrugName LIKE 'Kemadrin'");

		assertTrue("Did not find expected Laegemiddel Kemadrin", rs.next());

		assertEquals(Dates.newTimestampDK(2009, 7, 31), rs.getTimestamp("ValidTo", Dates.DK_CALENDAR));

		statement.close();
	}

	@Test
	@Ignore
	public void ImportCompleteDKMA() throws Exception
	{
		AuditingPersister persister = new AuditingPersister(connection);
		
		TakstVersion takst = parseTakst("data/takst/realtakst");
		persister.persist(takst.getDatasets());
	}

	private int getRecordCount(Persister versionedDao) throws SQLException
	{
		Statement statement = connection.createStatement();

		ResultSet rs = statement.executeQuery("SELECT COUNT(*) AS count FROM Laegemiddel");

		rs.next();

		return rs.getInt("count");
	}

	private TakstVersion parseTakst(String inputDirectory) throws Exception
	{
		URL fileURL = getClass().getClassLoader().getResource(inputDirectory);
		File file = FileUtils.toFile(fileURL);
		DKMAParser tp = new DKMAParser(FAKE_TIME_GAP);
		TakstVersion takst = tp.parseFiles(file.listFiles());

		return takst;
	}
}
