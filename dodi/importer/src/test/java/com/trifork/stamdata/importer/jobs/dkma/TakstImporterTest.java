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
import static org.junit.Assert.*;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.junit.*;

import com.trifork.stamdata.Helpers;
import com.trifork.stamdata.importer.jobs.dkma.model.Doseringskode;
import com.trifork.stamdata.importer.persistence.*;
import com.trifork.stamdata.importer.util.Dates;


public class TakstImporterTest
{
	private Connection connection;

	@Before
	public void cleanup() throws Exception
	{
		connection = Helpers.getConnection();
		Statement stmt = connection.createStatement();

		stmt.executeQuery("TRUNCATE TABLE LaegemiddelDoseringRef");
		stmt.executeQuery("TRUNCATE TABLE TakstVersion");
	}

	@After
	public void tearDown() throws SQLException
	{
		connection.close();
	}

	@Test
	public void testAreRequiredInputFilesPresent() throws Exception
	{
		DKMAParser importer = new DKMAParser(FAKE_TIME_GAP);
		File dir = FileUtils.toFile(getClass().getClassLoader().getResource("data/takst/initial/"));

		assertTrue(importer.checkFileSet(dir.listFiles()));
	}

	@Test
	public void testAreRequiredInputFilesPresent2() throws Exception
	{
		DKMAParser ti = new DKMAParser(FAKE_TIME_GAP);
		File dir = FileUtils.toFile(getClass().getClassLoader().getResource("data/takst/incomplete/"));

		assertFalse(ti.checkFileSet(dir.listFiles()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testLaegemiddelDoseringRef() throws Exception
	{
		Date from = Dates.toCETDate(2008, 01, 01);
		Date to = Dates.toCETDate(2009, 01, 01);

		Takst takst = new Takst(from, to);

		Doseringskode d = new Doseringskode();
		d.setDoseringskode(1l);
		d.setDrugid(2l);

		List<Doseringskode> dk = new ArrayList<Doseringskode>();
		dk.add(d);

		TakstDataset<Doseringskode> dataset = new TakstDataset<Doseringskode>(takst, dk, Doseringskode.class);
		takst.addDataset(dataset);

		Connection connection = Helpers.getConnection();

		AuditingPersister persister = new AuditingPersister(connection);

		persister.persistCompleteDataset(dataset);
		ResultSet rs = connection.createStatement().executeQuery("SELECT COUNT(*) FROM LaegemiddelDoseringRef");
		rs.next();

		assertEquals(1, rs.getInt(1));

		persister.persistCompleteDataset(dataset);
		rs = connection.createStatement().executeQuery("SELECT COUNT(*) FROM LaegemiddelDoseringRef");
		rs.next();

		assertEquals(1, rs.getInt(1));
	}
}
