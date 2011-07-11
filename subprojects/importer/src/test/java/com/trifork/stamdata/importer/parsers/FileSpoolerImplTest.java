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

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.parsers.exceptions.FileImporterException;


/**
 * SingleFileSpoolerImplTest. Tests that single files are spooled correctly.
 *
 * @author Jan Buchholdt (jbu@trifork.com)
 */
public class FileSpoolerImplTest
{

	private String spoolerDir = System.getProperty("java.io.tmpdir") + "/FileSpoolerImplTest";
	FileSpoolerImpl impl;

	@Before
	public void setUp()
	{

		impl = new FileSpoolerImpl(new FileSpoolerSetup("TestSpooler", spoolerDir, TstFileImporter.class));

		try
		{
			Connection con = MySQLConnectionManager.getAutoCommitConnection();
			con.setCatalog(MySQLConnectionManager.getHousekeepingDBName());
			con.createStatement().executeUpdate("truncate table Import");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@After
	@Before
	public void tearDown() throws Exception
	{

		deleteFile(new File(spoolerDir));
	}

	@Test
	public void testConstructImpl()
	{

		assertNotNull(impl);
		assertTrue(impl.getInputDir().isDirectory());
		assertTrue(impl.getInputDir().canWrite());
		assertTrue(impl.getProcessingDir().isDirectory());
		assertTrue(impl.getProcessingDir().canWrite());
		assertTrue(impl.getRejectedDir().isDirectory());
		assertTrue(impl.getRejectedDir().canWrite());
	}

	@Test
	public void testMoveProcessingFilesBackToInput() throws Exception
	{

		// Setup: Create a file in the processing dir.
		File processingSubdir = new File(impl.getProcessingDir() + "/" + "xxxyyyzzz/");
		File processingFile = new File(processingSubdir.getAbsolutePath() + "/fil.txt");
		File inputFile = new File(impl.getInputDir() + "/fil.txt");
		processingSubdir.mkdirs();
		processingFile.createNewFile();
		assertTrue(processingSubdir.exists());
		assertTrue(processingFile.exists());
		assertFalse(inputFile.exists());

		// it should now be moved back to input dir
		impl.moveProcessingFilesBackToInput();

		// check that the file is gone in processing dir and present in input
		// dir.
		assertFalse(processingFile.exists());
		assertFalse(processingSubdir.exists());
		assertTrue(inputFile.exists());
	}

	@Test
	public void testGetDirSignature() throws Exception
	{

		// Setup 1: Create an empty dir
		File dir = new File(spoolerDir + "/dir1");
		assertTrue(dir.mkdirs());
		long s = FileSpoolerImpl.getDirSignature(dir);

		// Check that same signature is returned
		assertEquals(s, FileSpoolerImpl.getDirSignature(dir));

		// Setup 2: add a file
		File file = new File(dir.getAbsolutePath() + "/file");
		assertTrue(file.createNewFile());
		// Check that a new signature is returned
		assertFalse(s == FileSpoolerImpl.getDirSignature(dir));
		s = FileSpoolerImpl.getDirSignature(dir);
		// Check that same signature is returned
		assertEquals(s, FileSpoolerImpl.getDirSignature(dir));

		// Setup 3: Write a char to the file
		FileWriter fw = new FileWriter(file);
		fw.write('x');
		fw.close();
		// Check that a new signature is returned
		assertFalse(s == FileSpoolerImpl.getDirSignature(dir));
		s = FileSpoolerImpl.getDirSignature(dir);
		// Check that same signature is returned
		assertEquals(s, FileSpoolerImpl.getDirSignature(dir));
	}

	@Test
	public void testIsRejectedDirsEmpty() throws Exception
	{

		assertTrue(impl.isRejectedDirEmpty());
		File f = new File(impl.getRejectedDir().getAbsolutePath() + "/file");
		f.createNewFile();
		assertFalse(impl.isRejectedDirEmpty());
	}

	@Test
	public void testPollNoFiles() throws Exception
	{

		impl.execute();
		assertEquals(impl.getStatus(), FileSpoolerImpl.Status.RUNNING);
		assertEquals(impl.getActivity(), FileSpoolerImpl.Activity.AWAITING);
		assertNull(impl.getMessage());
	}

	@Test
	public void testPollInputFile() throws Exception
	{

		File f = new File(impl.getInputDir() + "/f");
		assertTrue(f.createNewFile());
		impl.execute();
		assertEquals(impl.getStatus(), FileSpoolerImpl.Status.RUNNING);
		assertEquals(impl.getActivity(), FileSpoolerImpl.Activity.STABILIZING);
		assertNull(impl.getMessage());
	}

	// @Test
	public void testImportSucess() throws Exception
	{
		// Create an input file
		File f = new File(impl.getInputDir() + "/f");
		assertTrue(f.createNewFile());
		// "Cheat" and make it look as if the file is stable now, so we dont
		// have to wait
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MILLISECOND, -1000);
		impl.stabilizationPeriodEnd = cal;
		impl.inputdirSignature = FileSpoolerImpl.getDirSignature(impl.getInputDir());
		// Make an importer that always succeeds
		TstFileImporter importer = new TstFileImporter()
		{

			@Override
			public boolean checkRequiredFiles(List<File> files)
			{

				return true;
			}
		};
		impl.importer = importer;
		Date beforeCall = new Date();

		// Polling now should trigger import
		impl.execute();

		// Check that status/activity is set correctly after import
		assertEquals(impl.getStatus(), FileSpoolerImpl.Status.RUNNING);
		assertEquals(impl.getActivity(), FileSpoolerImpl.Activity.AWAITING);
		// Check that no error message is set
		assertNull(impl.getMessage());
		// Check that the importer was called once
		assertEquals(1, importer.importFileCalled);

		// Check that importtime was set in mysql and that we can get it out
		Date importTime = ImportTimeManager.getLastImportTime(impl.getSetup().getName());
		assertNotNull(importTime);
		// Check that importtime was set to the timestamp of the execution. I.e.
		// before now
		assertTrue(importTime.before(new Date()));

		// Check that importtime was set to the timestamp of the execution. I.e.
		// after before the call.
		// Due to MySQL not having sub-second presicion and the fact that it
		// rounds down, a second is added before the comparision

		DateTime date = new DateTime(importTime);
		date = date.plusSeconds(1);

		assertTrue(date.toDate().after(beforeCall));

		// Check that the input files are deleted, as they should be after
		// succesful processing
		assertTrue(impl.getInputDir().listFiles().length == 0);
		assertTrue(impl.getProcessingDir().listFiles().length == 0);
		assertTrue(impl.getRejectedDir().listFiles().length == 0);
		assertFalse(f.exists());
	}

	@Test
	public void testImportFailure() throws Exception
	{

		// create a file
		File f = new File(impl.getInputDir() + "/f");
		assertTrue(f.createNewFile());
		// "Cheat" and make it look as if it is stable
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MILLISECOND, -1000);
		impl.stabilizationPeriodEnd = cal;
		impl.inputdirSignature = FileSpoolerImpl.getDirSignature(impl.getInputDir());

		// Make an importer that always fails
		TstFileImporter importer = new TstFileImporter()
		{

			@Override
			public boolean checkRequiredFiles(List<File> files)
			{

				return true;
			}

			@Override
			public void run(List<File> files) throws FileImporterException
			{

				throw new FileImporterException("errormsg");
			}
		};
		impl.importer = importer;

		// Do import and check that the failure is handled correctly
		impl.execute();
		// there should be created a new dir in rejected with the input file and
		// a RejectReason file
		assertEquals(2, FileUtils.listFiles(impl.getRejectedDir(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).size());
		assertEquals(1, FileUtils.listFiles(impl.getRejectedDir(), new NameFileFilter("f"), TrueFileFilter.INSTANCE).size());
		// and a rejectreason
		File rejReason = (File) FileUtils.listFiles(impl.getRejectedDir(), new NameFileFilter("RejectReason"), TrueFileFilter.INSTANCE).iterator().next();
		assertTrue(FileUtils.readFileToString(rejReason).contains("errormsg"));
		assertFalse(f.exists());
		assertEquals(FileSpoolerImpl.Status.ERROR, impl.getStatus());
		assertEquals("errormsg", impl.getMessage());
		assertNull(ImportTimeManager.getLastImportTime(impl.getSetup().getName()));
		// No files should be present in input or processing dirs
		assertEquals(0, FileUtils.listFiles(impl.getInputDir(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).size());
		assertEquals(0, FileUtils.listFiles(impl.getProcessingDir(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).size());
	}

	/**
	 * This function will recursively delete directories and files.
	 *
	 * @param path File or Directory to be deleted
	 * @return true indicates success.
	 */
	public static boolean deleteFile(File path)
	{

		if (path.exists())
		{
			if (path.isDirectory())
			{
				File[] files = path.listFiles();
				for (int i = 0; i < files.length; i++)
				{
					if (files[i].isDirectory())
					{
						deleteFile(files[i]);
					}
					else
					{
						files[i].delete();
					}
				}
			}
		}
		return (path.delete());
	}
}


class TstFileImporter implements FileImporter
{

	int importFileCalled = 0;

	@Override
	public void run(List<File> files) throws FileImporterException
	{

		System.out.println("TESTFILEIMPORTER IMPORTING!");
		importFileCalled++;
	}

	@Override
	public boolean checkRequiredFiles(List<File> files)
	{

		return false;
	}
};
