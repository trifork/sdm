package com.trifork.stamdata.importer.jobs.sks;


import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;

import com.trifork.stamdata.importer.jobs.FileImporter;
import com.trifork.stamdata.importer.jobs.FileImporterException;
import com.trifork.stamdata.importer.persistence.ConnectionFactory;
import com.trifork.stamdata.importer.persistence.MySQLTemporalDao;
import com.trifork.stamdata.importer.persistence.RecordPersister;
import com.trifork.stamdata.persistence.Dataset;
import com.trifork.stamdata.registre.sks.Organisation;


public class SksImporter extends FileImporter
{
	private static final Logger LOGGER = getLogger(SksImporter.class);


	public SksImporter(File rootDir, ConnectionFactory factory)
	{
		super(rootDir, factory);
		// TODO Auto-generated constructor stub
	}


	/**
	 * SKS files usually arrive monthly, we add a little padding to allow for
	 * the period to be extended.
	 */
	@Override
	public boolean checkRequiredFiles(File rootDir)
	{
		boolean present = false;

		for (File file : rootDir.listFiles())
		{
			// FIXME: This doesn't seem to check anything,
			// except that there is a file, and it ends with .txt?

			if (file.getName().toUpperCase().endsWith(".TXT"))
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

		RecordPersister persister = new MySQLTemporalDao(connection);

		for (File file : rootDir.listFiles())
		{
			if (file.getName().toUpperCase().endsWith(".TXT"))
			{
				Dataset<Organisation> dataset = SksParser.parseOrganisationer(file);
				persister.persistDeltaDataset(dataset);
			}
			else
			{
				LOGGER.warn("Ignoring file which because it does not end with matches *.TXT. file='{}'", file);
			}
		}

		return success;
	}


	@Override
	public String getName()
	{
		return "SKS Importer";
	}


	@Override
	public int getImportFrequency()
	{
		return 45;
	}
}
