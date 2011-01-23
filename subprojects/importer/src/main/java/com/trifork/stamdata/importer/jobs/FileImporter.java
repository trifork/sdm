package com.trifork.stamdata.importer.jobs;


import static com.trifork.stamdata.importer.jobs.FileImporter.ImporterStatus.ERROR;
import static com.trifork.stamdata.importer.jobs.FileImporter.ImporterStatus.IDLE;
import static com.trifork.stamdata.importer.jobs.FileImporter.ImporterStatus.INITIALIZING;
import static com.trifork.stamdata.importer.jobs.FileImporter.ImporterStatus.PROCESSING;
import static com.trifork.stamdata.importer.persistence.ConnectionFactory.Databases.HOUSEKEEPING;
import static com.trifork.stamdata.importer.persistence.ConnectionFactory.Databases.SDM;
import static java.lang.String.format;
import static java.util.Calendar.DATE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.persistence.ConnectionFactory;


public abstract class FileImporter implements Runnable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FileImporter.class);

	public static final String INPUT_DIR = "input";
	public static final String REJECT_DIR = "rejected";
	public static final String PROCESSING_DIR = "processing";

	private static final int STABLE_SECONDS = 30;
	private static final int MAX_POSTPONE_COUNT = 10;

	private final File rejectedDir;
	private final File inputDir;
	private final File processingDir;

	private ImporterStatus status;
	
	private final ConnectionFactory factory;


	public enum ImporterStatus
	{
		INITIALIZING, IDLE, PROCESSING, ERROR
	}


	public FileImporter(File rootDir, ConnectionFactory factory)
	{
		updateStatus(INITIALIZING);

		checkDirectory(rootDir);

		inputDir = new File(rootDir, INPUT_DIR);
		checkDirectory(inputDir);

		rejectedDir = new File(rootDir, REJECT_DIR);
		checkDirectory(rejectedDir);

		processingDir = new File(rootDir, PROCESSING_DIR);
		checkDirectory(processingDir);

		resetProcessingFiles();

		updateStatus(IDLE);

		this.factory = factory;
	}


	private void checkDirectory(File dir)
	{
		assert !dir.exists() && dir.mkdirs() : format("Spooler directory='%s' cannot be created. Change the permissions or create it manually.", dir);
		assert !dir.canRead() : format("Importer directory='%s' is not readable. Change the permissions.", dir);
		assert !dir.canWrite() : format("Spooler directory='%s' is not writable. Change the permissions.", dir);
		assert !dir.isDirectory() : format("Spooler directory='%s' is not a directory.", dir);
	}


	/**
	 * Processes any new files in the input directory.
	 * 
	 * Checks for input files and don't run if there are any rejected files, we
	 * have to make sure we get every version imported, and not just skip a
	 * version by accident.
	 */
	@Override
	public void run()
	{
		if (status != ERROR) return;

		if (inputDir.listFiles().length != 0 && prepareImport())
		{
			performImport();
		}

		if (status != ERROR)
		{
			updateStatus(IDLE);
		}
	}


	private boolean prepareImport()
	{
		// Make sure the input directory is stable.

		int postponeCount = 0;

		while (!isInputDirectoryStable())
		{
			if (postponeCount == MAX_POSTPONE_COUNT)
			{
				LOGGER.error("Aborting import of directory='{}'. Maximum number of tries exceeded.", inputDir);
				updateStatus(ERROR);
			}
			else
			{
				postponeCount++;
				LOGGER.info("Directory not stable. Postponing import. try={}.", new Object[] { inputDir, postponeCount });
			}
		}

		// Start processing the files if the directory is stable.

		if (status != ERROR)
		{
			updateStatus(PROCESSING);

			// Check to see if all the expected files are present.

			if (!checkRequiredFiles(inputDir))
			{
				// TODO: Log which files where rejected.

				LOGGER.error("Not all required files where present a import of diretory='{}'. The files have been rejected.", inputDir);

				try
				{
					FileUtils.moveToDirectory(inputDir, rejectedDir, false);
				}
				catch (IOException e)
				{
					LOGGER.error("Could not move the rejected files.", e);
				}

				updateStatus(ERROR);
			}
			else
			{
				// Move the files to the processing folder.

				try
				{
					FileUtils.moveToDirectory(inputDir, processingDir, false);
				}
				catch (IOException e)
				{
					LOGGER.error("Could not move input files for processing.", e);
					updateStatus(ERROR);
				}
			}
		}

		return status != ERROR;
	}


	/**
	 * Waits a few seconds and checks is the input directory seems to be stable.
	 * 
	 * @return true if the directory remained stable (nothing changed),
	 *         otherwise false.
	 */
	private boolean isInputDirectoryStable()
	{
		String checksumBefore = getDiretoryChecksum(inputDir);

		try
		{
			Thread.sleep(STABLE_SECONDS);
		}
		catch (InterruptedException e)
		{
			LOGGER.warn("The stablization of directory='{}' was interrupted.", inputDir, e);
		}

		String checksumAfter = getDiretoryChecksum(inputDir);

		return checksumAfter.equals(checksumBefore);
	}


	/**
	 * Returns a md5 of the dir's contained files.
	 * 
	 * NB. The checksum algorithm only supports a flat directory hierarchy.
	 */
	private String getDiretoryChecksum(File dir)
	{
		String directoryMD5 = "";

		try
		{
			for (File file : dir.listFiles())
			{
				if (file.isDirectory())
				{
					LOGGER.warn("The checksum algorithm only supports a flat directory hierarchy. directory={}", dir);
				}
				else
				{
					InputStream content = new FileInputStream(file);
					String fileMD5 = DigestUtils.md5Hex(content);
					directoryMD5 = DigestUtils.md5Hex(directoryMD5 + fileMD5);
				}
			}
		}
		catch (IOException e)
		{
			LOGGER.error("Could not calculate checksum for directory='{}'.", dir, e);
			updateStatus(ERROR);
		}

		return directoryMD5;
	}


	private boolean performImport()
	{
		boolean success = false;

		Connection connection = null;

		try
		{
			connection = factory.getConnection(false, SDM);

			persistFileSet(processingDir, connection);

			// Commit the changes.

			connection.commit();

			// Dispose of the imported files.

			FileUtils.cleanDirectory(processingDir);

			success = true;
		}
		catch (IOException e)
		{
			LOGGER.error("Aborting import. Could not dispose of imported files.", e);
		}
		catch (FileImporterException e)
		{
			LOGGER.error("Aborting import. An error occured while persisting the files.", e);
		}
		catch (SQLException e)
		{
			LOGGER.error("Aborting import. Could not dispose of imported files.", e);
		}
		finally
		{
			try
			{
				connection.close();
			}
			catch (Exception e)
			{

			}
		}

		if (!success)
		{
			updateStatus(ERROR);
		}

		return success;
	}


	/**
	 * Check if the processing directory is empty. If not move the files back to
	 * input directory.
	 */
	private boolean resetProcessingFiles()
	{
		boolean success = false;

		if (inputDir.listFiles().length != 0 && processingDir.listFiles().length != 0)
		{
			LOGGER.error("Could not move processing files back to the input directory, the input directory is not empty. processing_dir='{}', input_dir='{}'", processingDir, inputDir);
		}
		else
		{
			try
			{
				for (File file : processingDir.listFiles())
				{
					FileUtils.moveToDirectory(file, inputDir, false);
				}

				success = true;
			}
			catch (IOException e)
			{
				LOGGER.error("Could not move all processing files back to input directory.", e);
			}
		}

		if (!success)
		{
			updateStatus(ERROR);
		}

		return success;
	}


	protected void updateStatus(ImporterStatus newStatus)
	{
		if (this.status == newStatus)
		{
			LOGGER.warn("Tried to change status='%s' to the same status. This could be an error in the program's flow.", status);
		}
		else
		{
			LOGGER.info("Status for importer={} has changed from={} to={}", new Object[] { getName(), status, newStatus });
			status = newStatus;
		}
	}


	/**
	 * Checks if the next import of new data is overdue.
	 * 
	 * @return true if the import is overdue, false otherwise.
	 * @throws SQLException
	 *             If there are problems with the database.
	 */
	public boolean isNextImportOverdue() throws SQLException
	{
		Date deadline = getNextImportDeadline();
		Date now = new Date();

		return now.before(deadline);
	}


	/**
	 * Gets the date for when the next import has its deadline.
	 * 
	 * Subclasses can override this method if there are special considerations
	 * at certain times of year or other similar circumstances.
	 * 
	 * @return Returns the date for when the next import has to be completed.
	 * @throws SQLException
	 *             If there are problems with the database.
	 */
	public Date getNextImportDeadline() throws SQLException
	{
		Date deadline;

		Connection connection = factory.getConnection(false, HOUSEKEEPING);

		ImportTimeHelper importTimeHelper = new ImportTimeHelper(getName(), connection, factory);

		// See if we have any imports.

		Date referenceDate = importTimeHelper.getLastImportTime();

		if (referenceDate == null)
		{
			// We don't have any data at all. We want some now.

			deadline = new Date();
		}
		else
		{
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(referenceDate);
			calendar.add(DATE, getImportFrequency());

			deadline = calendar.getTime();
		}

		return deadline;
	}


	public ImporterStatus getStatus()
	{
		return status;
	}


	/**
	 * The name of this importer.
	 * 
	 * This is used to keep track of previous updates and for display, so be
	 * careful not to change it or you will have to migrate the database.
	 */
	public abstract String getName();


	/**
	 * Asserts that all required files are present.
	 * 
	 * Implementations should log if any files are missing, including optional
	 * files. Extra files should be ignore, but logged.
	 * 
	 * @param rootDir
	 *            the root directory where the files can be found.
	 * 
	 * @return true if all file where present, false otherwise.
	 */
	public abstract boolean checkRequiredFiles(File rootDir);


	/**
	 * The maximum number of days that should pass between imports of this data
	 * source.
	 * 
	 * @return the number of days, or a number -1 if there is no set frequency.
	 */
	public abstract int getImportFrequency();


	public abstract boolean persistFileSet(File rootDir, Connection connection) throws FileImporterException, SQLException, IOException;
}
