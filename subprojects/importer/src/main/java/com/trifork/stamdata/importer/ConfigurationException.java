package com.trifork.stamdata.importer;


public class ConfigurationException extends Exception
{
	private static final long serialVersionUID = -3605101185805773976L;


	public ConfigurationException(String message)
	{
		super(message);
	}


	public ConfigurationException(String message, Throwable e)
	{
		super(message, e);
	}
}
