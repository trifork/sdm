/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */


package com.trifork.stamdata.importer;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletContextEvent;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.ComponentMonitor;
import com.trifork.stamdata.MonitoringModule;
import com.trifork.stamdata.importer.jobs.FileParser;
import com.trifork.stamdata.importer.jobs.FileParserJob;
import com.trifork.stamdata.importer.jobs.Job;
import com.trifork.stamdata.importer.jobs.JobManager;
import com.trifork.stamdata.importer.webinterface.DataManagerComponentMonitor;
import com.trifork.stamdata.importer.webinterface.DatabaseStatus;
import com.trifork.stamdata.importer.webinterface.GUIServlet;


public class ApplicationContextListener extends GuiceServletContextListener
{
	private static final Logger logger = LoggerFactory.getLogger(ApplicationContextListener.class);

	public static final String BUILDIN_CONFIG_FILE = "config.properties";
	public static final String DEPLOYMENT_CONFIG_FILE = "stamdata-data-manager.properties";

	private Injector injector;

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent)
	{
		// We have to call the super method to allow Guice to initialize
		// itself.
		
		super.contextInitialized(servletContextEvent);

		// Start the jobs.

		try
		{
			injector.getInstance(JobManager.class).start();
		}
		catch (Exception e)
		{
			throw new RuntimeException("Could not start the job manager.", e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent)
	{
		try
		{
			injector.getInstance(JobManager.class).stop();
		}
		catch (Exception e)
		{
			// We'll just log the error here and allow the rest of the
			// system to shut down.

			logger.error("Could not start the job manager.", e);
		}

		super.contextDestroyed(servletContextEvent);
	}

	@Override
	protected Injector getInjector()
	{
		if (injector != null) return injector;

		final CompositeConfiguration config = loadConfiguration();

		// Parse the properties from the configuration files.

		final List<Job> jobs = Lists.newArrayList();

		jobs.addAll(getConfiguredParserJobs(config));

		injector = Guice.createInjector(new ServletModule()
		{
			@Override
			protected void configureServlets()
			{
				// Bind the configured jobs.

				bind(new TypeLiteral<List<Job>>() {}).toInstance(jobs);

				// Serve the status servlet.

				bind(ComponentMonitor.class).to(DataManagerComponentMonitor.class);
				install(new MonitoringModule());

				serve("/").with(GUIServlet.class);

				// Bind the required dependencies.

				bind(JobManager.class).in(Scopes.SINGLETON);
				bind(DatabaseStatus.class).in(Scopes.SINGLETON);
			}
		});

		return injector;
	}

	private List<Job> getConfiguredParserJobs(final CompositeConfiguration config)
	{
		// File parser config format:
		//
		// parser : <File parser's canonical class name>; <minimum import
		// frequency in days>

		List<Job> fileParsers = Lists.newArrayList();

		final File rootDir = new File(config.getString("rootDir"));

		for (String jobConfiguration : config.getStringArray("parser"))
		{
			String[] values = jobConfiguration.split(";");

			if (values.length != 2)
			{
				throw new RuntimeException("All parsers must be configured with an expected import frequency. " + jobConfiguration);
			}

			String className = values[0].trim();
			int minimumImportFrequency = Integer.parseInt(values[1].trim());

			try
			{
				Class<? extends FileParser> type = Class.forName(className).asSubclass(FileParser.class);
				FileParser parser = type.newInstance();

				// Wrap the parser in a file parser job.

				fileParsers.add(new FileParserJob(rootDir, parser, minimumImportFrequency));
			}
			catch (Exception e)
			{
				throw new RuntimeException("An error occurred while loading job.", e);
			}
		}

		return fileParsers;
	}

	private CompositeConfiguration loadConfiguration()
	{
		try
		{
			// Override the build-in configuration 'config.properties' with
			// the one found in 'stamdata-data-manager.properties'.
			//
			// Composite configurations always return the first version of a
			// property
			// that is added to it. Therefore we load the defaults last.

			CompositeConfiguration configuration = new CompositeConfiguration();

			URL deploymentConfigurationFile = getClass().getClassLoader().getResource(DEPLOYMENT_CONFIG_FILE);

			if (deploymentConfigurationFile != null)
			{
				configuration.addConfiguration(new PropertiesConfiguration(deploymentConfigurationFile));
				logger.info("Configuration file '{}' loaded.", deploymentConfigurationFile);
			}
			else
			{
				logger.warn("Configuration file '{}' could not be found. Using default configuration.", DEPLOYMENT_CONFIG_FILE);
			}

			// Add any missing properties from the defaults.

			configuration.addConfiguration(new PropertiesConfiguration(BUILDIN_CONFIG_FILE));

			return configuration;

		}
		catch (Exception e)
		{
			throw new RuntimeException("The application could not be started do to a configuration error.", e);
		}
	}
}
