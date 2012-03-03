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
package dk.nsi.stamdata.cpr;

import static com.google.inject.name.Names.bindProperties;

import java.util.Set;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.ComponentMonitor;
import com.trifork.stamdata.ConfigurationLoader;
import com.trifork.stamdata.MonitoringModule;
import com.trifork.stamdata.persistence.PersistenceModule;
import com.trifork.stamdata.persistence.Persistent;

import dk.nsi.stamdata.security.*;
import dk.nsi.stamdata.cpr.models.Person;
import dk.nsi.stamdata.cpr.pvit.proxy.CprSubscriptionClient;
import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;


public class ComponentController extends GuiceServletContextListener
{
	private static final TypeLiteral<Set<String>> A_SET_OF_STRINGS = new TypeLiteral<Set<String>>() {};
	private static final String DISPLAY_SOAP_FAULT_STACK_TRACE = "com.sun.xml.ws.fault.SOAPFaultBuilder.disableCaptureStackTrace";
	public static final String COMPONENT_NAME = "stamdata-cpr-ws";

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
		return Guice.createInjector(Stage.PRODUCTION, new ComponentModule(), new ServiceModule());
	}


	public static class ComponentModule extends AbstractModule
	{
		@Override
		protected void configure()
		{
			// Load the components configuration and bind it to named
			// dependencies.

			bindProperties(binder(), ConfigurationLoader.loadForName(COMPONENT_NAME));
            getLogger().info("Configuring ComponentModule for " + COMPONENT_NAME);

			// The white-list controls which clients have access to protected
			// data and which that do not.

//			bind(A_SET_OF_STRINGS).annotatedWith(Whitelist.class).toProvider(WhitelistProvider.class); //TODO: FRJ - replaced by methodinterceptor and WhitelistDbInterceptorModule in ServiceModule - remove this when tested
            bind(WhitelistService.class).toProvider(WhitelistServiceProvider.class);
            getLogger().info("Installing PersistenceModule");
			install(new PersistenceModule());
			
			// Bind the classes that Hibernate needs to manager.
			
			Multibinder<Object> persistentClasses = Multibinder.newSetBinder(binder(), Object.class, Persistent.class); 
			persistentClasses.addBinding().to(Person.class);
		}
	}


	private class ServiceModule extends ServletModule
	{
		@Override
		protected void configureServlets()
		{
			// We don't want JAX-WS to expose the stack trace
			// when an exception occurs.
            getLogger().info("Configuring ServiceModule for " + COMPONENT_NAME);
			System.setProperty(DISPLAY_SOAP_FAULT_STACK_TRACE, "false");
			
			// To make sure the property for the CPR ABBS end-point is set
			// we bind the class here and have Guice check at start-up if
			// if can be instantiated.
			
			bind(CprSubscriptionClient.class);

			// Tell the monitoring module how to monitor the component.
			// All monitor pages are bound to the URL /status.

			bind(ComponentMonitor.class).to(ComponentMonitorImpl.class);
            getLogger().info("Installing MonitoringModule");
			install(new MonitoringModule());

			// Filter everything through the DGWS filter,
			// but exclude the status page.

			filterRegex("(?!/status)/.*").through(DenGodeWebServiceFilter.class);

            getLogger().info("Installing DenGodeWebServiceModule");
			install(new DenGodeWebServiceModule());
            getLogger().info("Installing WhitelistDbInterceptorModule");
            install(new WhitelistDbInterceptorModule()); // Enable this to get "whitelist from database" features scheduled for 2.1 release
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
