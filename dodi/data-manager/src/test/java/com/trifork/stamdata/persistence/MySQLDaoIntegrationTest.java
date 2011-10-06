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


package com.trifork.stamdata.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.persistence.AuditingPersister;
import com.trifork.stamdata.importer.persistence.CompleteDataset;
import com.trifork.stamdata.importer.persistence.DatabaseTableWrapper;
import com.trifork.stamdata.importer.util.DateUtils;
import com.trifork.stamdata.models.TemporalEntity;


public class MySQLDaoIntegrationTest extends AbstractMySQLIntegrationTest
{
	@Before
	public void setupTable() throws SQLException
	{
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		try
		{
			Statement stmt = con.createStatement();
			try
			{
				stmt.executeUpdate("drop table if exists SDE");
				stmt.executeUpdate("create table SDE(id VARCHAR(20) NOT NULL, data VARCHAR(20), date DATETIME, ModifiedDate DATETIME NOT NULL, ValidFrom DATETIME, ValidTo DATETIME, CreatedDate DATETIME);");
			}
			finally
			{
				stmt.close();
			}
		}
		finally
		{
			con.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPersistCompleteDataset() throws Exception
	{
		CompleteDataset<SDE> dataset = new CompleteDataset<SDE>(SDE.class, t0, t1);
		dataset.addEntity(new SDE(t0, DateUtils.THE_END_OF_TIME));
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		AuditingPersister dao = new AuditingPersister(con);
		dao.persistCompleteDataset(dataset);
		DatabaseTableWrapper<SDE> table = dao.getTable(SDE.class);
		assertTrue(table.fetchEntityVersions(t0, t1));
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(table.getCurrentRowValidTo(), DateUtils.THE_END_OF_TIME);
		assertFalse(table.nextRow());
		con.close();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPersistCompleteDatasetX2() throws Exception
	{
		CompleteDataset<SDE> dataset = new CompleteDataset<SDE>(SDE.class, t0, t1);
		dataset.addEntity(new SDE(t0, DateUtils.THE_END_OF_TIME));
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		AuditingPersister dao = new AuditingPersister(con);
		dao.persistCompleteDataset(dataset);
		dao.persistCompleteDataset(dataset);
		DatabaseTableWrapper<?> table = dao.getTable(SDE.class);
		assertTrue(table.fetchEntityVersions(t0, t1));
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(table.getCurrentRowValidTo(), DateUtils.THE_END_OF_TIME);
		assertFalse(table.nextRow());
		con.close();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPersistCompleteDatasetChangedStringSameValidity() throws Exception
	{
		CompleteDataset<SDE> dataset1 = new CompleteDataset<SDE>(SDE.class, t0, t1);
		CompleteDataset<SDE> dataset2 = new CompleteDataset<SDE>(SDE.class, t0, t1);
		dataset1.addEntity(new SDE(t0, DateUtils.THE_END_OF_TIME, "1", "a"));
		dataset2.addEntity(new SDE(t0, DateUtils.THE_END_OF_TIME, "1", "b"));
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		AuditingPersister dao = new AuditingPersister(con);
		dao.persistCompleteDataset(dataset1);
		dao.persistCompleteDataset(dataset2);
		DatabaseTableWrapper<?> table = dao.getTable(SDE.class);
		assertTrue(table.fetchEntityVersions(t0, t1));
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(DateUtils.THE_END_OF_TIME, table.getCurrentRowValidTo());
		assertEquals("b", table.currentRS.getString("data"));
		assertFalse(table.nextRow());
		con.close();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPersistCompleteDatasetChangedDateSameValidity() throws Exception
	{
		CompleteDataset<SDE> dataset1 = new CompleteDataset<SDE>(SDE.class, t0, t1);
		CompleteDataset<SDE> dataset2 = new CompleteDataset<SDE>(SDE.class, t0, t1);
		dataset1.addEntity(new SDE(t0, DateUtils.THE_END_OF_TIME, "1", "a", t3));
		dataset2.addEntity(new SDE(t0, DateUtils.THE_END_OF_TIME, "1", "a", t4));
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		AuditingPersister dao = new AuditingPersister(con);
		dao.persistCompleteDataset(dataset1);
		dao.persistCompleteDataset(dataset2);
		DatabaseTableWrapper<?> table = dao.getTable(SDE.class);
		assertTrue(table.fetchEntityVersions(t0, t1));
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(DateUtils.THE_END_OF_TIME, table.getCurrentRowValidTo());
		assertEquals(t4.getTime(), table.currentRS.getTimestamp("date").getTime());
		assertFalse(table.nextRow());
		con.close();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPersistCompleteDatasetChangedDataNewValidFrom() throws Exception
	{
		CompleteDataset<SDE> dataset1 = new CompleteDataset<SDE>(SDE.class, t0, t1);
		CompleteDataset<SDE> dataset2 = new CompleteDataset<SDE>(SDE.class, t1, t2);
		dataset1.addEntity(new SDE(t0, DateUtils.THE_END_OF_TIME, "1", "a"));
		dataset2.addEntity(new SDE(t1, DateUtils.THE_END_OF_TIME, "1", "b"));
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		AuditingPersister dao = new AuditingPersister(con);
		dao.persistCompleteDataset(dataset1);
		dao.persistCompleteDataset(dataset2);
		DatabaseTableWrapper<?> table = dao.getTable(SDE.class);
		assertTrue(table.fetchEntityVersions(t0, t0)); // Get the old version
		assertEquals(t0, table.getCurrentRowValidFrom());
		assertEquals(t1, table.getCurrentRowValidTo());
		assertEquals("a", table.currentRS.getString("data"));
		assertFalse(table.nextRow());
		assertTrue(table.fetchEntityVersions(t2, t2)); // Get the new version
		assertEquals(table.getCurrentRowValidFrom(), t1);
		assertEquals(DateUtils.THE_END_OF_TIME, table.getCurrentRowValidTo());
		assertEquals("b", table.currentRS.getString("data"));
		assertFalse(table.nextRow());
		con.close();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPersistCompleteDatasetChangedDataNewValidToNoDataChange() throws Exception
	{
		CompleteDataset<SDE> dataset1 = new CompleteDataset<SDE>(SDE.class, t0, t1);
		CompleteDataset<SDE> dataset2 = new CompleteDataset<SDE>(SDE.class, t1, t2);
		CompleteDataset<SDE> dataset3 = new CompleteDataset<SDE>(SDE.class, t2, t1000);

		// Normal t0 -> THE_END_OF_TIME

		dataset1.addEntity(new SDE(t0, DateUtils.THE_END_OF_TIME, "1", "a"));

		// Limit validTo to T1 no data change.

		dataset2.addEntity(new SDE(t0, t1, "1", "a"));

		// Extend validTo to after THE_END_OF_TIME
		dataset3.addEntity(new SDE(t0, t1000, "1", "a"));

		Connection con = MySQLConnectionManager.getAutoCommitConnection();

		AuditingPersister dao = new AuditingPersister(con);
		dao.persistCompleteDataset(dataset1);
		DatabaseTableWrapper<?> table = dao.getTable(SDE.class);
		assertTrue(table.fetchEntityVersions(t0, DateUtils.THE_END_OF_TIME));
		assertEquals(t0, table.getCurrentRowValidFrom());
		assertEquals(DateUtils.THE_END_OF_TIME, table.getCurrentRowValidTo());
		assertEquals("a", table.currentRS.getString("data"));
		assertFalse(table.nextRow());

		// Persist validTo = T1
		dao.persistCompleteDataset(dataset2);
		assertTrue(table.fetchEntityVersions(t0, DateUtils.THE_END_OF_TIME));
		assertEquals(t0, table.getCurrentRowValidFrom());
		assertEquals(t1, table.getCurrentRowValidTo());
		assertEquals("a", table.currentRS.getString("data"));
		assertFalse(table.nextRow());

		// Persist validTo = T1000
		dao.persistCompleteDataset(dataset3);
		assertTrue(table.fetchEntityVersions(t0, DateUtils.THE_END_OF_TIME));
		assertEquals(t0, table.getCurrentRowValidFrom());
		assertEquals(t1000, table.getCurrentRowValidTo());
		assertEquals("a", table.currentRS.getString("data"));
		assertFalse(table.nextRow());
		con.close();
	}


	@Entity
	public static class SDE implements TemporalEntity
	{
		Date validfrom, validto;
		String id = "1"; // default value
		String data = "a"; // default value
		Date date = DateUtils.toDate(2001, 1, 1, 1, 2, 3);

		public SDE(Date validFrom, Date validTo)
		{
			this.validfrom = validFrom;
			this.validto = validTo;
		}

		public SDE(Date validFrom, Date validTo, String id, String data)
		{
			this.validfrom = validFrom;
			this.validto = validTo;
			this.data = data;
			this.id = id;
		}

		public SDE(Date validFrom, Date validTo, String id, String data, Date date)
		{
			this.validfrom = validFrom;
			this.validto = validTo;
			this.data = data;
			this.id = id;
			this.date = date;
		}

		@Override
		public Date getValidFrom()
		{
			return validfrom;
		}

		@Override
		public Date getValidTo()
		{
			return validto;
		}

		@Column
		public String getData()
		{
			return data;
		}

		@Id
		@Column(name = "id")
		public Object getID()
		{
			return id;
		}

		@Column
		public Date getDate()
		{
			return date;
		}
	}
}
