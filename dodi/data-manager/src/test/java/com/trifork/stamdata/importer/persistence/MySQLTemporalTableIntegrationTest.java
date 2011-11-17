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


package com.trifork.stamdata.importer.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.Entities;
import com.trifork.stamdata.importer.config.ConnectionManager;
import com.trifork.stamdata.models.TemporalEntity;

public class MySQLTemporalTableIntegrationTest extends AbstractMySQLIntegrationTest
{
    private Connection con;
    private DatabaseTableWrapper<SDE> table;

    @Before
    public void setUp() throws Exception
    {
        con = new ConnectionManager().getAutoCommitConnection();
        table = new DatabaseTableWrapper<SDE>(con, SDE.class);
    }

    @After
    public void tearDown()
    {
        ConnectionManager.closeQuietly(con);
    }

    @Test
	public void testFetchVersionsAbeforeB() throws Exception
	{
        SDE a = new SDE(t0, t1);
		table.insertEntity(a, new Date());
		Object key = Entities.getEntityID(a);
		boolean found = table.fetchEntityConflicts(key, t2, t3);
		assertFalse(found);
	}

	@Test
	public void testFetchVersionsBbeforeA() throws Exception
	{
		SDE a = new SDE(t2, t3);
		table.insertEntity(a, new Date());
		Object key = Entities.getEntityID(a);
		boolean found = table.fetchEntityConflicts(key, t0, t1);
		assertFalse(found);
	}

	@Test
	public void testFetchVersionsBinA() throws Exception
	{
		SDE a = new SDE(t0, t3);
		table.insertEntity(a, new Date());
		Object key = Entities.getEntityID(a);
		boolean found = table.fetchEntityConflicts(key, t1, t2);
		assertTrue(found);
	}

	@Test
	public void testFetchVersionsAinB() throws Exception
	{
		SDE a = new SDE(t1, t2);
		table.insertEntity(a, new Date());
		Object key = Entities.getEntityID(a);
		boolean found = table.fetchEntityConflicts(key, t0, t3);
		assertTrue(found);
	}

	@Test
	public void testFetchVersionsAoverlapsB() throws Exception
	{
		SDE a = new SDE(t0, t2);
		table.insertEntity(a, new Date());
		Object key = Entities.getEntityID(a);
		boolean found = table.fetchEntityConflicts(key, t1, t3);
		assertTrue(found);
	}

	@Test
	public void testFetchVersionsBoverlapsA() throws Exception
	{
		SDE a = new SDE(t1, t3);
		table.insertEntity(a, new Date());
		Object key = Entities.getEntityID(a);
		boolean found = table.fetchEntityConflicts(key, t0, t2);
		assertTrue(found);
	}

	@Test
	public void testGetValidFromAndTo() throws Exception
	{
		SDE a = new SDE(t0, t1);
		table.insertEntity(a, new Date());

		Object key = Entities.getEntityID(a);
		assertTrue(table.fetchEntityConflicts(key, t0, t1));
		assertEquals(table.getCurrentRowValidFrom().getTime(), t0.getTime());
		assertEquals(table.getCurrentRowValidTo().getTime(), t1.getTime());
	}

	@Test
	public void testCopyCurrentRowButWithChangedValidFrom() throws Exception
	{
		SDE a = new SDE(t2, t3);
		table.insertEntity(a, new Date());

		// Find the row we just created.
		
		Object key = Entities.getEntityID(a);

		table.fetchEntityConflicts(key, t2, t3);
		table.copyCurrentRowButWithChangedValidFrom(t0, new Date());

		// We should find the copy only.

		assertTrue(table.fetchEntityConflicts(key, t0, t1));
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(table.getCurrentRowValidTo(), t3);
		assertFalse(table.moveToNextRow());
	}

	@Test
	public void testUpdateValidToOnCurrentRow1() throws Exception
	{
		SDE a = new SDE(t0, t1);
		table.insertEntity(a, new Date());
		Object key = Entities.getEntityID(a);

        // Find the row we just created.
		table.fetchEntityConflicts(key, t0, t1);
		table.updateValidToOnCurrentRow(t2, new Date());

        // We should find it again.
		table.fetchEntityConflicts(key, t0, t1);
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(table.getCurrentRowValidTo(), t2);
		assertFalse(table.moveToNextRow());
	}

	@Test
	public void testUpdateValidToOnCurrentRow_noSideEffects() throws Exception
	{
		SDE a = new SDE(t0, t1);
		Object key = Entities.getEntityID(a);
		SDE b = new SDE(t2, t3);
		table.insertEntity(a, new Date());
		table.insertEntity(b, new Date());

        // Find only the 'b' row we just created.
		table.fetchEntityConflicts(key, t2, t3);
		table.updateValidToOnCurrentRow(t4, new Date());

        // Find the 'a' row
		table.fetchEntityConflicts(key, t0, t1);
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(table.getCurrentRowValidTo(), t1);
		assertFalse(table.moveToNextRow());

        // Find the 'b' row
		table.fetchEntityConflicts(key, t2, t3);
		assertEquals(table.getCurrentRowValidFrom(), t2);
		assertEquals(table.getCurrentRowValidTo(), t4);
		assertFalse(table.moveToNextRow());
	}

	@Test
	public void testUpdateValidFromOnCurrentRow() throws Exception
	{
		SDE a = new SDE(t1, t2);
		table.insertEntity(a, new Date());

		Object key = Entities.getEntityID(a);

        // Find the row we just created.
		table.fetchEntityConflicts(key, t1, t2);
		table.updateValidFromOnCurrentRow(t0, new Date());

        // We should find it again.
		table.fetchEntityConflicts(key, t1, t2);
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(table.getCurrentRowValidTo(), t2);
		assertFalse(table.moveToNextRow());
		con.close();
	}

	@Test
	public void testdataInCurrentRowEquals() throws Exception
	{
		SDE a = new SDE(t0, t1);
		SDE b = new SDE(t2, t3);

		table.insertEntity(a, new Date());
		Object key = Entities.getEntityID(a);
        // Find the row we just created.
		table.fetchEntityConflicts(key, t0, t1);
		assertTrue(table.currentRowEquals(b));
	}

	@Test
	public void testdataInCurrentRowEquals2() throws Exception
	{
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

		table.insertEntity(a, new Date());
		
		Object key = Entities.getEntityID(a);

        // Find the row we just created.
		table.fetchEntityConflicts(key, t0, t1);
		assertFalse(table.currentRowEquals(b));
	}

	@Test
	public void testFetchEntityVersions() throws Exception
	{
		SDE a = new SDE(t0, t1);
		SDE b = new SDE(t2, t3);

		table.insertEntity(a, new Date());
		table.insertEntity(b, new Date());
		table.fetchEntitiesInRange(t0, t3);

		assertTrue(table.moveToNextRow());
		assertFalse(table.moveToNextRow());
	}

	@Entity(name = "TakstVersion")
	public static class SDE implements TemporalEntity
	{
		Date validfrom, validto;

		public SDE(Date validFrom, Date validTo)
		{
			this.validfrom = validFrom;
			this.validto = validTo;
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
		@Column
		public String getTakstuge()
		{
			return "1999-11";
		}
	}
}
