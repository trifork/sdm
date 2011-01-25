package com.trifork.sdm.importer.importers.sks;


import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;

import com.trifork.sdm.importer.TestHelper;
import com.trifork.stamdata.importer.jobs.sks.SksImporter;


public class SksImporterTest
{
	public static final File SHAK_COMPLETE = TestHelper.getFile("testdata/sks/SHAKCOMPLETE.TXT");

	// This field does not use the TestHelper because the file does not exist,
	// and the test helper would fail.
	public static final File wrong = new File("testdata/sks/SHAKCOMPLETE.XML");


	@Test
	public void testAreRequiredInputFilesPresent()
	{
		/*
		SksImporter importer = new SksImporter();

		ArrayList<File> files = new ArrayList<File>();

		assertFalse(importer.areRequiredInputFilesPresent(files));
		files.add(SHAK_COMPLETE);
		assertTrue(importer.areRequiredInputFilesPresent(files));

		files.remove(SHAK_COMPLETE);
		files.add(wrong);
		assertFalse(importer.areRequiredInputFilesPresent(files));
		*/
	}
}
