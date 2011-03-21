package dk.trifork.sdm.dao.mysql;

import dk.trifork.sdm.config.MySQLConnectionManager;
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
import java.util.Map;

import static org.junit.Assert.*;


public class MySQLDaoIntegrationTest extends AbstractMySQLIntegationTest {

	@Before
	public void setupTable() throws SQLException {

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();

		try {
			stmt.executeUpdate("drop table SDE");
		}
		catch (Exception e) {
		}

		try {
			con.createStatement().executeUpdate("create table SDE(id VARCHAR(20) NOT NULL, data VARCHAR(20), date DATETIME, ModifiedBy VARCHAR(200) NOT NULL, ModifiedDate DATETIME NOT NULL, ValidFrom DATETIME, ValidTo DATETIME, CreatedBy VARCHAR(200), CreatedDate DATETIME);");
		}
		catch (Exception e) {
			// it probably already existed
			// FIXME: This is ugly. A database should be created on the fly.
		}

		stmt.close();
		con.close();
	}

	@Test
	public void testPersistCompleteDataset() throws Exception {

		CompleteDataset<SDE> dataset = new CompleteDataset<SDE>(SDE.class, t0, t1);
		dataset.addEntity(new SDE(t0, DateUtils.FUTURE));
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalDao dao = new MySQLTemporalDao(con);
		dao.persistCompleteDataset(dataset);
		MySQLTemporalTable<SDE> table = dao.getTable(SDE.class);
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
		MySQLTemporalDao dao = new MySQLTemporalDao(con);
		dao.persistCompleteDataset(dataset);
		dao.persistCompleteDataset(dataset);
		MySQLTemporalTable table = dao.getTable(SDE.class);
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
		MySQLTemporalDao dao = new MySQLTemporalDao(con);
		dao.persistCompleteDataset(dataset1);
		dao.persistCompleteDataset(dataset2);
		MySQLTemporalTable table = dao.getTable(SDE.class);
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
		MySQLTemporalDao dao = new MySQLTemporalDao(con);
		dao.persistCompleteDataset(dataset1);
		dao.persistCompleteDataset(dataset2);
		MySQLTemporalTable table = dao.getTable(SDE.class);
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
		MySQLTemporalDao dao = new MySQLTemporalDao(con);
		dao.persistCompleteDataset(dataset1);
		dao.persistCompleteDataset(dataset2);
		MySQLTemporalTable table = dao.getTable(SDE.class);
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

		MySQLTemporalDao dao = new MySQLTemporalDao(con);
		dao.persistCompleteDataset(dataset1);
		MySQLTemporalTable table = dao.getTable(SDE.class);
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
		public Date getDate() {

			return date;
		}
	}
}
