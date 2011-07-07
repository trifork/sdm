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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.parsers.exceptions.FileImporterException;


/**
 * SingleFileSpoolerImplTest. Tests that single files are spooled correctly.
 * 
 * @author Jan Buchholdt (jbu@trifork.com)
 */
public class FileSpoolerImplTest
{
	@Rule
	public static TemporaryFolder testFolder = new TemporaryFolder();

	private FileParserJob worker;
	private Connection con;

	@Before
	public void setUp() throws SQLException
	{
		worker = new FileParserJob(new FileSpoolerSetup("TestSpooler", testFolder.getRoot().getAbsolutePath(), TestFileImporter.class));

		con = MySQLConnectionManager.getConnection();

		con.createStatement().executeUpdate("TRUNCATE TABLE Import");
	}

	@Test
	public void testConstructImpl()
	{
		assertNotNull(worker);
		assertTrue(worker.getInputDir().isDirectory());
		assertTrue(worker.getInputDir().canWrite());
		assertTrue(worker.getProcessingDir().isDirectory());
		assertTrue(worker.getProcessingDir().canWrite());
		assertTrue(worker.getRejectedDir().isDirectory());
		assertTrue(worker.getRejectedDir().canWrite());
	}

	@Test
	public void testMoveProcessingFilesBackToInput() throws Exception
	{
		// Setup: Create a file in the processing dir.

		File processingSubdir = new File(worker.getProcessingDir() + "/xxxyyyzzz/");
		File processingFile = new File(processingSubdir.getAbsolutePath() + "/fil.txt");
		File inputFile = new File(worker.getInputDir() + "/fil.txt");

		processingSubdir.mkdirs();
		processingFile.createNewFile();

		assertTrue(processingSubdir.exists());
		assertTrue(processingFile.exists());
		assertFalse(inputFile.exists());

		// It should now be moved back to input dir.

		worker.moveProcessingFilesBackToInput();

		// Check that the file is gone in processing dir and present in input
		// dir.

		assertFalse(processingFile.exists());
		assertFalse(processingSubdir.exists());
		assertTrue(inputFile.exists());
	}

	@Test
	public void testGetDirSignature() throws Exception
	{
		// Setup 1: Create an empty dir.

		File dir = new File(testFolder.newFolder("dir1").getAbsolutePath());
		assertTrue(dir.mkdirs());
		long s = FileParserJob.getDirSignature(dir);

		// Check that same signature is returned.

		assertEquals(s, FileParserJob.getDirSignature(dir));

		// Setup 2: add a file.

		File file = new File(dir.getAbsolutePath() + "/file");
		assertTrue(file.createNewFile());

		// Check that a new signature is returned.

		assertFalse(s == FileParserJob.getDirSignature(dir));
		s = FileParserJob.getDirSignature(dir);

		// Check that same signature is returned.

		assertEquals(s, FileParserJob.getDirSignature(dir));

		// Setup 3: Write a char to the file.

		FileWriter fw = new FileWriter(file);
		fw.write('x');
		fw.close();

		// Check that a new signature is returned.

		assertFalse(s == FileParserJob.getDirSignature(dir));
		s = FileParserJob.getDirSignature(dir);

		// Check that same signature is returned.

		assertEquals(s, FileParserJob.getDirSignature(dir));
	}

	@Test
	public void testIsRejectedDirsEmpty() throws Exception
	{
		assertTrue(worker.isRejectedDirEmpty());
		File f = new File(worker.getRejectedDir().getAbsolutePath() + "/file");
		f.createNewFile();
		assertFalse(worker.isRejectedDirEmpty());
	}

	@Test
	public void testPollNoFiles() throws Exception
	{
		worker.run();
		assertEquals(worker.getState(), FileSpoolerImpl.FileParserJob.OK);
		assertEquals(worker.getActivity(), FileSpoolerImpl.FileParserJob.AWAITING);
		assertNull(worker.getMessage());
	}

	@Test
	public void testPollInputFile() throws Exception
	{
		File f = new File(worker.getInputDir() + "/f");
		assertTrue(f.createNewFile());
		worker.run();
		assertEquals(worker.getState(), FileSpoolerImpl.FileParserJob.OK);
		assertEquals(worker.getActivity(), FileSpoolerImpl.FileParserJob.STABILIZING);
		assertNull(worker.getMessage());
	}

	@Test
	public void testImportSucess() throws Exception
	{
		// Create an input file.

		File f = new File(worker.getInputDir() + "/f");
		assertTrue(f.createNewFile());

		// "Cheat" and make it look as if the file is stable now, so we dont
		// have to wait.

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MILLISECOND, -1000);
		worker.stabilizationPeriodEnd = cal;
		worker.inputdirSignature = FileParserJob.getDirSignature(worker.getInputDir());

		// Make an importer that always succeeds.

		TestFileImporter importer = new TestFileImporter()
		{
			@Override
			public boolean checkRequiredFiles(List<File> files)
			{
				return true;
			}
		};

		worker.importer = importer;
		Date beforeCall = new Date();

		// Polling now should trigger import.

		worker.run();

		// Check that status/activity is set correctly after import

		assertEquals(worker.getState(), FileSpoolerImpl.FileParserJob.OK);
		assertEquals(worker.getActivity(), FileSpoolerImpl.FileParserJob.AWAITING);

		// Check that no error message is set

		assertNull(worker.getMessage());

		// Check that the importer was called once

		assertEquals(1, importer.importFileCalled);

		// Check that importtime was set in mysql and that we can get it out

		Date importTime = ImportTimeManager.getLastImportTime(worker.getSetup().getName());
		assertNotNull(importTime);

		// Check that importtime was set to the timestamp of the execution. I.e.
		// before now.

		assertTrue(importTime.before(new Date()));

		// Check that importtime was set to the timestamp of the execution. I.e.
		// after before the call.
		// Due to MySQL not having sub-second presicion and the fact that it
		// rounds down, a second is added before the comparision.

		DateTime date = new DateTime(importTime);
		date = date.plusSeconds(1);

		assertTrue(date.toDate().after(beforeCall));

		// Check that the input files are deleted, as they should be after
		// succesful processing.

		assertTrue(worker.getInputDir().listFiles().length == 0);
		assertTrue(worker.getProcessingDir().listFiles().length == 0);
		assertTrue(worker.getRejectedDir().listFiles().length == 0);
		assertFalse(f.exists());
	}

	@Test
	public void testImportFailure() throws Exception
	{
		// create a file

		File f = new File(worker.getInputDir() + "/f");
		assertTrue(f.createNewFile());

		// "Cheat" and make it look as if it is stable

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MILLISECOND, -1000);
		worker.stabilizationPeriodEnd = cal;
		worker.inputdirSignature = FileParserJob.getDirSignature(worker.getInputDir());

		// Make an importer that always fails

		TestFileImporter importer = new TestFileImporter()
		{
			@Override
			public boolean checkRequiredFiles(List<File> files)
			{
				return true;
			}

			@Override
			public void run(List<File> files, Connection con) throws FileImporterException
			{
				throw new FileImporterException("errormsg");
			}
		};

		worker.importer = importer;

		// Do import and check that the failure is handled correctly.

		worker.run();

		// there should be created a new dir in rejected with the input file and
		// a RejectReason file.

		assertEquals(2, FileUtils.listFiles(worker.getRejectedDir(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).size());
		assertEquals(1, FileUtils.listFiles(worker.getRejectedDir(), new NameFileFilter("f"), TrueFileFilter.INSTANCE).size());

		assertEquals(FileSpoolerImpl.FileParserJob.ERROR, worker.getState());

		assertNull(ImportTimeManager.getLastImportTime(worker.getSetup().getName()));

		// No files should be present in input or processing dirs

		assertEquals(0, FileUtils.listFiles(worker.getInputDir(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).size());
		assertEquals(0, FileUtils.listFiles(worker.getProcessingDir(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).size());
	}
}


class TestFileImporter implements Parser
{
	int importFileCalled = 0;

	@Override
	public void run(List<File> files, Connection con) throws Exception
	{
		importFileCalled++;
	}

	@Override
	public boolean checkRequiredFiles(List<File> files)
	{

		return false;
	}

	@Override
	public Date getNextImportExpectedBefore(Date lastImport)
	{
		return new DateTime().plusHours(1).toDate();
	}
};
