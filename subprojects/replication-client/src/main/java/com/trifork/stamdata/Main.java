package com.trifork.stamdata;

import static org.slf4j.LoggerFactory.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.*;
import com.trifork.stamdata.modules.ProductionModule;


public class Main
{
	private static final Logger LOGGER = getLogger(Main.class);
	
	public static void main(String[] args)
	{
		Injector injector;
		
		try
		{
			injector = Guice.createInjector(new ProductionModule());
		}
		catch (ConfigurationException e)
		{
			LOGGER.error("Could non")
			return;
		}
	}
}
