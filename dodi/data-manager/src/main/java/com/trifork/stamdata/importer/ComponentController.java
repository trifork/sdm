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

import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceServletContextListener;
import com.trifork.stamdata.importer.jobs.JobManager;
import com.trifork.stamdata.importer.parsers.ParserScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import java.io.File;

/**
 * The entry point for the Data Manager component.
 *
 * Responsibilities:
 *
 * <ul>
 *     <li>Configure the Guice Injector</li>
 *     <li>Make sure required system properties are set.</li>
 *     <li>Startup and shutdown any internal services.</li>
 * </ul>
 *
 * @author Thomas Børlum <thb@trifork.com>
 */
public class ComponentController extends GuiceServletContextListener
{
	private static final Logger logger = LoggerFactory.getLogger(ComponentController.class);

    private static final String PARSER_INBOX_ROOT_DIR_PROP = "jboss.server.data.dir";
    
	private Injector injector;

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent)
	{
        // In order to support both production and development environments
        // we have to use the same names for system properties. For instance
        // 
        if (System.getProperty(PARSER_INBOX_ROOT_DIR_PROP) == null)
        {
            System.setProperty(PARSER_INBOX_ROOT_DIR_PROP, Files.createTempDir().getAbsolutePath());
        }
        
        logger.info("Parser inbox root is set to: {}", System.getProperty(PARSER_INBOX_ROOT_DIR_PROP));

		// We have to call the super method to allow Guice to initialize
		// itself.
        //
		super.contextInitialized(servletContextEvent);

		// Start the internal services.
        //
		try
		{
			injector.getInstance(JobManager.class).start();
            injector.getInstance(ParserScheduler.class).start();
		}
		catch (Exception e)
		{
            // Nothing we can do.
            //
			throw new RuntimeException("Could not start the services.", e);
		}

        logger.info("Stamdata Data Manager [Started]");
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent)
	{
		try
		{
			injector.getInstance(JobManager.class).stop();
            injector.getInstance(ParserScheduler.class).stop();
		}
		catch (Exception e)
		{
			// We'll just log the error here and allow the rest of the
			// system to shut down.
            //
			logger.error("Could not stop the running services.", e);
		}

        // Not sure that it actually makes a difference to not keep
        // a reference to the injector anymore. But, it doesn't hurt.
        //
        injector = null;

        // Allow Guice to wrap up.
        //
		super.contextDestroyed(servletContextEvent);

        logger.info("Stamdata Data Manager [Shutdown]");
	}

	@Override
	protected Injector getInjector()
	{
		return injector = Guice.createInjector(Stage.PRODUCTION, new ComponentModule());
	}
}
