package com.trifork.stamdata.importer.jobs.takst.factories;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractFactory
{

	protected final static Logger logger = LoggerFactory.getLogger(AbstractFactory.class);


	static Double toDouble(String s)
	{

		if (s == null || s.trim().length() == 0) return null;
		return new Double(s);
	}


	static Long toLong(String s)
	{

		if (s == null || s.trim().length() == 0) return null;
		return new Long(s);
	}
}
