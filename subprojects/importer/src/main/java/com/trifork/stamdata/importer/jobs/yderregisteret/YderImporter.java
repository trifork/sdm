package com.trifork.stamdata.importer.jobs.yderregisteret;


import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;

import com.trifork.stamdata.importer.jobs.FileImporter;
import com.trifork.stamdata.importer.jobs.FileImporterException;
import com.trifork.stamdata.importer.persistence.ConnectionFactory;
import com.trifork.stamdata.importer.persistence.MySQLTemporalDao;


public class YderImporter extends FileImporter
{
	private static final Logger LOGGER = getLogger(YderImporter.class);


	public YderImporter(File rootDir, ConnectionFactory factory)
	{
		super(rootDir, factory);
	}


	@Override
	public String getName()
	{
		return "Yderregister Importer";
	}


	@Override
	public boolean persistFileSet(File rootDir, Connection connection) throws FileImporterException, SQLException
	{
		boolean success = true;

		LOGGER.info("Verifying versions.");

		Integer newVersion = null;

		// Make sure the files are all from the same version.

		for (File file : rootDir.listFiles())
		{
			Integer fileVersion;

			if (file.getName().endsWith("XML") && file.getName().length() >= 15)
			{
				String versionString = file.getName().substring(10, 15);
				fileVersion = new Integer(versionString);
			}
			else
			{
				LOGGER.debug("Skipping unreconized file='{}'.", file);
				continue;
			}

			if (newVersion == null)
			{
				newVersion = fileVersion;
			}
			else if (!newVersion.equals(fileVersion))
			{
				LOGGER.error("Incorrect yderregister file. File version numbers did not match.");
				success = true;
				break;
			}
		}

		if (success)
		{
			Integer currentVersion = getVersion(connection);

			// Verify the version sequence.

			if (currentVersion == null)
			{
				LOGGER.warn("No previous version of yderregisteret detected.");
			}
			else if (currentVersion > newVersion)
			{
				// FIXME: Shouldn't we check that (newVersion == currentVersion
				// + 1)?
				LOGGER.error("File in import of yderregisteret are out of version sequence.");
				success = false;
			}
			else
			{
				// If everything is okay, we can start parsing the data.

				MySQLTemporalDao persister = new MySQLTemporalDao(connection);

				YderregisterParser parser = new YderregisterParser();
				YderregisterDatasets yderreg = parser.parseYderregister(rootDir);

				persister.persistCompleteDataset(yderreg.getYderregisterDS());
				persister.persistCompleteDataset(yderreg.getYderregisterPersonDS());

				// Finally update the version.

				updateVersion(connection, newVersion);
			}
		}

		return success;
	}


	@Override
	public boolean checkRequiredFiles(File rootDir)
	{
		Set<String> requiredFileTypes = new HashSet<String>();

		requiredFileTypes.add("K05");
		requiredFileTypes.add("K40");
		requiredFileTypes.add("K45");
		requiredFileTypes.add("K1025");
		requiredFileTypes.add("K5094");

		for (File file : rootDir.listFiles())
		{
			String filename = file.getName();

			int firstDot = filename.indexOf('.');
			int lastDot = filename.lastIndexOf('.');

			if (firstDot != -1 && firstDot != lastDot)
			{
				String fileType = filename.substring(firstDot + 1, lastDot);

				requiredFileTypes.remove(fileType);
			}
		}

		return requiredFileTypes.isEmpty();
	}


	/**
	 * Updates to authorization register should arrive at least every quarter.
	 */
	@Override
	public int getImportFrequency()
	{
		final int QUARTER_YEAR = 95;

		return QUARTER_YEAR;
	}


	private Integer getVersion(Connection connection) throws SQLException
	{
		Integer latestInDB = null;

		Statement statement = connection.createStatement();
		ResultSet results = statement.executeQuery("SELECT MAX(Loebenummer) FROM YderLoebenummer");

		if (results.next())
		{
			latestInDB = results.getInt(1);
		}

		statement.close();

		return latestInDB;
	}


	private void updateVersion(Connection connection, int version) throws SQLException
	{
		final String SQL = "INSERT INTO YderLoebenummer (Loebenummer) VALUES (?)";
		PreparedStatement statement = connection.prepareStatement(SQL);

		statement.setInt(1, version);

		// TODO: Check if it is actually inserted.
		statement.executeUpdate();

		statement.close();
	}
}
