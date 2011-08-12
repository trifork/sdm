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

import java.util.Properties;

import javax.servlet.ServletContextEvent;

import org.slf4j.*;

import com.google.inject.*;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.google.inject.servlet.*;
import com.trifork.stamdata.ConfigurationLoader;
import com.trifork.stamdata.importer.jobs.*;
import com.trifork.stamdata.importer.jobs.autorisationsregister.*;
import com.trifork.stamdata.importer.jobs.cpr.CPRParser;
import com.trifork.stamdata.importer.jobs.doseringsforslag.DoseringsforslagParser;
import com.trifork.stamdata.importer.persistence.ConnectionPool;
import com.trifork.stamdata.importer.webinterface.*;


public class ApplicationContextListener extends GuiceServletContextListener
{
	private static final Logger logger = LoggerFactory.getLogger(ApplicationContextListener.class);

	public static final String COMPONENT_NAME = "stamdata-data-manager";

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
		// Shutdown the job manager.
		
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
		// Load the configuration file.
		
		final Properties configuration = ConfigurationLoader.getForComponent(COMPONENT_NAME);
		
		// Bind the dependencies.
		
		return Guice.createInjector(Stage.PRODUCTION, new ServletModule()
		{			
			@Override
			protected void configureServlets()
			{
				// Bind the configuration.
				
				Names.bindProperties(binder(), configuration);
				
				// Serve the GUI and status servlet.

				serve("/status").with(StatusServelet.class);
				serve("/").with(GUIServlet.class);

				// Bind the service dependencies.

				bind(JobManager.class).in(Scopes.SINGLETON);
				bind(DatabaseStatus.class).in(Scopes.SINGLETON);
				bind(ConnectionPool.class).in(Scopes.SINGLETON);
				
				// Bind the file parsers.
				
				Multibinder<FileParserJob> parsers = Multibinder.newSetBinder(binder(), FileParserJob.class);
				parsers.addBinding().to(AutorisationsregisterParser.class);
				parsers.addBinding().to(CPRParser.class);
				parsers.addBinding().to(DoseringsforslagParser.class);
				
				// Bind the batch jobs.
				
				Multibinder<BatchJob> batchJobs = Multibinder.newSetBinder(binder(), BatchJob.class);
				batchJobs.addBinding().to(AutorisationsregisterUpdater.class);
			}
		});
	}
}
