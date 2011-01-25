package com.trifork.sdm.importer.persistence.mysql;


import static org.junit.Assert.*;

import java.sql.*;
import java.util.Date;

import javax.persistence.*;

import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.AbstractRecord;
import com.trifork.stamdata.DateUtils;
import com.trifork.stamdata.importer.persistence.MySQLTemporalDao;
import com.trifork.stamdata.importer.persistence.MySQLTemporalTable;
import com.trifork.stamdata.persistence.CompleteDataset;


public class MySQLDaoIntegrationTest extends AbstractMySQLIntegationTest
{

	@Before
	public void setupTable() throws SQLException
	{
		/*
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();

		try
		{
			stmt.executeUpdate("drop table SDE");
		}
		catch (Exception e)
		{
		}

		try
		{
			con.createStatement().executeUpdate("create table SDE(id VARCHAR(20) NOT NULL, " + "data VARCHAR(20)," + "date DATETIME," + "				 ModifiedBy VARCHAR(200) NOT NULL," + "				 ModifiedDate DATETIME NOT NULL," + "				 ValidFrom DATETIME ," + "				 ValidTo DATETIME," + "				 CreatedBy VARCHAR(200)," + "				 CreatedDate DATETIME);");
		}
		catch (Exception e)
		{
			// it probably already existed
		}

		stmt.close();
		con.close();
		*/
	}


	@SuppressWarnings("unchecked")
	@Test
	public void testPersistCompleteDataset() throws Exception
	{
		/*
		CompleteDataset<SDE> dataset = new CompleteDataset<SDE>(SDE.class, t0, t1);
		dataset.addRecord(new SDE(t0, FUTURE));
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalDao dao = new MySQLTemporalDao(con);
		dao.persistCompleteDataset(dataset);
		MySQLTemporalTable<SDE> table = dao.getTable(SDE.class);
		assertTrue(table.fetchEntityVersions(t0, t1));
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(table.getCurrentRowValidTo(), FUTURE);
		assertFalse(table.hasMoreRows());
		con.close();
		*/
	}


	@SuppressWarnings("rawtypes")
	@Test
	public void testPersistCompleteDatasetX2() throws Exception
	{
		/*
		CompleteDataset<SDE> dataset = new CompleteDataset<SDE>(SDE.class, t0, t1);
		dataset.addRecord(new SDE(t0, FUTURE));
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalDao dao = new MySQLTemporalDao(con);
		dao.persistCompleteDataset(dataset);
		dao.persistCompleteDataset(dataset);
		MySQLTemporalTable table = dao.getTable(SDE.class);
		assertTrue(table.fetchEntityVersions(t0, t1));
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(table.getCurrentRowValidTo(), FUTURE);
		assertFalse(table.hasMoreRows());
		con.close();
		*/
	}


	@SuppressWarnings("rawtypes")
	@Test
	public void testPersistCompleteDatasetChangedStringSameValidity() throws Exception
	{
		/*
		CompleteDataset<SDE> dataset1 = new CompleteDataset<SDE>(SDE.class, t0, t1);
		CompleteDataset<SDE> dataset2 = new CompleteDataset<SDE>(SDE.class, t0, t1);
		dataset1.addRecord(new SDE(t0, FUTURE, "1", "a"));
		dataset2.addRecord(new SDE(t0, FUTURE, "1", "b"));
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalDao dao = new MySQLTemporalDao(con);
		dao.persistCompleteDataset(dataset1);
		dao.persistCompleteDataset(dataset2);
		MySQLTemporalTable table = dao.getTable(SDE.class);
		assertTrue(table.fetchEntityVersions(t0, t1));
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(FUTURE, table.getCurrentRowValidTo());
		assertEquals("b", table.currentRS.getString("data"));
		assertFalse(table.hasMoreRows());
		con.close();
		*/
	}


	@SuppressWarnings("rawtypes")
	@Test
	public void testPersistCompleteDatasetChangedDateSameValidity() throws Exception
	{
		/*
		CompleteDataset<SDE> dataset1 = new CompleteDataset<SDE>(SDE.class, t0, t1);
		CompleteDataset<SDE> dataset2 = new CompleteDataset<SDE>(SDE.class, t0, t1);
		dataset1.addRecord(new SDE(t0, FUTURE, "1", "a", t3));
		dataset2.addRecord(new SDE(t0, FUTURE, "1", "a", t4));
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalDao dao = new MySQLTemporalDao(con);
		dao.persistCompleteDataset(dataset1);
		dao.persistCompleteDataset(dataset2);
		MySQLTemporalTable table = dao.getTable(SDE.class);
		assertTrue(table.fetchEntityVersions(t0, t1));
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(FUTURE, table.getCurrentRowValidTo());
		assertEquals(t4.getTime(), table.currentRS.getTimestamp("date").getTime());
		assertFalse(table.hasMoreRows());
		con.close();
		*/
	}


	@SuppressWarnings("rawtypes")
	@Test
	public void testPersistCompleteDatasetChangedDataNewValidFrom() throws Exception
	{
		/*
		CompleteDataset<SDE> dataset1 = new CompleteDataset<SDE>(SDE.class, t0, t1);
		CompleteDataset<SDE> dataset2 = new CompleteDataset<SDE>(SDE.class, t1, t2);
		dataset1.addRecord(new SDE(t0, FUTURE, "1", "a"));
		dataset2.addRecord(new SDE(t1, FUTURE, "1", "b"));

		Connection con = MySQLConnectionManager.getAutoCommitConnection();

		MySQLTemporalDao dao = new MySQLTemporalDao(con);
		dao.persistCompleteDataset(dataset1);
		dao.persistCompleteDataset(dataset2);

		MySQLTemporalTable table = dao.getTable(SDE.class);

		assertTrue(table.fetchEntityVersions(t0, t0)); // Get the old version
		assertEquals(t0, table.getCurrentRowValidFrom());
		assertEquals(t1, table.getCurrentRowValidTo());
		assertEquals("a", table.currentRS.getString("data"));
		assertFalse(table.hasMoreRows());

		assertTrue(table.fetchEntityVersions(t2, t2)); // Get the new version
		assertEquals(t1, table.getCurrentRowValidFrom());
		assertEquals(FUTURE, table.getCurrentRowValidTo());
		assertEquals("b", table.currentRS.getString("data"));
		assertFalse(table.hasMoreRows());

		MySQLConnectionManager.close(con);
		*/
	}


	@SuppressWarnings("rawtypes")
	@Test
	public void testPersistCompleteDatasetChangedDataNewValidToNoDataChange() throws Exception
	{
		/*
		CompleteDataset<SDE> dataset1 = new CompleteDataset<SDE>(SDE.class, t0, t1);
		CompleteDataset<SDE> dataset2 = new CompleteDataset<SDE>(SDE.class, t1, t2);
		CompleteDataset<SDE> dataset3 = new CompleteDataset<SDE>(SDE.class, t2, t1000);

		// Normal t0 -> FUTURE
		dataset1.addRecord(new SDE(t0, FUTURE, "1", "a"));

		// Limit validTo to T1 no data change
		dataset2.addRecord(new SDE(t0, t1, "1", "a"));

		// Extend validTo to after FUTURE
		dataset3.addRecord(new SDE(t0, t1000, "1", "a"));

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalDao dao = new MySQLTemporalDao(con);
		dao.persistCompleteDataset(dataset1);

		MySQLTemporalTable table = dao.getTable(SDE.class);
		assertTrue(table.fetchEntityVersions(t0, FUTURE));
		assertEquals(t0, table.getCurrentRowValidFrom());
		assertEquals(FUTURE, table.getCurrentRowValidTo());
		assertEquals("a", table.currentRS.getString("data"));
		assertFalse(table.hasMoreRows());

		// Persist validTo = T1
		dao.persistCompleteDataset(dataset2);
		assertTrue(table.fetchEntityVersions(t0, FUTURE));
		assertEquals(t0, table.getCurrentRowValidFrom());
		assertEquals(t1, table.getCurrentRowValidTo());
		assertEquals("a", table.currentRS.getString("data"));
		assertFalse(table.hasMoreRows());

		// Persist validTo = T1000
		dao.persistCompleteDataset(dataset3);
		assertTrue(table.fetchEntityVersions(t0, FUTURE));
		assertEquals(t0, table.getCurrentRowValidFrom());
		assertEquals(t1000, table.getCurrentRowValidTo());
		assertEquals("a", table.currentRS.getString("data"));
		assertFalse(table.hasMoreRows());
		con.close();
		*/
	}


	@Entity
	private static class SDE extends AbstractRecord
	{
		/*
		private String id = "1";
		private String data = "a";
		Date date;


		public SDE(Date validFrom, Date validTo)
		{

			this(validFrom, validTo, "1", "a");
		}


		public SDE(Date validFrom, Date validTo, String id, String data)
		{

			this(validFrom, validTo, id, data, DateUtils.toDate(2001, 1, 1, 1, 2, 3));
		}


		public SDE(Date validFrom, Date validTo, String id, String data, Date date)
		{

			setValidFrom(validFrom);
			setValidTo(validTo);
			this.data = data;
			this.id = id;
			this.date = date;
		}


		@Override
		public long getPID()
		{

			return 0;
		}


		@SuppressWarnings("unused")
		@Column
		public String getData()
		{

			return data;
		}


		@Id
		@Column(name = "id")
		public Object getKey()
		{

			return id;
		}


		@SuppressWarnings("unused")
		@Column
		public Date getDate()
		{

			return date;
		}
		*/
	}
}
