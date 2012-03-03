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

package com.trifork.stamdata.authorization;

import static com.google.inject.name.Names.bindProperties;

import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceServletContextListener;
import com.trifork.stamdata.ConfigurationLoader;
import com.trifork.stamdata.authorization.models.DbModule;
import com.trifork.stamdata.authorization.webservice.WebserviceModule;

import dk.nsi.stamdata.security.DenGodeWebServiceFilter;
import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;


public class ComponentController extends GuiceServletContextListener
{
	private static final String COMPONENT_NAME = "stamdata-authorization-lookup-ws";

    private static Logger logger;

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        super.contextDestroyed(servletContextEvent);
        getLogger().info(servletContextEvent.getServletContext().getServletContextName() + " [Shutdown]");
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        super.contextInitialized(servletContextEvent);
        getLogger().info(servletContextEvent.getServletContext().getServletContextName() + " [Started]");
    }

	@Override
	protected Injector getInjector()
	{
	    return Guice.createInjector(Stage.PRODUCTION, new ComponentModule(), new WebserviceModule());
	}


	public static class ComponentModule extends AbstractModule
	{
        @Override
        protected void configure()
        {
            Properties properties = ConfigurationLoader.loadForName(COMPONENT_NAME);
            getLogger().info("Loaded configuration for component: " + COMPONENT_NAME);
            bindProperties(binder(), properties);
            
            // A previous version of this component used the property
            // 'security' to determine what type of security to use (e.g. none, DGWS, Two-way-SSL).
            // To avoid the operator (Netic) to have to change deployment (i.e. puppet scripts) we
            // still support this property.

            String useTestSTS = "dgwsTest".equalsIgnoreCase(properties.getProperty("security")) ? "true" : "false";
            getLogger().info("Using TestSTS: " + useTestSTS);
            bindConstant().annotatedWith(Names.named(DenGodeWebServiceFilter.USE_TEST_FEDERATION_PARAMETER)).to(useTestSTS);
            
            install(new DbModule());
        }
	}
    
    private static Logger getLogger()
    {
        if (logger == null) {
            logger = Logger.getLogger(ComponentController.class);
        }
        return logger;
    }
}
