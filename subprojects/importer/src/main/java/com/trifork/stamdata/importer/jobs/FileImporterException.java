package com.trifork.stamdata.importer.jobs;


public class FileImporterException extends Exception
{
	private static final long serialVersionUID = 2852228444994730597L;


	public FileImporterException(String message, Throwable cause)
	{
		super(message, cause);
	}


	public FileImporterException(String message)
	{
		super(message);
	}
}
