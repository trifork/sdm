package com.trifork.stamdata.importer.jobs.sikrede;

import java.io.File;

import com.trifork.stamdata.importer.jobs.FileParser;
import com.trifork.stamdata.importer.persistence.AuditingPersister;
import com.trifork.stamdata.importer.persistence.Dataset;

public class SikredeParser implements FileParser
{
	@Override
	public String getIdentifier()
	{
		return "sikrede";
	}
	
	@Override
	public String getHumanName()
	{
		return "\"Sikrede\" Parser";
	}

	@Override
	public boolean ensureRequiredFileArePresent(File[] input)
	{
		// 1. CHECK THAT ALL FILES ARE PRESENT
		
		// TODO Auto-generated method stub.
		
		return true;
	}

	@Override
	public void importFiles(File[] input, AuditingPersister persister) throws Exception
	{
		// 1. CHECK VERSIONS
		//
		// The first time a dataset it imported we have no previous
		// version and just accept any version.
		// 
		// For subsequent versions we make sure the files contain the correct version.
		// The versions should be in sequence.
		
		// 2. PARSE THE DATA
		//
		// Put each data type in to a dataset and let the persister
		// store theme in the database.
		//
		// Use @Output and @Id annotations on your domain classes to
		// tell the persister what to do.
		
		Dataset<?> s;
	}
}
