// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
//
// Contributor(s): Contributors are attributed in the source code
// where applicable.
//
// The Original Code is "Stamdata".
//
// The Initial Developer of the Original Code is Trifork Public A/S.
//
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
//
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.importer.jobs;

import static com.trifork.stamdata.Preconditions.checkArgument;
import static com.trifork.stamdata.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.persistence.AuditingPersister;


public class FileParserJob implements Job
{
	private static final Logger logger = LoggerFactory.getLogger(FileParserJob.class);

	private DateTime stabilizationPeriodEnd;
	private long inputDirSignature;

	private boolean isRunning = false;

	private final FileParser parser;
	private final File rootDir;

	private final int minimumImportFrequency;

	public FileParserJob(File rootDir, FileParser importer, int minimumImportFrequency)
	{
		checkArgument(minimumImportFrequency > 0);
		this.minimumImportFrequency = minimumImportFrequency;
		this.rootDir = checkNotNull(rootDir);
		this.parser = checkNotNull(importer);
	}

	/**
	 * Checks if new files are present, and handle them if true.
	 */
	@Override
	public final void run()
	{
		// Check for rejected files.
		// The parser is in a error state as long as there are files there.

		if (!isOK()) return;

		// Check if there are any files to import,
		// ignoring any unimportant files such as '.DS_Store'.

		if (getInputFiles().length == 0) return;

		isRunning = true;

		// If there are files to import wait a while and make sure the
		// files are stable.

		if (inputDirSignature != getDirSignature())
		{
			logger.info("Files discovered in the input directory. Making sure the files have been completly transfered before parsing will begin. parser={}", parser.getIdentifier());

			startStabilizationPeriod();
			return;
		}

		// Wait until the input files seem to be stable.

		if (stabilizationPeriodEnd.isAfterNow()) return;

		stabilizationPeriodEnd = null;
		inputDirSignature = -1;

		// Once stable check to see if all the expected files are there.

		File[] input = getInputFiles();

		if (!parser.ensureRequiredFileArePresent(input))
		{
			logger.error("Not all expected files could be found. Moving the input to the rejected folder. parser={}", parser.getIdentifier());

			moveAllFilesToRejected();

			return;
		}

		// If so parse and import them.

		doImport();
	}

	/**
	 * Determins wether a file should be ignored when checking for input.
	 * 
	 * @param file the file to check.
	 * 
	 * @return true if the file can safely be ignored.
	 */
	private boolean isMundane(File file)
	{
		return file.getName().equals(".DS_Store");
	}

	/**
	 * Returns a signature of the input dir's contained files.
	 * 
	 * @return an hash of the input directory's contents.
	 */
	protected long getDirSignature()
	{
		long hash = 0;

		for (File file : getInputFiles())
		{
			if (isMundane(file)) continue;

			hash += file.getName().hashCode() * (file.lastModified() + file.length());
		}

		return hash;
	}

	private void startStabilizationPeriod()
	{
		stabilizationPeriodEnd = new DateTime().plusSeconds(30);

		inputDirSignature = getDirSignature();
	}

	private boolean moveInputToProcessing()
	{
		boolean success;

		try
		{
			for (File file : getInputFiles())
			{
				// Skip any OS file and the like.

				if (isMundane(file)) continue;

				// Move each file.

				FileUtils.moveToDirectory(file, getProcessingDir(), true);
			}

			success = true;
		}
		catch (IOException e)
		{
			logger.error("Could not move all input files to processing directory. parser={} message=\"{}\"", parser.getIdentifier(), e.getMessage());
			success = false;
		}

		return success;
	}

	/**
	 * Wraps an import in a database transaction, and handles any errors that
	 * might occur while parsing a set of files.
	 */
	private void doImport()
	{
		moveInputToProcessing();

		Connection connection = null;

		try
		{
			logger.info("Starting import. parser={}", parser.getIdentifier());

			connection = MySQLConnectionManager.getConnection();

			parser.importFiles(getProcessingFiles(), new AuditingPersister(connection));
			ImportTimeManager.setImportTime(parser.getIdentifier(), new Date());

			connection.commit();

			logger.info("Import completed. parser={}", parser.getIdentifier());

			FileUtils.deleteQuietly(getProcessingDir());
		}
		catch (Exception e)
		{
			logger.error("Unhandled exception during import. Input files will be moved to the rejected folder.", e);

			try
			{
				connection.rollback();
			}
			catch (Exception ex)
			{
				logger.error("Could not rollback the connection.", ex);
			}

			moveAllFilesToRejected();
		}
		finally
		{
			MySQLConnectionManager.close(connection);
			isRunning = false;
		}
	}

	protected void moveAllFilesToRejected()
	{
		try
		{
			for (File f : getProcessingFiles())
			{
				FileUtils.moveFileToDirectory(f, getRejectedDir(), true);
			}

			for (File f : getInputFiles())
			{
				FileUtils.moveFileToDirectory(f, getRejectedDir(), true);
			}
		}
		catch (Exception e)
		{
			logger.error("The files couldn't be moved to the rejected folder.", e);
		}
	}

	public boolean isRejectedDirEmpty()
	{
		return getRejectedFiles().length == 0;
	}

	public File getInputDir()
	{
		File file = new File(rootDir, getIdentifier() + "/input");
		file.mkdirs();
		return file;
	}

	public File[] getInputFiles()
	{
		return filterOutMundaneFiles(getInputDir());
	}

	public File getRejectedDir()
	{
		File dir = new File(rootDir, getIdentifier() + "/rejected");
		dir.mkdirs();
		return dir;
	}

	public File[] getProcessingFiles()
	{
		return filterOutMundaneFiles(getProcessingDir());
	}

	public File getProcessingDir()
	{
		File file = new File(rootDir, getIdentifier() + "/processing");
		file.mkdirs();
		return file;
	}

	public File[] getRejectedFiles()
	{
		return filterOutMundaneFiles(getRejectedDir());
	}

	public File[] filterOutMundaneFiles(File root)
	{
		List<File> result = Lists.newArrayList();

		for (File file : root.listFiles())
		{
			if (isMundane(file)) continue;
			result.add(file);
		}

		return result.toArray(new File[] {});
	}

	/**
	 * Indicated wheather a file delivery is overdue.
	 * 
	 * If no files have previously been imported, this method always returns
	 * false.
	 * 
	 * @return true if the parser expected files but has not received any.
	 */
	public boolean isOverdue()
	{
		return hasBeenRun() && getNextDeadline().isBeforeNow();
	}

	/**
	 * The deadline for when the next files have to have been imported.
	 * 
	 * The returned date will always be at midnight to avoid the day of time
	 * slipping everytime a new batch is imported.
	 * 
	 * @return the timestamp with the deadline.
	 */
	public DateTime getNextDeadline()
	{
		return getLatestRunTime().plusDays(minimumImportFrequency).toDateMidnight().toDateTime();
	}

	/** {@inheritDoc} */
	@Override
	public DateTime getLatestRunTime()
	{
		return ImportTimeManager.getLastImportTime(parser.getIdentifier());
	}

	/** {@inheritDoc} */
	public boolean hasBeenRun()
	{
		return getLatestRunTime() != null;
	}

	/** {@inheritDoc} */
	@Override
	public String getIdentifier()
	{
		return parser.getIdentifier();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isOK()
	{
		return isRejectedDirEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isExecuting()
	{
		return isRunning;
	}

	/** {@inheritDoc} */
	@Override
	public String getCronExpression()
	{
		// File parsers poll their input directories
		// every 5 seconds.

		return "0/5 * * * * ?";
	}

	/** {@inheritDoc} */
	@Override
	public String getHumanName()
	{
		return parser.getHumanName();
	}
}
