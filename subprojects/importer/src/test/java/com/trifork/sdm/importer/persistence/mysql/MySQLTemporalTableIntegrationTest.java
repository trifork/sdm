package com.trifork.sdm.importer.persistence.mysql;


import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.Date;
import java.util.Map;

import javax.persistence.*;

import org.junit.Test;

import com.trifork.stamdata.AbstractRecord;
import com.trifork.stamdata.importer.persistence.MySQLTemporalTable;


public class MySQLTemporalTableIntegrationTest extends AbstractMySQLIntegationTest
{

	@Test
	public void testFetchVersionsAbeforeB() throws Exception
	{
		/*
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t1);
		table.insertRow(a, new Date());
		boolean found = table.fetchEntityVersions(a.getKey(), t2, t3);
		assertFalse(found);
		con.close();
		*/
	}


	@Test
	public void testFetchVersionsBbeforeA() throws Exception
	{
		/*
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t2, t3);
		table.insertRow(a, new Date());
		boolean found = table.fetchEntityVersions(a.getKey(), t0, t1);
		assertFalse(found);
		con.close();
		*/
	}


	@Test
	public void testFetchVersionsBinA() throws Exception
	{
		/*
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t3);
		table.insertRow(a, new Date());
		boolean found = table.fetchEntityVersions(a.getKey(), t1, t2);
		assertTrue(found);
		con.close();
		*/
	}


	@Test
	public void testFetchVersionsAinB() throws Exception
	{
		/*
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t1, t2);
		table.insertRow(a, new Date());
		boolean found = table.fetchEntityVersions(a.getKey(), t0, t3);
		assertTrue(found);
		con.close();
		*/
	}


	@Test
	public void testFetchVersionsAoverlapsB() throws Exception
	{
		/*
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t2);
		table.insertRow(a, new Date());
		boolean found = table.fetchEntityVersions(a.getKey(), t1, t3);
		assertTrue(found);
		con.close();
		*/
	}


	@Test
	public void testFetchVersionsBoverlapsA() throws Exception
	{
		/*
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t1, t3);
		table.insertRow(a, new Date());
		boolean found = table.fetchEntityVersions(a.getKey(), t0, t2);
		assertTrue(found);
		con.close();
		*/
	}


	@Test
	public void testGetValidFromAndTo() throws Exception
	{
		/*
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t1);
		table.insertRow(a, new Date());
		boolean found = table.fetchEntityVersions(a.getKey(), t0, t1);
		assertTrue(found);
		assertEquals(table.getCurrentRowValidFrom().getTime(), t0.getTime());
		assertEquals(table.getCurrentRowValidTo(), t1);
		con.close();
		*/
	}


	@Test
	public void testCopyCurrentRowButWithChangedValidFrom() throws Exception
	{
		/*
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);

		SDE a = new SDE(t2, t3);
		table.insertRow(a, new Date());

		// Find the row we just created.

		table.fetchEntityVersions(a.getKey(), t2, t3);
		table.copyCurrentRowButWithChangedValidFrom(t0, new Date());

		// We should find the copy only.

		assertTrue(table.fetchEntityVersions(a.getKey(), t0, t1));
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(table.getCurrentRowValidTo(), t3);
		assertFalse(table.hasMoreRows());
		con.close();
		*/
	}


	@Test
	public void testUpdateValidToOnCurrentRow1() throws Exception
	{
		/*
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t1);
		table.insertRow(a, new Date());
		table.fetchEntityVersions(a.getKey(), t0, t1); // Find the row we
														// just created
		table.updateValidToOnCurrentRow(t2, new Date());

		table.fetchEntityVersions(a.getKey(), t0, t1); // We should find it
														// again.
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(table.getCurrentRowValidTo(), t2);
		assertFalse(table.hasMoreRows());
		con.close();
		*/
	}


	@Test
	public void testUpdateValidToOnCurrentRow_noSideEffects() throws Exception
	{
		/*
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t1);
		SDE b = new SDE(t2, t3);
		table.insertRow(a, new Date());
		table.insertRow(b, new Date());
		table.fetchEntityVersions(a.getKey(), t2, t3); // Find only the 'b'
														// row we just
														// created
		table.updateValidToOnCurrentRow(t4, new Date());

		table.fetchEntityVersions(a.getKey(), t0, t1); // Find the 'a' row
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(table.getCurrentRowValidTo(), t1);
		assertFalse(table.hasMoreRows());
		table.fetchEntityVersions(a.getKey(), t2, t3); // Find the 'b' row
		assertEquals(table.getCurrentRowValidFrom(), t2);
		assertEquals(table.getCurrentRowValidTo(), t4);
		assertFalse(table.hasMoreRows());
		con.close();
		*/
	}


	@Test
	public void testDeleteCurrentRow() throws Exception
	{
		/*
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t1);
		table.insertRow(a, new Date());
		table.fetchEntityVersions(a.getKey(), t0, t1);
		table.deleteCurrentRow();
		assertFalse(table.fetchEntityVersions(a.getKey(), t0, t1));
		con.close();
		*/
	}


	@Test
	public void testUpdateValidFromOnCurrentRow() throws Exception
	{
		/*
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t1, t2);
		table.insertRow(a, new Date());
		table.fetchEntityVersions(a.getKey(), t1, t2); // Find the row we
														// just created
		table.updateValidFromOnCurrentRow(t0, new Date());

		table.fetchEntityVersions(a.getKey(), t1, t2); // We should find it
														// again.
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(table.getCurrentRowValidTo(), t2);
		assertFalse(table.hasMoreRows());
		con.close();
		*/
	}


	@Test
	public void testdataInCurrentRowEquals() throws Exception
	{
		/*
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t1);
		SDE b = new SDE(t2, t3);
		table.insertRow(a, new Date());
		table.fetchEntityVersions(a.getKey(), t0, t1); // Find the row we
														// just created
		assertTrue(table.dataInCurrentRowEquals(b));
		con.close();
		*/
	}


	@Test
	public void testdataInCurrentRowEquals2() throws Exception
	{
		/*
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t1);
		SDE b = new SDE(t2, t3)
		{

			@Id
			@Column
			public String getTakstuge()
			{

				return "1997-11";
			}
		};
		table.insertRow(a, new Date());
		table.fetchEntityVersions(a.getKey(), t0, t1); // Find the row we
														// just created
		assertFalse(table.dataInCurrentRowEquals(b));
		con.close();
		*/
	}


	@Test
	public void testFetchEntityVersions() throws Exception
	{
		/*
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t1);
		SDE b = new SDE(t2, t3);
		table.insertRow(a, new Date());
		table.insertRow(b, new Date());
		table.fetchEntityVersions(t0, t3);
		assertTrue(table.hasMoreRows());
		assertFalse(table.hasMoreRows());
		con.close();
		*/
	}


	@Entity
	@Table(name = "TakstVersion")
	public static class SDE extends AbstractRecord
	{
		/*
		Date validfrom, validto;


		public SDE(Date validFrom, Date validTo)
		{

			this.validfrom = validFrom;
			this.validto = validTo;
		}


		public Object getKey()
		{

			return getTakstuge();
		}


		public Date getValidFrom()
		{

			return validfrom;
		}


		public Date getValidTo()
		{

			return validto;
		}


		public Map<String, Object> serialize()
		{

			return null;
		}


		@Id
		@Column
		public String getTakstuge()
		{

			return "1999-11";
		}
		*/
	}
}
