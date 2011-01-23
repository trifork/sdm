package com.trifork.stamdata.importer.jobs;




public class FilePersistException extends FileImporterException
{
	private static final long serialVersionUID = 1L;


	public FilePersistException(String message)
	{
		super(message);
	}


	public FilePersistException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
