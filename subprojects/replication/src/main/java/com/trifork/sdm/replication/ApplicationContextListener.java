package com.trifork.sdm.replication;

import static org.slf4j.LoggerFactory.*;

import org.slf4j.Logger;

import com.google.inject.*;
import com.google.inject.servlet.GuiceServletContextListener;


public class ApplicationContextListener extends GuiceServletContextListener
{
	private static final Logger LOG = getLogger(ApplicationContextListener.class);


	@Override
	protected Injector getInjector()
	{
		Injector injector = null;

		try
		{
			LOG.info("Initializing SDM replication service.");

			injector = Guice.createInjector(new ProductionModule());

			LOG.info("Service initialized.");
		}
		catch (Throwable t)
		{
			LOG.error("Initialization failed, do to configuration error.", t);
		}

		return injector;
	}
}
