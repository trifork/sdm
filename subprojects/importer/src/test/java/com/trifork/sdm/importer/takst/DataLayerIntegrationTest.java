package com.trifork.sdm.importer.takst;


import java.sql.*;
import java.text.SimpleDateFormat;

import org.junit.*;

import com.trifork.stamdata.importer.jobs.FileImporterException;
import com.trifork.stamdata.importer.persistence.MySQLTemporalDao;
import com.trifork.stamdata.importer.persistence.RecordPersister;
import com.trifork.stamdata.registre.takst.Takst;


/**
 * Integration test of the database access layer. Tests that a dataset can be written to the
 * database.
 */
public class DataLayerIntegrationTest
{

	private static final String TAKST_TESTDATA_DIR = "testdata/takst";
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


	@Before
	@After
	public void cleanDatabase() throws Exception
	{
		/*
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement statement = con.createStatement();
		statement.execute("truncate table TakstVersion");
		statement.execute("truncate table Laegemiddel");
		statement.execute("truncate table Pakning");
		statement.execute("truncate table Administrationsvej");
		statement.execute("truncate table ATC");
		statement.execute("truncate table IndikationATCRef");
		statement.execute("truncate table Indikation");
		statement.execute("truncate table LaegemiddelDoseringRef");
		statement.execute("truncate table Klausulering");
		statement.execute("truncate table Medicintilskud");
		statement.execute("truncate table Dosering");
		statement.execute("truncate table Formbetegnelse");
		statement.execute("truncate table Tidsenhed");
		statement.execute("truncate table Pakningsstoerrelsesenhed");
		statement.execute("truncate table Styrkeenhed");
		statement.execute("truncate table LaegemiddelAdministrationsvejRef");
		statement.close();
		con.close();
		*/
	}


	@Test
	public void ImportTest() throws Exception
	{
		/*
		// Arrange
		Takst takst = parseTakst(TAKST_TESTDATA_DIR + "/initial");

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement statement = con.createStatement();
		RecordDao versionedDao = new MySQLTemporalDao(con);

		// Act
		versionedDao.persistCompleteDatasets(takst.getDatasets());

		// Assert
		Assert.assertEquals(new Integer(92), getRecordCount(versionedDao));

		ResultSet rs = statement.executeQuery("select * from Laegemiddel where DrugName like 'Kemadrin';");
		if (!rs.next())
		{
			Assert.fail("Did not find expected Laegemiddel Kemadrin");
		}
		Assert.assertEquals(dateFormat.parse("2999-12-31 00:00:00"), rs.getTimestamp("ValidTo"));
		statement.close();
		con.close();
		*/
	}


	@Test
	public void UpdateTest() throws Exception
	{
		/*
		// Arrange
		Takst takstinit = parseTakst(TAKST_TESTDATA_DIR + "/initial");
		Takst takstupd = parseTakst(TAKST_TESTDATA_DIR + "/update");

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement statement = con.createStatement();
		RecordDao versionedDao = new MySQLTemporalDao(con);

		// Act
		versionedDao.persistCompleteDatasets(takstinit.getDatasets());
		versionedDao.persistCompleteDatasets(takstupd.getDatasets());

		// Assert
		Assert.assertEquals(new Integer(93), getRecordCount(versionedDao));

		ResultSet rs = statement.executeQuery("select * from Laegemiddel where DrugName like 'Kemadrin';");
		if (!rs.next())
		{
			Assert.fail("Did not find expected Laegemiddel Kemadrin");
		}
		Assert.assertEquals(dateFormat.parse("2009-07-30 00:00:00"), rs.getTimestamp("ValidTo"));

		rs = statement.executeQuery("select * from Laegemiddel where DrugName like 'Kemadron';");
		if (!rs.next())
		{
			Assert.fail("Did not find expected Laegemiddel Kemadron");
		}
		Assert.assertEquals(dateFormat.parse("2999-12-31 00:00:00"), rs.getTimestamp("ValidTo"));
		statement.close();
		con.close();
		 */
	}


	@Test
	public void DeleteTest() throws Exception
	{
		/*
		// Arrange
		Takst takstinit = parseTakst(TAKST_TESTDATA_DIR + "/initial");
		Takst deleteupd = parseTakst(TAKST_TESTDATA_DIR + "/delete");

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement statement = con.createStatement();
		RecordDao versionedDao = new MySQLTemporalDao(con);

		// Act
		versionedDao.persistCompleteDatasets(takstinit.getDatasets());
		versionedDao.persistCompleteDatasets(deleteupd.getDatasets());

		// Assert
		Assert.assertEquals(new Integer(92), getRecordCount(versionedDao));

		ResultSet rs = statement.executeQuery("select * from Laegemiddel where DrugName like 'Kemadrin';");
		if (!rs.next())
		{
			Assert.fail("Did not find expected Laegemiddel Kemadrin");
		}
		Assert.assertEquals(dateFormat.parse("2009-07-31 00:00:00"), rs.getTimestamp("ValidTo"));
		statement.close();
		con.close();
		*/
	}


	@Test
	public void RealTest() throws Exception
	{
		/*
		// Arrange
		Takst takstinit = parseTakst(TAKST_TESTDATA_DIR + "/realtakst");

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement statement = con.createStatement();
		RecordDao versionedDao = new MySQLTemporalDao(con);

		// Act
		versionedDao.persistCompleteDatasets(takstinit.getDatasets());

		// Assert
		statement.close();
		con.close();
		*/
	}


	private Statement getStatement(RecordPersister versionedDao) throws SQLException
	{
		return ((MySQLTemporalDao) versionedDao).getConnection().createStatement();
	}


	private Integer getRecordCount(RecordPersister versionedDao) throws SQLException
	{

		Statement statement = getStatement(versionedDao);

		ResultSet rs = statement.executeQuery("Select count(*) from Laegemiddel");
		Integer recordsfound = 0;

		if (rs.next())
		{
			recordsfound = rs.getInt(1);
		}

		return recordsfound;
	}


	private Takst parseTakst(String dir) throws FileImporterException
	{
		/*
		TakstParser tp = new TakstParser();
		Takst takst = tp.parseTakst(Arrays.asList(TestHelper.getFile(dir).listFiles()));

		return takst;
		*/
		return null;
	}
}
