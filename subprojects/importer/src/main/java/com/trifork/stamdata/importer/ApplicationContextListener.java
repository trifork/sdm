// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
//
// Contributor(s): Contributors are attributed in the source code
// where applicable.
//
// The Original Code is "Stamdata".
//
// The Initial Developer of the Original Code is Trifork Public A/S.
//
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
//
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.importer;

import java.util.List;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.importer.parsers.Job;
import com.trifork.stamdata.importer.parsers.JobManager;
import com.trifork.stamdata.importer.webinterface.DatabaseStatus;
import com.trifork.stamdata.importer.webinterface.ImporterServlet;


public class ApplicationContextListener extends GuiceServletContextListener
{
	private static final Logger logger = LoggerFactory.getLogger(ApplicationContextListener.class);

	@Override
	protected Injector getInjector()
	{
		final CompositeConfiguration config = loadConfiguration();
		
		// Parse the properties from the configuration files.

		final String rootDir = config.getString("rootDir");

		final List<Job> jobs = Lists.newArrayList();

		for (String jobConfiguration : config.getStringArray("job"))
		{
			String[] values = jobConfiguration.split(";");

			String className = values[0].trim();
			String schedule = values.length == 2 ? values[1].trim() : "* * * * *";
			
			try
			{
				Class<? extends Job> type = Class.forName(className).asSubclass(Job.class);
				Job job = type.newInstance();
				job.setSchedule(schedule);
				jobs.add(job);
			}
			catch (Exception e)
			{
				throw new RuntimeException("An error occurred while loading job. " + className, e);
			}
		}

		// The jobs are specified with the format:

		return Guice.createInjector(new ServletModule()
		{
			@Override
			protected void configureServlets()
			{
				// Bind the configured jobs.

				bind(new TypeLiteral<List<Job>>()
				{}).toInstance(jobs);

				// Serve the status servlet.

				serve("/status").with(ImporterServlet.class);

				bind(ProjectInfo.class).in(Scopes.SINGLETON);
				bind(JobManager.class).in(Scopes.SINGLETON);
				bind(DatabaseStatus.class).in(Scopes.SINGLETON);

				bindConstant().annotatedWith(Names.named("RootDir")).to(rootDir);
			}
		});
	}

	private CompositeConfiguration loadConfiguration()
	{
		try
		{
			logger.info("Loading configuration.");

			// Override the default configuration 'config.properties' with
			// the one found in 'stamdata-importer.properties'.
			//
			// Composite configurations always return the first version of a
			// property
			// that is added to it. Therefore we load the defaults last.

			CompositeConfiguration configuration = new CompositeConfiguration();

			final String DEPLOYMENT_CONFIG_FILENAME = "stamdata-importer.properties";

			if (getClass().getResource(DEPLOYMENT_CONFIG_FILENAME) != null)
			{
				configuration.addConfiguration(new PropertiesConfiguration(DEPLOYMENT_CONFIG_FILENAME));
				logger.info("Configuration loaded.");
			}
			else
			{
				logger.warn("Configuration file could not be found. Using default configuration.");
			}

			// Add any missing properties from the defaults.

			configuration.addConfiguration(new PropertiesConfiguration("config.properties"));

			return configuration;

		}
		catch (Exception e)
		{
			throw new RuntimeException("The application could not be started do to a configuration error.", e);
		}
	}
}
