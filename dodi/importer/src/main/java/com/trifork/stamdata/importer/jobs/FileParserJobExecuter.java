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

import static com.google.common.base.Preconditions.*;

import java.io.*;
import java.sql.Connection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.slf4j.*;

import com.google.common.collect.Lists;
import com.trifork.stamdata.importer.persistence.*;


public class FileParserJobExecuter implements Executer
{
	private static final Logger logger = LoggerFactory.getLogger(FileParserJobExecuter.class);

	private static final File ROOT_DIR = new File(System.getProperty("jboss.server.data.dir") + "/stamdata/");

	private DateTime stabilizationPeriodEnd;
	private long inputDirSignature;

	private boolean isRunning = false;

	private final FileParserJob parser;
	private final ConnectionPool connectionPool;

	public FileParserJobExecuter(FileParserJob parser, ConnectionPool connectionPool)
	{
		this.parser = checkNotNull(parser, "parser");
		this.connectionPool = checkNotNull(connectionPool, "connectionPool");
	}

	/**
	 * Checks if new files are present, and handle them if true.
	 */
	@Override
	public final void run()
	{
		// Check for rejected files.
		// The parser is in a error state as long as there are files there.

		if (!isOK())
		{
			return;
		}

		// Check if there are any files to import,
		// ignoring any unimportant files such as '.DS_Store'.

		if (getInputFiles().length == 0)
		{
			return;
		}

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

		if (stabilizationPeriodEnd.isAfterNow())
		{
			return;
		}

		stabilizationPeriodEnd = null;
		inputDirSignature = -1;

		// Once stable check to see if all the expected files are there.

		File[] input = getInputFiles();

		if (!parser.checkFileSet(input))
		{
			logger.error("Not all expected files could be found. Moving the input to the rejected folder. parser={}", parser.getIdentifier());

			moveAllFilesToRejected();

			return;
		}

		// If so parse and import them.

		doImport();
	}

	/**
	 * Determines whether a file should be ignored when checking for input.
	 * 
	 * @param file
	 *            the file to check.
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
			if (isMundane(file))
			{
				continue;
			}

			hash += file.getName().hashCode() * (file.lastModified() + file.length());
		}

		return hash;
	}

	private void startStabilizationPeriod()
	{
		stabilizationPeriodEnd = new DateTime().plusSeconds(5);

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

				if (isMundane(file))
				{
					continue;
				}

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
			logger.info("Starting file parser job. job={}", parser.getHumanName());

			connection = connectionPool.getConnection();

			parser.run(getProcessingFiles(), new AuditingPersister(connection), connection, 0);
			ImportTimeManager.updateLastRunTime(connection, parser);

			connection.commit();

			logger.info("Parser finished successfully. parser={}", parser.getHumanName());

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
			try
			{
				if (connection != null) connection.close();
			}
			catch (Exception e)
			{
				
			}

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
		File file = new File(ROOT_DIR + parser.getIdentifier() + "/input");
		file.mkdirs();
		return file;
	}

	public File[] getInputFiles()
	{
		return filterOutMundaneFiles(getInputDir());
	}

	public File getRejectedDir()
	{
		File dir = new File(ROOT_DIR + parser.getIdentifier() + "/rejected");
		dir.mkdirs();
		return dir;
	}

	public File[] getProcessingFiles()
	{
		return filterOutMundaneFiles(getProcessingDir());
	}

	public File getProcessingDir()
	{
		File file = new File(ROOT_DIR + getIdentifier() + "/processing");
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
			if (isMundane(file))
			{
				continue;
			}
			result.add(file);
		}

		return result.toArray(new File[] {});
	}

	/**
	 * Indicated whether a file delivery is overdue.
	 * 
	 * If no files have previously been imported, this method always returns
	 * false.
	 * 
	 * @return true if the parser expected files but has not received any.
	 */
	@Override
	public boolean isOverdue()
	{
		return hasBeenRun() && getNextDeadline().isBeforeNow();
	}

	/**
	 * The deadline for when the next files have to have been imported.
	 * 
	 * The returned date will always be at midnight to avoid the day of time
	 * slipping every time a new batch is imported.
	 * 
	 * @return the timestamp with the deadline.
	 */
	public DateTime getNextDeadline()
	{
		return getLatestRunTime().plus(parser.getMaxTimeGap()).toDateMidnight().toDateTime();
	}

	/** {@inheritDoc} */
	@Override
	public DateTime getLatestRunTime()
	{
		return ImportTimeManager.getLastRunTime(connectionPool.getConnection(), parser);
	}

	/** {@inheritDoc} */
	@Override
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
