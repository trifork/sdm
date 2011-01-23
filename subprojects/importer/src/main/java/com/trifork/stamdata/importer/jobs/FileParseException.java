package com.trifork.stamdata.importer.jobs;


public class FileParseException extends FileImporterException
{

	private static final long serialVersionUID = 1L;


	public FileParseException(String message, Throwable cause)
	{

		super(message, cause);
	}


	public FileParseException(String message)
	{

		super(message);
	}
}
