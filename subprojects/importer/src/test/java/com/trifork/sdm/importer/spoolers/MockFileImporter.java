package com.trifork.sdm.importer.spoolers;


import java.io.File;
import java.util.List;

import com.trifork.stamdata.importer.jobs.FileImporterException;


public class MockFileImporter
{
	int importFileCalled = 0;


	public void importFiles(List<File> files) throws FileImporterException
	{
		System.out.println("TESTFILEIMPORTER IMPORTING!");
		importFileCalled++;
	}


	public boolean areRequiredInputFilesPresent(List<File> files)
	{

		return false;
	}
};
