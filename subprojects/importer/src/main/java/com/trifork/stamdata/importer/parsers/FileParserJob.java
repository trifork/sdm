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

package com.trifork.stamdata.importer.parsers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.util.DateUtils;


/**
 * The implementation of the file spooler. Based on the FileSpoolerSetup the
 * spooler is started and a monitor thread is spanned. The FileSpoolerImpl
 * continue to monitor the spooler but all activations are made in the newly
 * created thread.
 * 
 * @author Jan Buchholdt <jbu@trifork.com>
 * @author Thomas Børlum <thb@trifork.com>
 */
public class FileParserJob implements Job
{
	private static final Logger logger = LoggerFactory.getLogger(FileParserJob.class);

	private DateTime stabilizationPeriodEnd;
	private long inputDirSignature;

	private Date lastRun;

	private final FileParser importer;
	private final File rootDir;
	
	private JobStatus state = JobStatus.OK;
	private JobActivity activity = JobActivity.AWAITING;

	public FileParserJob(File rootDir, FileParser importer)
	{
		this.rootDir = rootDir;
		this.importer = importer;
		this.lastRun = ImportTimeManager.getLastImportTime(importer.getIdentifier());
	}

	/**
	 * Checks if new files are present, and handle them if true.
	 * 
	 * @throws IOException
	 */
	@Override
	public final void run()
	{
		// Check for rejected files.
		// The importer is in a error state as long as there are files there.

		if (!isRejectedDirEmpty())
		{
			state = JobStatus.ERROR;
			return;
		}

		// Check if there are any files to import.

		if (getInputDir().list().length == 0) return;

		// If there are files to import wait a while and make sure the
		// files are stable.

		if (inputDirSignature != getDirSignature())
		{
			logger.info("Files discovered in the input directory. Making sure the files have been completly transfered before parsing will begin. parser={}", importer.getIdentifier());
			
			startStabilizationPeriod();
			return;
		}

		// Wait until the input files seem to be stable.

		final DateTime now = new DateTime();
		if (now.isAfter(stabilizationPeriodEnd)) return;

		// Once stable check to see if all the expected files are there.

		File[] input = getInputDir().listFiles();

		if (!importer.ensureRequiredFileArePresent(input))
		{
			logger.error("Not all expected files could be found. Moving the input to the rejected folder. parser={}", importer.getIdentifier());
			
			moveAllFilesToRejected();
			state = JobStatus.ERROR;
			
			return;
		}

		// If so parse and import them.

		moveInputToProcessing();

		doImport();
	}

	private boolean isMundane(File file)
	{
		return file.getName().equals(".DS_Store");
	}
	
	private boolean areMundane(File[] rejFiles)
	{
		for (File file : rejFiles)
		{
			if (!isMundane(file)) return false;
		}

		return true;
	}

	/**
	 * Returns a signature of the dir's contained files.
	 * 
	 * @param inputDir
	 * @return
	 */
	protected long getDirSignature()
	{
		long hash = 0;

		for (File file : getInputDir().listFiles())
		{
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
			for (File file : getInputDir().listFiles())
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
			logger.error("Could not move all input files to processing directory. parser={} message=\"{}\"", importer.getIdentifier(), e.getMessage());
			success = false;
		}

		return success;
	}

	private void doImport()
	{
		activity = JobActivity.IMPORTING;

		Connection connection = null;

		try
		{
			connection = MySQLConnectionManager.getConnection();

			importer.importFiles(getProcessingDir().listFiles(), connection);

			lastRun = new Date();

			ImportTimeManager.setImportTime(importer.getIdentifier(), lastRun);

			connection.commit();
				
			logger.info("Import completed. parser={}", importer);
			
			FileUtils.deleteQuietly(getProcessingDir());
			
			activity = JobActivity.AWAITING;
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
		}
	}

	protected void moveAllFilesToRejected()
	{
		try
		{
			for (File f : getProcessingDir().listFiles())
			{
				FileUtils.moveFileToDirectory(f, getRejectedDir(), true);
			}

			for (File f : getInputDir().listFiles())
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
		File[] filesInRejectedDir = getRejectedDir().listFiles();

		return filesInRejectedDir.length == 0 || areMundane(filesInRejectedDir);
	}

	public File getInputDir()
	{
		File file = new File(rootDir, getIdentifier() + "/input");
		file.mkdirs();
		return file;
	}

	public File getProcessingDir()
	{
		File file = new File(rootDir, getIdentifier() + "/processing");
		file.mkdirs();
		return file;
	}

	public File getRejectedDir()
	{
		File file = new File(rootDir, getIdentifier() + "/rejected");
		file.mkdirs();
		return file;
	}

	public String getNextImportExpectedBeforeFormatted()
	{
		return "FAKE DATE"; // DateUtils.toMySQLdate(getNextImportExpectedBefore(lastRun));
	}

	public String getLastImportFormatted()
	{
		Date lastImport = ImportTimeManager.getLastImportTime(importer.getIdentifier());

		if (lastImport == null)
		{
			return "Never";
		}
		else
		{
			return DateUtils.toMySQLdate(lastImport);
		}
	}

	public Date getLastRun()
	{
		return lastRun;
	}

	public boolean isOverdue()
	{
		return true; //getNextImportExpectedBefore(lastRun).before(new Date());
	}
	
	@Override
	public String getIdentifier()
	{
		return importer.getIdentifier();
	}
	
	@Override
	public JobStatus getState()
	{
		return isRejectedDirEmpty() ? JobStatus.ERROR : JobStatus.OK;
	}
	
	@Override
	public JobActivity getActivity()
	{
		return activity;
	}
	
	/**
	 * All file parser jobs continueously poll their input directory
	 * for new data.
	 * @return 
	 */
	@Override
	public String getSchedule()
	{
		return "* * * * * ?";
	}
}
