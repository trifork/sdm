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


package com.trifork.stamdata.importer.jobs.takst;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.importer.config.ConnectionManager;
import com.trifork.stamdata.importer.jobs.takst.model.Doseringskode;
import com.trifork.stamdata.importer.persistence.AuditingPersister;
import com.trifork.stamdata.importer.util.Dates;


public class TakstImporterIntegrationTest
{
	@Before
	@After
	public void cleanup() throws Exception
	{
		Connection connection = new ConnectionManager().getAutoCommitConnection();
		Statement stmt = connection.createStatement();

		stmt.executeQuery("TRUNCATE TABLE LaegemiddelDoseringRef");
		stmt.executeQuery("TRUNCATE TABLE TakstVersion");

		ConnectionManager.closeQuietly(connection);
	}

	@Test
	public void testAreRequiredInputFilesPresent() throws Exception
	{
		TakstImporter importer = new TakstImporter();
		File dir = FileUtils.toFile(getClass().getClassLoader().getResource("data/takst/initial/"));

		assertTrue(importer.validateInputStructure(dir.listFiles()));
	}

	@Test
	public void testAreRequiredInputFilesPresent2() throws Exception
	{
		TakstImporter ti = new TakstImporter();
		File dir = FileUtils.toFile(getClass().getClassLoader().getResource("data/takst/incomplete/"));

		assertFalse(ti.validateInputStructure(dir.listFiles()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testLaegemiddelDoseringRef() throws Exception
	{
		Date from = Dates.toDate(2008, 01, 01);
		Date to = Dates.toDate(2009, 01, 01);

		Takst takst = new Takst(from, to);

		Doseringskode d = new Doseringskode();
		d.setDoseringskode(1l);
		d.setDrugid(2l);

		List<Doseringskode> dk = new ArrayList<Doseringskode>();
		dk.add(d);

		TakstDataset<Doseringskode> dataset = new TakstDataset<Doseringskode>(takst, dk, Doseringskode.class);
		takst.addDataset(dataset, Doseringskode.class);

		Connection con = new ConnectionManager().getAutoCommitConnection();

		AuditingPersister persister = new AuditingPersister(con);
		
		persister.persistCompleteDataset(dataset);
		ResultSet rs = con.createStatement().executeQuery("SELECT COUNT(*) FROM LaegemiddelDoseringRef");
		rs.next();

		assertEquals(1, rs.getInt(1));

		persister.persistCompleteDataset(dataset);
		rs = con.createStatement().executeQuery("SELECT COUNT(*) FROM LaegemiddelDoseringRef");
		rs.next();

		assertEquals(1, rs.getInt(1));
	}
}
