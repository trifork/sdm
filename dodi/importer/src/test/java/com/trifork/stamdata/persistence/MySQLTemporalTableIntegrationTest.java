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

package com.trifork.stamdata.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.util.Date;

import org.junit.Test;

import com.trifork.stamdata.Helpers;
import com.trifork.stamdata.importer.persistence.*;


public class MySQLTemporalTableIntegrationTest extends AbstractMySQLIntegationTest
{

	@Test
	public void testFetchVersionsAbeforeB() throws Exception
	{

		Connection con = Helpers.getConnection();
		DatabaseTableWrapper<SDE> table = new DatabaseTableWrapper<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t1);
		table.insertRow(a, new Date());
		boolean found = table.fetchEntityVersions(a.getKey(), t2, t3);
		assertFalse(found);
		con.close();
	}

	@Test
	public void testFetchVersionsBbeforeA() throws Exception
	{
		Connection con = Helpers.getConnection();
		DatabaseTableWrapper<SDE> table = new DatabaseTableWrapper<SDE>(con, SDE.class);
		SDE a = new SDE(t2, t3);
		table.insertRow(a, new Date());
		boolean found = table.fetchEntityVersions(a.getKey(), t0, t1);
		assertFalse(found);
		con.close();
	}

	@Test
	public void testFetchVersionsBinA() throws Exception
	{
		Connection con = Helpers.getConnection();
		DatabaseTableWrapper<SDE> table = new DatabaseTableWrapper<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t3);
		table.insertRow(a, new Date());
		boolean found = table.fetchEntityVersions(a.getKey(), t1, t2);
		assertTrue(found);
		con.close();
	}

	@Test
	public void testFetchVersionsAinB() throws Exception
	{
		Connection con = Helpers.getConnection();
		DatabaseTableWrapper<SDE> table = new DatabaseTableWrapper<SDE>(con, SDE.class);
		SDE a = new SDE(t1, t2);
		table.insertRow(a, new Date());
		boolean found = table.fetchEntityVersions(a.getKey(), t0, t3);
		assertTrue(found);
		con.close();
	}

	@Test
	public void testFetchVersionsAoverlapsB() throws Exception
	{

		Connection con = Helpers.getConnection();
		DatabaseTableWrapper<SDE> table = new DatabaseTableWrapper<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t2);
		table.insertRow(a, new Date());
		boolean found = table.fetchEntityVersions(a.getKey(), t1, t3);
		assertTrue(found);
		con.close();
	}

	@Test
	public void testFetchVersionsBoverlapsA() throws Exception
	{
		Connection con = Helpers.getConnection();
		DatabaseTableWrapper<SDE> table = new DatabaseTableWrapper<SDE>(con, SDE.class);
		SDE a = new SDE(t1, t3);
		table.insertRow(a, new Date());
		boolean found = table.fetchEntityVersions(a.getKey(), t0, t2);
		assertTrue(found);
		con.close();
	}

	@Test
	public void testGetValidFromAndTo() throws Exception
	{
		Connection con = Helpers.getConnection();
		DatabaseTableWrapper<SDE> table = new DatabaseTableWrapper<SDE>(con, SDE.class);

		SDE a = new SDE(t0, t1);
		table.insertRow(a, new Date());

		assertTrue(table.fetchEntityVersions(a.getKey(), t0, t1));
		assertEquals(table.getCurrentRowValidFrom().getTime(), t0.getTime());
		assertEquals(table.getCurrentRowValidTo().getTime(), t1.getTime());

		con.close();
	}

	@Test
	public void testCopyCurrentRowButWithChangedValidFrom() throws Exception
	{
		Connection con = Helpers.getConnection();
		DatabaseTableWrapper<SDE> table = new DatabaseTableWrapper<SDE>(con, SDE.class);
		SDE a = new SDE(t2, t3);
		table.insertRow(a, new Date());

		// Find the row we just created.

		table.fetchEntityVersions(a.getKey(), t2, t3);
		table.copyCurrentRowButWithChangedValidFrom(t0, new Date());

		// We should find the copy only.

		assertTrue(table.fetchEntityVersions(a.getKey(), t0, t1));
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(table.getCurrentRowValidTo(), t3);
		assertFalse(table.nextRow());
		con.close();
	}

	@Test
	public void testUpdateValidToOnCurrentRow1() throws Exception
	{
		Connection con = Helpers.getConnection();
		DatabaseTableWrapper<SDE> table = new DatabaseTableWrapper<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t1);
		table.insertRow(a, new Date());
		table.fetchEntityVersions(a.getKey(), t0, t1); // Find the row we just
														// created
		table.updateValidToOnCurrentRow(t2, new Date());

		table.fetchEntityVersions(a.getKey(), t0, t1); // We should find it
														// again.
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(table.getCurrentRowValidTo(), t2);
		assertFalse(table.nextRow());
		con.close();
	}

	@Test
	public void testUpdateValidToOnCurrentRow_noSideEffects() throws Exception
	{

		Connection con = Helpers.getConnection();
		DatabaseTableWrapper<SDE> table = new DatabaseTableWrapper<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t1);
		SDE b = new SDE(t2, t3);
		table.insertRow(a, new Date());
		table.insertRow(b, new Date());
		table.fetchEntityVersions(a.getKey(), t2, t3); // Find only the 'b' row
														// we just created
		table.updateValidToOnCurrentRow(t4, new Date());

		table.fetchEntityVersions(a.getKey(), t0, t1); // Find the 'a' row
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(table.getCurrentRowValidTo(), t1);
		assertFalse(table.nextRow());
		table.fetchEntityVersions(a.getKey(), t2, t3); // Find the 'b' row
		assertEquals(table.getCurrentRowValidFrom(), t2);
		assertEquals(table.getCurrentRowValidTo(), t4);
		assertFalse(table.nextRow());
		con.close();
	}

	@Test
	public void testDeleteCurrentRow() throws Exception
	{
		Connection con = Helpers.getConnection();
		DatabaseTableWrapper<SDE> table = new DatabaseTableWrapper<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t1);
		table.insertRow(a, new Date());
		table.fetchEntityVersions(a.getKey(), t0, t1);
		table.deleteCurrentRow();
		assertFalse(table.fetchEntityVersions(a.getKey(), t0, t1));
		con.close();
	}

	@Test
	public void testUpdateValidFromOnCurrentRow() throws Exception
	{
		Connection con = Helpers.getConnection();
		DatabaseTableWrapper<SDE> table = new DatabaseTableWrapper<SDE>(con, SDE.class);
		SDE a = new SDE(t1, t2);
		table.insertRow(a, new Date());
		table.fetchEntityVersions(a.getKey(), t1, t2); // Find the row we just
														// created
		table.updateValidFromOnCurrentRow(t0, new Date());

		table.fetchEntityVersions(a.getKey(), t1, t2); // We should find it
														// again.
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(table.getCurrentRowValidTo(), t2);
		assertFalse(table.nextRow());
		con.close();
	}

	@Test
	public void testdataInCurrentRowEquals() throws Exception
	{
		Connection con = Helpers.getConnection();
		DatabaseTableWrapper<SDE> table = new DatabaseTableWrapper<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t1);
		SDE b = new SDE(t2, t3);
		table.insertRow(a, new Date());
		table.fetchEntityVersions(a.getKey(), t0, t1); // Find the row we just
														// created
		assertTrue(table.dataInCurrentRowEquals(b));
		con.close();
	}

	@Test
	public void testdataInCurrentRowEquals2() throws Exception
	{
		Connection con = Helpers.getConnection();
		DatabaseTableWrapper<SDE> table = new DatabaseTableWrapper<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t1);
		SDE b = new SDE(t2, t3)
		{

			@Id
			@Output
			public String getTakstuge()
			{
				return "1997-11";
			}
		};

		// Find the row we just created.

		table.insertRow(a, new Date());
		table.fetchEntityVersions(a.getKey(), t0, t1);
		assertFalse(table.dataInCurrentRowEquals(b));
		con.close();
	}

	@Test
	public void testFetchEntityVersions() throws Exception
	{
		Connection con = Helpers.getConnection();
		DatabaseTableWrapper<SDE> table = new DatabaseTableWrapper<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t1);
		SDE b = new SDE(t2, t3);
		table.insertRow(a, new Date());
		table.insertRow(b, new Date());
		table.fetchEntityVersions(t0, t3);
		assertTrue(table.nextRow());
		assertFalse(table.nextRow());
		con.close();
	}

	@Output(name = "TakstVersion")
	public static class SDE implements StamdataEntity
	{
		Date validfrom, validto;

		public SDE(Date validFrom, Date validTo)
		{
			this.validfrom = validFrom;
			this.validto = validTo;
		}

		@Override
		public Object getKey()
		{
			return getTakstuge();
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

		@Id
		@Output
		public String getTakstuge()
		{
			return "1999-11";
		}
	}
}
