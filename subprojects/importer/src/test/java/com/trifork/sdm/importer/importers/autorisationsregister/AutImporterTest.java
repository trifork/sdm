package com.trifork.sdm.importer.importers.autorisationsregister;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.trifork.sdm.importer.TestHelper;
import com.trifork.stamdata.importer.jobs.autorisationsregisteret.AutorisationImporter;
import com.trifork.stamdata.importer.jobs.autorisationsregisteret.AutorisationsregisterParser;


public class AutImporterTest
{
	public static File validFile = TestHelper.getFile("testdata/aut/valid/20090915AutDK.csv");
	private AutorisationImporter importer;


	@Before
	public void Setup()
	{
		//importer = new AutorisationImporter();
	}


	@Test
	public void testAreRequiredInputFilesPresent() throws IOException
	{
		/*
		List<File> files = new ArrayList<File>();

		// empty set

		assertFalse(importer.areRequiredInputFilesPresent(files));
		files.add(new File("blabla.nowayamigo"));

		// wrong file name and empty file.

		assertFalse(importer.areRequiredInputFilesPresent(files));
		files.add(validFile);

		// one bad and one good file.

		assertFalse(importer.areRequiredInputFilesPresent(files));
		files = new ArrayList<File>();
		files.add(validFile);

		// one good file

		assertTrue(importer.areRequiredInputFilesPresent(files));
		files.add(validFile);

		// two good files

		assertTrue(importer.areRequiredInputFilesPresent(files));
		*/
	}


	AutorisationsregisterParser parser = new AutorisationsregisterParser();


	@Test
	public void testImport() throws Exception
	{
		List<File> files = new ArrayList<File>();
		//files.add(validFile);
		//MySQLTemporalDao daoMock = mock(MySQLTemporalDao.class);
		//importer.doImport(files, daoMock);
		//verify(daoMock).persistCompleteDataset(any(CompleteDataset.class));
	}


	@Test
	public void testGetDateFromFileName()
	{
		//AutorisationImporter importer = new AutorisationImporter();
		//Date date = importer.getDateFromInputFileName("19761110sgfdgfg");
		//assertEquals("19761110", new SimpleDateFormat("yyyyMMdd").format(date));
	}
}
