package com.trifork.stamdata.importer.jobs.cpr;


import static java.lang.System.*;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.sql.*;
import java.util.Date;

import org.slf4j.Logger;

import com.trifork.stamdata.importer.jobs.FileImporter;
import com.trifork.stamdata.importer.jobs.FileImporterException;
import com.trifork.stamdata.importer.jobs.FilePersistException;
import com.trifork.stamdata.importer.persistence.ConnectionFactory;
import com.trifork.stamdata.importer.persistence.MySQLTemporalDao;


public class CprImporter extends FileImporter
{
	private static final Logger LOGGER = getLogger(FileImporter.class);

	// TODO (thb): Lokalitet <> 'Addressebeskyttelse'? Why, what is the purpose?

	private static String WHERE_PROTECTION_IS_ACTIVE_SQL = "WHERE NavneBeskyttelseStartDato < NOW() " + "AND (NavneBeskyttelseSletteDato > NOW() OR ISNULL(NavneBeskyttelseSletteDato)) " + "AND Lokalitet <> 'Adressebeskyttet' ";

	private static String BACKUP_NAME_AND_ADDRESS_SQL = "REPLACE INTO AdresseBeskyttelse " + "(       CPR, Fornavn, Mellemnavn, Efternavn, CoNavn, Lokalitet, Vejnavn, Bygningsnummer, Husnummer, Etage, SideDoerNummer, Bynavn, Postnummer, PostDistrikt, NavneBeskyttelseStartDato, NavneBeskyttelseSletteDato, VejKode, KommuneKode) " + "(SELECT CPR, Fornavn, Mellemnavn, Efternavn, CoNavn, Lokalitet, Vejnavn, Bygningsnummer, Husnummer, Etage, SideDoerNummer, Bynavn, Postnummer, PostDistrikt, NavneBeskyttelseStartDato, NavneBeskyttelseSletteDato, VejKode, KommuneKode " + "FROM Person " + WHERE_PROTECTION_IS_ACTIVE_SQL + "ORDER BY validTo)";

	private static String APPLY_NAME_AND_ADDRESS_PROTECTION_SQL = "UPDATE Person SET Fornavn='Navnebeskyttet', Mellemnavn='Navnebeskyttet', Efternavn='Navnebeskyttet', CoNavn='Navnebeskyttet', Lokalitet='Adressebeskyttet', Vejnavn='Adressebeskyttet', Bygningsnummer='99', Husnummer='99', Etage='99', SideDoerNummer='', Bynavn='Adressebeskyttet', Postnummer='9999', PostDistrikt='Adressebeskyttet', VejKode='99', KommuneKode='999', ModifiedBy='Address And Name Protection' " + WHERE_PROTECTION_IS_ACTIVE_SQL;


	public CprImporter(File rootDir, ConnectionFactory factory)
	{
		super(rootDir, factory);
	}


	@Override
	public boolean persistFileSet(File rootDir, Connection connection) throws FileImporterException, SQLException
	{
		boolean success = false;

		LOGGER.info("Starting import of CPR files.");

		MySQLTemporalDao persister = new MySQLTemporalDao(connection);

		for (File file : rootDir.listFiles())
		{
			if (!isPersonFile(file))
			{
				LOGGER.error("CPR data file='{}' does not appear to be a valid CPR person data file.", file);
				return false;
			}

			LOGGER.info("Parsing CPR data file='{}'.", file);

			// FIXME: Argh! Statics galore...
			CPRDataset data = CPRParser.parse(file);

			if (isDeltaFile(file))
			{
				// Check that the sequence is honored.

				Date actualPrevVersion = getVersion(connection);
				Date expectedPrevVersion = data.getExpectedPreviousVersion();

				if (actualPrevVersion == null)
				{
					// FIXME: Why assume, when you could just check if the db is empty?
					
					LOGGER.warn("Could not fetch latest CPR version from the database. Asuming empty database and skipping import sequence checks.");
				}

				if (actualPrevVersion == null || expectedPrevVersion.equals(actualPrevVersion))
				{
					updateVersion(data.getEffectuationDate(), connection);
				}
				else
				{
					LOGGER.error("CPR import out of sequence: expected_previous_date='{}', actual_previous_version='{}'", expectedPrevVersion, actualPrevVersion);
					return false;
				}
			}

			persister.persistDeltaDataset(data.getPersonoplysninger());
			persister.persistDeltaDataset(data.getNavneoplysninger());
			persister.persistDeltaDataset(data.getKlarskriftadresse());
			persister.persistDeltaDataset(data.getNavneBeskyttelse());
			persister.persistDeltaDataset(data.getBarnRelation());
			persister.persistDeltaDataset(data.getForaeldreMyndighedRelation());
			persister.persistDeltaDataset(data.getUmyndiggoerelseVaergeRelation());

			LOGGER.info("Successfully finished parsing CPR file='{}'.", file);
		}

		LOGGER.info("Applying name and address protection.");

		PreparedStatement statement = null;
		
		try
		{
			Timestamp transactionTime = new Timestamp(currentTimeMillis());

			// Backup name and address to the 'AdresseBeskyttelse' table for all
			// citizens with active name and address protection.

			statement = connection.prepareStatement(BACKUP_NAME_AND_ADDRESS_SQL);
			statement.setTimestamp(1, transactionTime);
			statement.execute();
			statement.close();
			
			// Censor names and addresses for all citizens with active name and 
			// address protection.

			statement = connection.prepareStatement(APPLY_NAME_AND_ADDRESS_PROTECTION_SQL);
			statement.setTimestamp(1, transactionTime);
			statement.execute();
			statement.close();
			
			success = true;
		}
		catch (Exception e)
		{
			LOGGER.error("Name and address protection failed.", e);
		}
		finally
		{
			try
			{
				if (statement != null && !statement.isClosed()) statement.close();
			}
			catch (Exception e)
			{
				LOGGER.error("Could not close database statement.", e);
			}
		}

		return success;
	}


	protected boolean isPersonFile(File file)
	{
		return (file.getName().startsWith("D") && file.getName().indexOf(".L4311") == 7);
	}


	protected boolean isDeltaFile(File file)
	{
		return (file.getName().startsWith("D") && file.getName().endsWith(".L431101"));
	}


	@Override
	public boolean checkRequiredFiles(File rootDir)
	{
		// TODO: Is this right?
		// Don't we know anything about what is being input.
		return true;
	}


	private Date getVersion(Connection connection) throws SQLException
	{
		Date version = null;

		Statement statement = connection.createStatement();
		ResultSet results = statement.executeQuery("SELECT MAX(IkraftDato) FROM PersonIkraft");

		if (results.next())
		{
			version = results.getTimestamp(1);
		}

		statement.close();

		return version;
	}


	private void updateVersion(Date date, Connection con) throws FilePersistException, SQLException
	{
		PreparedStatement statement = con.prepareStatement("INSERT INTO PersonIkraft (IkraftDato) VALUES (?)");

		// This is a date, not a time stamp.
		statement.setDate(1, new java.sql.Date(date.getTime()));

		statement.execute();

		statement.close();
	}


	@Override
	public String getName()
	{
		return "CPR Importer";
	}


	/**
	 * CRP must be imported every 12 days.
	 * 
	 * NOTE: Maximum gap observed is 7 days without CPR during Christmas 2008.
	 */
	@Override
	public int getImportFrequency()
	{
		return 12;
	}
}
