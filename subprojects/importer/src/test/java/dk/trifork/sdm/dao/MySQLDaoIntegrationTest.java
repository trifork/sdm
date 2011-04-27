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

package dk.trifork.sdm.dao;

import dk.trifork.sdm.config.MySQLConnectionManager;
import dk.trifork.sdm.dao.AuditingPersister;
import dk.trifork.sdm.dao.DatabaseTableWrapper;
import dk.trifork.sdm.model.CompleteDataset;
import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;
import dk.trifork.sdm.model.StamdataEntity;
import dk.trifork.sdm.util.DateUtils;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Statement;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;


public class MySQLDaoIntegrationTest extends AbstractMySQLIntegationTest {

	@Before
	public void setupTable() throws SQLException {

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		try {
			Statement stmt = con.createStatement();
			try {
				stmt.executeUpdate("drop table if exists SDE");
				stmt.executeUpdate("create table SDE(id VARCHAR(20) NOT NULL, data VARCHAR(20), date DATETIME, ModifiedBy VARCHAR(200) NOT NULL, ModifiedDate DATETIME NOT NULL, ValidFrom DATETIME, ValidTo DATETIME, CreatedBy VARCHAR(200), CreatedDate DATETIME);");
			} finally {
				stmt.close();
			}
		} finally {
			con.close();
		}
	}

	@Test
	public void testPersistCompleteDataset() throws Exception {

		CompleteDataset<SDE> dataset = new CompleteDataset<SDE>(SDE.class, t0, t1);
		dataset.addEntity(new SDE(t0, DateUtils.FUTURE));
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		AuditingPersister dao = new AuditingPersister(con);
		dao.persistCompleteDataset(dataset);
		DatabaseTableWrapper<SDE> table = dao.getTable(SDE.class);
		assertTrue(table.fetchEntityVersions(t0, t1));
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(table.getCurrentRowValidTo(), DateUtils.FUTURE);
		assertFalse(table.nextRow());
		con.close();
	}

	@Test
	public void testPersistCompleteDatasetX2() throws Exception {

		CompleteDataset<SDE> dataset = new CompleteDataset<SDE>(SDE.class, t0, t1);
		dataset.addEntity(new SDE(t0, DateUtils.FUTURE));
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		AuditingPersister dao = new AuditingPersister(con);
		dao.persistCompleteDataset(dataset);
		dao.persistCompleteDataset(dataset);
		DatabaseTableWrapper<?> table = dao.getTable(SDE.class);
		assertTrue(table.fetchEntityVersions(t0, t1));
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(table.getCurrentRowValidTo(), DateUtils.FUTURE);
		assertFalse(table.nextRow());
		con.close();
	}

	@Test
	public void testPersistCompleteDatasetChangedStringSameValidity() throws Exception {

		CompleteDataset<SDE> dataset1 = new CompleteDataset<SDE>(SDE.class, t0, t1);
		CompleteDataset<SDE> dataset2 = new CompleteDataset<SDE>(SDE.class, t0, t1);
		dataset1.addEntity(new SDE(t0, DateUtils.FUTURE, "1", "a"));
		dataset2.addEntity(new SDE(t0, DateUtils.FUTURE, "1", "b"));
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		AuditingPersister dao = new AuditingPersister(con);
		dao.persistCompleteDataset(dataset1);
		dao.persistCompleteDataset(dataset2);
		DatabaseTableWrapper<?> table = dao.getTable(SDE.class);
		assertTrue(table.fetchEntityVersions(t0, t1));
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(DateUtils.FUTURE, table.getCurrentRowValidTo());
		assertEquals("b", table.currentRS.getString("data"));
		assertFalse(table.nextRow());
		con.close();
	}

	@Test
	public void testPersistCompleteDatasetChangedDateSameValidity() throws Exception {

		CompleteDataset<SDE> dataset1 = new CompleteDataset<SDE>(SDE.class, t0, t1);
		CompleteDataset<SDE> dataset2 = new CompleteDataset<SDE>(SDE.class, t0, t1);
		dataset1.addEntity(new SDE(t0, DateUtils.FUTURE, "1", "a", t3.getTime()));
		dataset2.addEntity(new SDE(t0, DateUtils.FUTURE, "1", "a", t4.getTime()));
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		AuditingPersister dao = new AuditingPersister(con);
		dao.persistCompleteDataset(dataset1);
		dao.persistCompleteDataset(dataset2);
		DatabaseTableWrapper<?> table = dao.getTable(SDE.class);
		assertTrue(table.fetchEntityVersions(t0, t1));
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(DateUtils.FUTURE, table.getCurrentRowValidTo());
		assertEquals(t4.getTime().getTime(), table.currentRS.getTimestamp("date").getTime());
		assertFalse(table.nextRow());
		con.close();
	}

	@Test
	public void testPersistCompleteDatasetChangedDataNewValidFrom() throws Exception {

		CompleteDataset<SDE> dataset1 = new CompleteDataset<SDE>(SDE.class, t0, t1);
		CompleteDataset<SDE> dataset2 = new CompleteDataset<SDE>(SDE.class, t1, t2);
		dataset1.addEntity(new SDE(t0, DateUtils.FUTURE, "1", "a"));
		dataset2.addEntity(new SDE(t1, DateUtils.FUTURE, "1", "b"));
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
		assertEquals(DateUtils.FUTURE, table.getCurrentRowValidTo());
		assertEquals("b", table.currentRS.getString("data"));
		assertFalse(table.nextRow());
		con.close();
	}

	@Test
	public void testPersistCompleteDatasetChangedDataNewValidToNoDataChange() throws Exception {

		CompleteDataset<SDE> dataset1 = new CompleteDataset<SDE>(SDE.class, t0, t1);
		CompleteDataset<SDE> dataset2 = new CompleteDataset<SDE>(SDE.class, t1, t2);
		CompleteDataset<SDE> dataset3 = new CompleteDataset<SDE>(SDE.class, t2, t1000);

		dataset1.addEntity(new SDE(t0, DateUtils.FUTURE, "1", "a")); // Normal
																		// t0 ->
																		// FUTURE
		dataset2.addEntity(new SDE(t0, t1, "1", "a")); // Limit validTo to T1 no
														// data change
		dataset3.addEntity(new SDE(t0, t1000, "1", "a")); // Extend validTo to
															// after FUTURE

		Connection con = MySQLConnectionManager.getAutoCommitConnection();

		AuditingPersister dao = new AuditingPersister(con);
		dao.persistCompleteDataset(dataset1);
		DatabaseTableWrapper<?> table = dao.getTable(SDE.class);
		assertTrue(table.fetchEntityVersions(t0, DateUtils.FUTURE));
		assertEquals(t0, table.getCurrentRowValidFrom());
		assertEquals(DateUtils.FUTURE, table.getCurrentRowValidTo());
		assertEquals("a", table.currentRS.getString("data"));
		assertFalse(table.nextRow());

		// Persist validTo = T1
		dao.persistCompleteDataset(dataset2);
		assertTrue(table.fetchEntityVersions(t0, DateUtils.FUTURE));
		assertEquals(t0, table.getCurrentRowValidFrom());
		assertEquals(t1, table.getCurrentRowValidTo());
		assertEquals("a", table.currentRS.getString("data"));
		assertFalse(table.nextRow());

		// Persist validTo = T1000
		dao.persistCompleteDataset(dataset3);
		assertTrue(table.fetchEntityVersions(t0, DateUtils.FUTURE));
		assertEquals(t0, table.getCurrentRowValidFrom());
		assertEquals(t1000, table.getCurrentRowValidTo());
		assertEquals("a", table.currentRS.getString("data"));
		assertFalse(table.nextRow());
		con.close();
	}


	@Output
	private static class SDE implements StamdataEntity {

		Calendar validfrom, validto;
		String id = "1"; // default value
		String data = "a"; // default value
		Date date = DateUtils.toCalendar(2001, 1, 1, 1, 2, 3).getTime();

		public SDE(Calendar validFrom, Calendar validTo) {

			this.validfrom = validFrom;
			this.validto = validTo;
		}

		public SDE(Calendar validFrom, Calendar validTo, String id, String data) {

			this.validfrom = validFrom;
			this.validto = validTo;
			this.data = data;
			this.id = id;
		}

		public SDE(Calendar validFrom, Calendar validTo, String id, String data, Date date) {

			this.validfrom = validFrom;
			this.validto = validTo;
			this.data = data;
			this.id = id;
			this.date = date;
		}

		@Override
		public Calendar getValidFrom() {

			return validfrom;
		}

		@Override
		public Calendar getValidTo() {

			return validto;
		}

		@Output
		@SuppressWarnings("unused")
		public String getData() {

			return data;
		}

		@Id
		@Override
		@Output(name = "id")
		public Object getKey() {

			return id;
		}

		@Output
		@SuppressWarnings("unused")
		public Date getDate() {

			return date;
		}
	}
}
