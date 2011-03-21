package dk.trifork.sdm.dao.mysql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.util.Calendar;

import org.junit.Test;

import dk.trifork.sdm.config.MySQLConnectionManager;
import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;
import dk.trifork.sdm.model.StamdataEntity;


public class MySQLTemporalTableIntegrationTest extends AbstractMySQLIntegationTest {

	@Test
	public void testFetchVersionsAbeforeB() throws Exception {

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t1);
		table.insertRow(a, Calendar.getInstance());
		boolean found = table.fetchEntityVersions(a.getKey(), t2, t3);
		assertFalse(found);
		con.close();
	}

	@Test
	public void testFetchVersionsBbeforeA() throws Exception {

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t2, t3);
		table.insertRow(a, Calendar.getInstance());
		boolean found = table.fetchEntityVersions(a.getKey(), t0, t1);
		assertFalse(found);
		con.close();
	}

	@Test
	public void testFetchVersionsBinA() throws Exception {

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t3);
		table.insertRow(a, Calendar.getInstance());
		boolean found = table.fetchEntityVersions(a.getKey(), t1, t2);
		assertTrue(found);
		con.close();
	}

	@Test
	public void testFetchVersionsAinB() throws Exception {

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t1, t2);
		table.insertRow(a, Calendar.getInstance());
		boolean found = table.fetchEntityVersions(a.getKey(), t0, t3);
		assertTrue(found);
		con.close();
	}

	@Test
	public void testFetchVersionsAoverlapsB() throws Exception {

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t2);
		table.insertRow(a, Calendar.getInstance());
		boolean found = table.fetchEntityVersions(a.getKey(), t1, t3);
		assertTrue(found);
		con.close();
	}

	@Test
	public void testFetchVersionsBoverlapsA() throws Exception {

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t1, t3);
		table.insertRow(a, Calendar.getInstance());
		boolean found = table.fetchEntityVersions(a.getKey(), t0, t2);
		assertTrue(found);
		con.close();
	}

	@Test
	public void testGetValidFromAndTo() throws Exception {

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t1);
		table.insertRow(a, Calendar.getInstance());
		boolean found = table.fetchEntityVersions(a.getKey(), t0, t1);
		assertTrue(found);
		assertEquals(table.getCurrentRowValidFrom().getTime(), t0.getTime());
		assertEquals(table.getCurrentRowValidTo(), t1);
		con.close();
	}

	@Test
	public void testCopyCurrentRowButWithChangedValidFrom() throws Exception {

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t2, t3);
		table.insertRow(a, Calendar.getInstance());
		table.fetchEntityVersions(a.getKey(), t2, t3); // Find the row we just
														// created
		table.copyCurrentRowButWithChangedValidFrom(t0, Calendar.getInstance());

		assertTrue(table.fetchEntityVersions(a.getKey(), t0, t1)); // We should
																	// find the
																	// copy
																	// only.
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(table.getCurrentRowValidTo(), t3);
		assertFalse(table.nextRow());
		con.close();
	}

	@Test
	public void testUpdateValidToOnCurrentRow1() throws Exception {

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t1);
		table.insertRow(a, Calendar.getInstance());
		table.fetchEntityVersions(a.getKey(), t0, t1); // Find the row we just
														// created
		table.updateValidToOnCurrentRow(t2, Calendar.getInstance());

		table.fetchEntityVersions(a.getKey(), t0, t1); // We should find it
														// again.
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(table.getCurrentRowValidTo(), t2);
		assertFalse(table.nextRow());
		con.close();
	}

	@Test
	public void testUpdateValidToOnCurrentRow_noSideEffects() throws Exception {

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t1);
		SDE b = new SDE(t2, t3);
		table.insertRow(a, Calendar.getInstance());
		table.insertRow(b, Calendar.getInstance());
		table.fetchEntityVersions(a.getKey(), t2, t3); // Find only the 'b' row
														// we just created
		table.updateValidToOnCurrentRow(t4, Calendar.getInstance());

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
	public void testDeleteCurrentRow() throws Exception {

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t1);
		table.insertRow(a, Calendar.getInstance());
		table.fetchEntityVersions(a.getKey(), t0, t1);
		table.deleteCurrentRow();
		assertFalse(table.fetchEntityVersions(a.getKey(), t0, t1));
		con.close();
	}

	@Test
	public void testUpdateValidFromOnCurrentRow() throws Exception {

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t1, t2);
		table.insertRow(a, Calendar.getInstance());
		table.fetchEntityVersions(a.getKey(), t1, t2); // Find the row we just
														// created
		table.updateValidFromOnCurrentRow(t0, Calendar.getInstance());

		table.fetchEntityVersions(a.getKey(), t1, t2); // We should find it
														// again.
		assertEquals(table.getCurrentRowValidFrom(), t0);
		assertEquals(table.getCurrentRowValidTo(), t2);
		assertFalse(table.nextRow());
		con.close();
	}

	@Test
	public void testdataInCurrentRowEquals() throws Exception {

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t1);
		SDE b = new SDE(t2, t3);
		table.insertRow(a, Calendar.getInstance());
		table.fetchEntityVersions(a.getKey(), t0, t1); // Find the row we just
														// created
		assertTrue(table.dataInCurrentRowEquals(b));
		con.close();
	}

	@Test
	public void testdataInCurrentRowEquals2() throws Exception {

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t1);
		SDE b = new SDE(t2, t3) {

			@Id
			@Output
			public String getTakstuge() {

				return "1997-11";
			}
		};
		table.insertRow(a, Calendar.getInstance());
		table.fetchEntityVersions(a.getKey(), t0, t1); // Find the row we just
														// created
		assertFalse(table.dataInCurrentRowEquals(b));
		con.close();
	}

	@Test
	public void testFetchEntityVersions() throws Exception {

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalTable<SDE> table = new MySQLTemporalTable<SDE>(con, SDE.class);
		SDE a = new SDE(t0, t1);
		SDE b = new SDE(t2, t3);
		table.insertRow(a, Calendar.getInstance());
		table.insertRow(b, Calendar.getInstance());
		table.fetchEntityVersions(t0, t3);
		assertTrue(table.nextRow());
		assertFalse(table.nextRow());
		con.close();
	}


	@Output(name = "TakstVersion")
	public static class SDE implements StamdataEntity {

		Calendar validfrom, validto;

		public SDE(Calendar validFrom, Calendar validTo) {

			this.validfrom = validFrom;
			this.validto = validTo;
		}

		@Override
		public Object getKey() {

			return getTakstuge();
		}

		@Override
		public Calendar getValidFrom() {

			return validfrom;
		}

		@Override
		public Calendar getValidTo() {

			return validto;
		}

		@Id
		@Output
		public String getTakstuge() {

			return "1999-11";
		}
	}
}
