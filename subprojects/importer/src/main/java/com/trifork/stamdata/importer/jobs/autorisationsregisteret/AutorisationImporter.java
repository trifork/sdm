package com.trifork.stamdata.importer.jobs.autorisationsregisteret;


import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;

import com.trifork.stamdata.importer.jobs.FileImporter;
import com.trifork.stamdata.importer.jobs.FileImporterException;
import com.trifork.stamdata.importer.persistence.ConnectionFactory;
import com.trifork.stamdata.importer.persistence.MySQLTemporalDao;


public class AutorisationImporter extends FileImporter
{
	public AutorisationImporter(File rootDir, ConnectionFactory factory)
	{
		super(rootDir, factory);
	}


	private static final Logger LOGGER = getLogger(AutorisationImporter.class);


	public boolean areRequiredInputFilesPresent(List<File> files)
	{
		if (files.size() == 0) return false;

		for (File file : files)
		{
			if (getDateFromInputFileName(file.getName()) == null) return false;
		}

		return true;
	}


	@Override
	public boolean persistFileSet(File rootDir, Connection connection) throws FileImporterException, SQLException
	{
		boolean success = true;

		MySQLTemporalDao persister = new MySQLTemporalDao(connection);
		doImport(rootDir, persister);

		return success;
	}


	@Override
	public boolean checkRequiredFiles(File rootDir)
	{
		return false;
	}


	/**
	 * Import the files and persist the data.
	 * 
	 * @param rootDir
	 *            the root directory from which the autorisations should be
	 *            parsed.
	 * @param persister
	 *            the persister to which autorisations should be saved.
	 * @throws SQLException
	 *             if something goes wrong in the persister.
	 * @throws FileImporterException
	 *             if importing fails.
	 */
	private void doImport(File rootDir, MySQLTemporalDao persister) throws FileImporterException
	{
		for (File file : rootDir.listFiles())
		{
			Date date = getDateFromInputFileName(file.getName());

			if (date == null)
			{
				throw new FileImporterException("Filename format is invalid! Date could not be extracted");
			}

			try
			{
				AutorisationsregisterParser parser = new AutorisationsregisterParser();
				Autorisationsregisterudtraek dataset = parser.parse(file, date);
				persister.persistCompleteDataset(dataset);

			}
			catch (Exception e)
			{

				String mess = "Error reader autorisationsfil: " + file;
				LOGGER.error(mess, e);
				throw new FileImporterException(mess, e);
			}
		}
	}


	/**
	 * Extracts the date from the filename
	 * 
	 * @param fileName
	 * @return
	 */
	private Date getDateFromInputFileName(String fileName)
	{
		try
		{
			// FIXME: Use a date formatter...

			int year = new Integer(fileName.substring(0, 4));
			int month = new Integer(fileName.substring(4, 6));
			int date = new Integer(fileName.substring(6, 8));

			return new GregorianCalendar(year, month - 1, date).getTime();
		}
		catch (NumberFormatException e)
		{
			// Report an error.

			return null;
		}
	}


	@Override
	public String getName()
	{
		return null;
	}


	/**
	 * Import at least every 30 days.
	 * 
	 * Largest gap observed was 15 days from 2008-10-18 to 2008-11-01.
	 */
	@Override
	public int getImportFrequency()
	{
		return 30;
	}

}
