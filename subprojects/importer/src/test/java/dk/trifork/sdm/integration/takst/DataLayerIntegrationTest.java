package dk.trifork.sdm.integration.takst;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import dk.trifork.sdm.config.MySQLConnectionManager;
import dk.trifork.sdm.dao.Persister;
import dk.trifork.sdm.dao.mysql.AuditingPersister;
import dk.trifork.sdm.importer.exceptions.FileImporterException;
import dk.trifork.sdm.importer.takst.TakstParser;
import dk.trifork.sdm.importer.takst.model.Takst;


/**
 * Integration test of the database access layer. Tests that a dataset can be
 * written to the database
 * 
 * @author Anders Bo Christensen
 * 
 */
public class DataLayerIntegrationTest {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	@Before
	public void cleanDatabase() throws Exception {

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
		statement.execute("truncate table Beregningsregler");
		statement.execute("truncate table EmballagetypeKoder");
		statement.execute("truncate table Enhedspriser");
		statement.execute("truncate table Indholdsstoffer");
		statement.execute("truncate table Laegemiddelnavn");
		statement.execute("truncate table Opbevaringsbetingelser");
		statement.execute("truncate table OplysningerOmDosisdispensering");
		statement.execute("truncate table Pakningskombinationer");
		statement.execute("truncate table PakningskombinationerUdenPriser");
		statement.execute("truncate table Priser");
		statement.execute("truncate table Rekommandationer");
		statement.execute("truncate table SpecialeForNBS");
		statement.execute("truncate table Substitution");
		statement.execute("truncate table SubstitutionAfLaegemidlerUdenFastPris");
		statement.execute("truncate table Tilskudsintervaller");
		statement.execute("truncate table TilskudsprisgrupperPakningsniveau");
		statement.execute("truncate table UdgaaedeNavne");
		statement.execute("truncate table Udleveringsbestemmelser");
		statement.execute("truncate table Firma");
		statement.close();
		con.close();
	}

	@Test
	public void ImportTest() throws Exception {

		// Arrange
		Takst takst = parseTakst("data/takst/initial");

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement statement = con.createStatement();
		AuditingPersister versionedDao = new AuditingPersister(con);

		// Act
		versionedDao.persistCompleteDataset(takst.getDatasets());

		// Assert
		Assert.assertEquals(new Integer(92), getRecordCount(versionedDao));

		ResultSet rs = statement.executeQuery("select * from Laegemiddel where DrugName like 'Kemadrin';");
		if (!rs.next()) {
			Assert.fail("Did not find expected Laegemiddel Kemadrin");
		}
		Assert.assertEquals(dateFormat.parse("2999-12-31 00:00:00"), rs.getTimestamp("ValidTo"));
		statement.close();
		con.close();
	}

	@Test
	public void UpdateTest() throws Exception {

		// Arrange
		Takst takstinit = parseTakst("data/takst/initial");
		Takst takstupd = parseTakst("data/takst/update");

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement statement = con.createStatement();
		AuditingPersister versionedDao = new AuditingPersister(con);

		// Act
		versionedDao.persistCompleteDataset(takstinit.getDatasets());
		versionedDao.persistCompleteDataset(takstupd.getDatasets());

		// Assert
		Assert.assertEquals(new Integer(93), getRecordCount(versionedDao));

		ResultSet rs = statement.executeQuery("select * from Laegemiddel where DrugName like 'Kemadrin';");
		if (!rs.next()) {
			Assert.fail("Did not find expected Laegemiddel Kemadrin");
		}
		Assert.assertEquals(dateFormat.parse("2009-07-30 00:00:00"), rs.getTimestamp("ValidTo"));

		rs = statement.executeQuery("select * from Laegemiddel where DrugName like 'Kemadron';");
		if (!rs.next()) {
			Assert.fail("Did not find expected Laegemiddel Kemadron");
		}
		Assert.assertEquals(dateFormat.parse("2999-12-31 00:00:00"), rs.getTimestamp("ValidTo"));
		statement.close();
		con.close();

	}

	@Test
	public void DeleteTest() throws Exception {

		// Arrange
		Takst takstinit = parseTakst("data/takst/initial");
		Takst deleteupd = parseTakst("data/takst/delete");

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement statement = con.createStatement();
		AuditingPersister versionedDao = new AuditingPersister(con);

		// Act
		versionedDao.persistCompleteDataset(takstinit.getDatasets());
		versionedDao.persistCompleteDataset(deleteupd.getDatasets());

		// Assert
		Assert.assertEquals(new Integer(92), getRecordCount(versionedDao));

		ResultSet rs = statement.executeQuery("select * from Laegemiddel where DrugName like 'Kemadrin';");
		if (!rs.next()) {
			Assert.fail("Did not find expected Laegemiddel Kemadrin");
		}
		Assert.assertEquals(dateFormat.parse("2009-07-31 00:00:00"), rs.getTimestamp("ValidTo"));
		statement.close();
		con.close();
	}

	@Test
	public void RealTest() throws Exception {

		// Arrange
		Takst takstinit = parseTakst("data/takst/realtakst");

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement statement = con.createStatement();
		AuditingPersister versionedDao = new AuditingPersister(con);

		// Act
		versionedDao.persistCompleteDataset(takstinit.getDatasets());

		// Assert
		statement.close();
		con.close();
	}

	private Statement getStatement(Persister versionedDao) throws SQLException {

		return ((AuditingPersister) versionedDao).getConnection().createStatement();
	}

	private Integer getRecordCount(Persister versionedDao) throws SQLException {

		Statement statement = getStatement(versionedDao);

		ResultSet rs = statement.executeQuery("Select count(*) from Laegemiddel");
		Integer recordsfound = 0;
		if (rs.next()) {
			recordsfound = rs.getInt(1);
		}
		return recordsfound;
	}

	private Takst parseTakst(String dir) throws FileImporterException {

		File file = FileUtils.toFile(getClass().getClassLoader().getResource(dir));
		TakstParser tp = new TakstParser();
		Takst takst = tp.parseFiles(Arrays.asList(file.listFiles()));

		return takst;
	}
}
