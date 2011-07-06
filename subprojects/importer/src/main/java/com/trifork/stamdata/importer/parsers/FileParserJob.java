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
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
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
public class FileParserJob extends Job
{
	private static final Logger logger = LoggerFactory.getLogger(FileParserJob.class);

	private Date stabilizationPeriodEnd;
	private long inputDirSignature;

	private Date lastRun;

	private final FileImporter importer;
	private final File rootDir;

	public FileParserJob(String schedule, File rootDir, FileImporter importer)
	{
		this.rootDir = rootDir;
		this.importer = importer;
		this.lastRun = ImportTimeManager.getLastImportTime(importer.getIdentifier());

		setStatus(JobStatus.OK);
		setActivity(JobActivity.AWAITING);
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
			setStatus(JobStatus.ERROR);
			return;
		}

		// Check if there are any files to import.

		if (getInputDir().list().length == 0) return;

		// If there are files to import and

		if (inputDirSignature != getDirSignature(getInputDir()))
		{
			startStabilizationPeriod();
		}

		// Wait until the input files seem to be stable.

		if (!Calendar.getInstance().after(stabilizationPeriodEnd))
		{
			logger.info("File importer stabilizing.");
			return;
		}

		// Once stable check to see if they are all there.

		File[] input = getInputDir().listFiles();

		if (!importer.ensureRequiredFileArePresent(input))
		{
			moveAllFilesToRejected();
			setStatus(JobStatus.ERROR);
			return;
		}

		// If so parse and import them.

		File fileSet;

		try
		{
			fileSet = moveToProcessingFileset(input);
		}
		catch (IOException e)
		{
			setStatus(JobStatus.ERROR);
			return;
		}

		doImport(fileSet);
	}

	private boolean areMundane(File[] rejFiles)
	{
		for (File file : rejFiles)
		{
			if (!file.getName().equals(".DS_Store"))
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns a signature of the dir's contained files.
	 * 
	 * @param inputDir
	 * @return
	 */
	static long getDirSignature(File inputDir)
	{
		long hash = 0;

		for (File file : inputDir.listFiles())
		{
			hash += file.getName().hashCode() * (file.lastModified() + file.length());
		}

		return hash;
	}

	private void startStabilizationPeriod()
	{
		setActivity(JobActivity.STABILIZING);

		Calendar end = Calendar.getInstance();
		end.add(Calendar.SECOND, 30);
		stabilizationPeriodEnd = end.getTime();

		inputDirSignature = getDirSignature(getInputDir());
	}

	private File moveToProcessingFileset(File[] files) throws IOException
	{
		File destination = new File(getProcessingDir(), DateUtils.toFilenameDatetime(new Date()));
		destination.mkdirs();

		for (File file : files)
		{
			FileUtils.moveToDirectory(file, destination, true);
		}

		return destination;
	}

	private void doImport(File input)
	{
		setActivity(JobActivity.IMPORTING);

		Connection connection = null;

		try
		{
			connection = MySQLConnectionManager.getConnection();

			importer.importFiles(input.listFiles(), connection);

			logger.info("Import completed. parser=", importer.getClass());

			lastRun = new Date();

			ImportTimeManager.setImportTime(getName(), lastRun);

			if (!input.delete())
			{
				logger.error("Data was imported but couldn't delete fileset with files." + input.getAbsolutePath());
				setStatus(JobStatus.ERROR);
			}
			else
			{
				connection.commit();
				logger.info("Import completed. parser={}", importer);
				setActivity(JobActivity.AWAITING);
			}
		}
		catch (Exception e)
		{
			logger.error("Unhandled exception during import. Input files will be moved to the rejected folder.", e);

			moveAllFilesToRejected();

			try
			{
				connection.rollback();
			}
			catch (Exception ex)
			{
				logger.error("Could not rollback the connection.", ex);
			}

			setStatus(JobStatus.ERROR);
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
		catch (Exception ex)
		{
			logger.error("The files couldn't be moved to the rejected folder.", ex);
			setStatus(JobStatus.ERROR);
		}
	}

	public boolean isRejectedDirEmpty()
	{
		File[] filesInRejectedDir = getRejectedDir().listFiles();

		return filesInRejectedDir != null && filesInRejectedDir.length > 0 && !areMundane(filesInRejectedDir);
	}

	public File getInputDir()
	{
		return new File(rootDir, "input");
	}

	public File getProcessingDir()
	{
		return new File(rootDir, "processing");
	}

	public File getRejectedDir()
	{
		return new File(rootDir, "rejected");
	}

	public String getNextImportExpectedBeforeFormatted()
	{
		return "FAKE DATE"; // DateUtils.toMySQLdate(getNextImportExpectedBefore(lastRun));
	}

	public String getLastImportFormatted()
	{
		Date lastImport = ImportTimeManager.getLastImportTime(getName());

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
}
