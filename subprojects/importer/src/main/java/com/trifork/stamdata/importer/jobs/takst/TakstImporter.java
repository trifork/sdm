package com.trifork.stamdata.importer.jobs.takst;


import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.jobs.FileImporter;
import com.trifork.stamdata.importer.jobs.FileImporterException;
import com.trifork.stamdata.importer.persistence.ConnectionFactory;
import com.trifork.stamdata.importer.persistence.MySQLTemporalDao;
import com.trifork.stamdata.importer.persistence.RecordPersister;
import com.trifork.stamdata.registre.takst.Takst;


public class TakstImporter extends FileImporter
{
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());


	public TakstImporter(File rootDir, ConnectionFactory factory)
	{
		super(rootDir, factory);
	}


	@Override
	public boolean persistFileSet(File rootDir, Connection connection) throws FileImporterException
	{
		boolean success = true;

		Takst takst;

		TakstParser parser = new TakstParser();

		try
		{
			takst = parser.parseTakst(rootDir);

			RecordPersister versionedDao = new MySQLTemporalDao(connection);
			versionedDao.persistCompleteDatasets(takst.getDatasets());
		}
		catch (IOException e)
		{
			LOGGER.error("Could not persist taksten.", e);
			success = false;
		}

		return success;
	}


	@Override
	public boolean checkRequiredFiles(File rootDir)
	{
		boolean success = true;

		final String[] requiredFiles = new String[]
		{
				"system.txt", "lms01.txt", "lms02.txt",
				"lms03.txt", "lms04.txt", "lms05.txt",
				"lms07.txt", "lms09.txt", "lms10.txt",
				"lms11.txt", "lms12.txt", "lms13.txt",
				"lms14.txt", "lms15.txt", "lms16.txt",
				"lms17.txt", "lms18.txt", "lms19.txt",
				"lms20.txt", "lms23.txt", "lms24.txt",
				"lms25.txt", "lms26.txt", "lms27.txt",
				"lms28.txt"
		};

		File[] files = rootDir.listFiles();

		Map<String, File> fileMap = new HashMap<String, File>(files.length);

		for (File file : files)
		{
			fileMap.put(file.getName(), file);
		}

		for (String reqFile : requiredFiles)
		{
			if (!fileMap.containsKey(reqFile))
			{
				LOGGER.error("Did not find required file='{}'.", reqFile);
				success = false;
			}
		}

		return success;
	}


	@Override
	public String getName()
	{
		return "Takst Importer";
	}


	@Override
	public int getImportFrequency()
	{
		return 14;
	}

	// FIXME: Ensure that the takst is imported at least 36 hours before it
	// should be effectuated. Else an alarm should fire.
	// We can achive this my overriding one of the methods in the superclass.
}
