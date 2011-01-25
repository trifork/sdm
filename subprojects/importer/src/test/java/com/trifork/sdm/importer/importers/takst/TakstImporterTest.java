package com.trifork.sdm.importer.importers.takst;


import static org.junit.Assert.*;

import java.sql.*;
import java.util.*;
import java.util.Date;

import org.junit.*;

import com.trifork.stamdata.DateUtils;
import com.trifork.stamdata.importer.jobs.takst.TakstImporter;
import com.trifork.stamdata.importer.persistence.MySQLTemporalDao;
import com.trifork.stamdata.registre.takst.*;


public class TakstImporterTest
{

	@Before
	@After
	public void cleanup() throws Exception
	{
		/*
		 * Connection con = MySQLConnectionManager.getAutoCommitConnection(); Statement stmt =
		 * con.createStatement(); stmt.executeQuery("truncate table LaegemiddelDoseringRef");
		 * stmt.executeQuery("truncate table TakstVersion"); stmt.close(); con.close();
		 */
	}


	@Test
	public void testAreRequiredInputFilesPresent() throws Exception
	{
		/*
		TakstImporter ti = new TakstImporter();
		String dir = TakstParserTest.TAKST_TESTDATA_DIR + "/initial/";

		List<File> files = Arrays.asList(TestHelper.getFile(dir).listFiles());

		assertTrue(ti.areRequiredInputFilesPresent(files));
		*/
	}


	@Test
	public void testAreRequiredInputFilesPresent2() throws Exception
	{
		/*
		TakstImporter ti = new TakstImporter();
		String dir = TakstParserTest.TAKST_TESTDATA_DIR + "/incomplete/";

		List<File> files = Arrays.asList(TestHelper.getFile(dir).listFiles());

		assertFalse(ti.areRequiredInputFilesPresent(files));
		*/
	}


	@Test
	public void testGetNextImportExpectedBefore() throws SQLException
	{
		/*
		assertTrue(new TakstImporter().getNextImportExpectedBefore(null).getTime() < new Date().getTime());

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();
		stmt.execute("INSERT INTO TakstVersion (TakstUge, ModifiedBy, ModifiedDate, CreatedBy, CreatedDate) " + "VALUES (\"201001\", \"UnitTest\", \"2010-01-01 00:00:00\", \"UnitTest\", \"2010-01-01 00:00:00\") ");
		stmt.close();
		con.close();

		// We expect that the next takst after the first week in 2010 will be 3
		// week in 2010, or latest sat. January 16th at noon
		assertEquals(DateUtils.toDate(2010, 1, 16, 12, 0, 0).getTime(), new TakstImporter().getNextImportExpectedBefore(DateUtils.toDate(2008, 12, 12, 15, 10, 0)).getTime());
		*/
	}


	@Test
	public void testLaegemiddelDoseringRef() throws Exception
	{
		/*
		Date from = DateUtils.toDate(2008, 01, 01);
		Date to = DateUtils.toDate(2009, 01, 01);

		Takst takst = new Takst(from, to);

		Doseringskode d = new Doseringskode();
		d.setDoseringskode(1l);
		d.setDrugid(2l);
		List<Doseringskode> dk = new ArrayList<Doseringskode>();
		dk.add(d);

		TakstDataset<Doseringskode> dataset = new TakstDataset<Doseringskode>(takst, dk, Doseringskode.class);
		takst.addDataset(dataset);
		assertEquals(1, takst.getEntities().size());
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		MySQLTemporalDao dao = new MySQLTemporalDao(con);
		dao.persistCompleteDataset(dataset);

		ResultSet rs = con.createStatement().executeQuery("SELECT count(*) FROM LaegemiddelDoseringRef");
		rs.next();
		assertEquals(1, rs.getInt(1));
		dao.persistCompleteDataset(dataset);
		rs = con.createStatement().executeQuery("SELECT count(*) FROM LaegemiddelDoseringRef");

		rs.next();

		assertEquals(1, rs.getInt(1));
		*/
	}

}
