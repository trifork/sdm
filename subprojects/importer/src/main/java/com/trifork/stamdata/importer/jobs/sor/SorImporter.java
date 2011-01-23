package com.trifork.stamdata.importer.jobs.sor;


import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.trifork.stamdata.importer.jobs.FileImporter;
import com.trifork.stamdata.importer.jobs.FileImporterException;
import com.trifork.stamdata.importer.persistence.ConnectionFactory;
import com.trifork.stamdata.importer.persistence.MySQLTemporalDao;


public class SorImporter extends FileImporter
{
	public SorImporter(File rootDir, ConnectionFactory factory)
	{
		super(rootDir, factory);
	}


	@Override
	public boolean checkRequiredFiles(File rootDir)
	{
		boolean present = false;

		for (File file : rootDir.listFiles())
		{
			if (file.getName().toLowerCase().endsWith(".xml"))
			{
				present = true;
			}
		}

		return present;
	}


	@Override
	public boolean persistFileSet(File rootDir, Connection connection) throws FileImporterException, SQLException, IOException
	{
		boolean success = true;

		for (File file : rootDir.listFiles())
		{
			MySQLTemporalDao persister = new MySQLTemporalDao(connection);

			SORDataSets dataSets = SORParser.parse(file);
			persister.persistCompleteDataset(dataSets.getPraksisDS());
			persister.persistCompleteDataset(dataSets.getYderDS());
			persister.persistCompleteDataset(dataSets.getSygehusDS());
			persister.persistCompleteDataset(dataSets.getSygehusAfdelingDS());
			persister.persistCompleteDataset(dataSets.getApotekDS());
		}

		return success;
	}


	@Override
	public String getName()
	{
		return "SOR Importer";
	}


	/**
	 * SOR should be updated every day.
	 * 
	 * We add a margin of 2 days to ensure the that the alarm does not go off if
	 * it is only a little late. The data is not crucial.
	 */
	@Override
	public int getImportFrequency()
	{
		return 3;
	}

}
