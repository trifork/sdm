package dk.nsi.stamdata.cpr;

import static com.google.inject.name.Names.bindProperties;

import java.util.Properties;
import java.util.Set;

import org.hibernate.Session;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.GuiceServletContextListener;
import com.trifork.stamdata.ComponentMonitor;
import com.trifork.stamdata.ConfigurationLoader;
import com.trifork.stamdata.MonitoringModule;

import dk.nsi.stamdata.cpr.annotations.Whitelist;

public class ApplicationController extends GuiceServletContextListener
{
	private static final TypeLiteral<Set<String>> A_SET_OF_STRINGS = new TypeLiteral<Set<String>>() {};
	private static final String DISPLAY_SOAP_FAULT_STACK_TRACE = "com.sun.xml.ws.fault.SOAPFaultBuilder.disableCaptureStackTrace";
	public static final String COMPONENT_NAME = "stamdata-cpr-ws";
	
	public static Injector injector;
	
	@Override
	protected Injector getInjector()
	{
		// We don't want JAX-WS to expose the stack trace
		// when an exception occurs.
		
		System.setProperty(DISPLAY_SOAP_FAULT_STACK_TRACE, "false");
		
		// Load the components configuration and bind it to named dependencies.
		
		final Properties config = ConfigurationLoader.loadForName(COMPONENT_NAME);

		// Create the injector and bind all the dependencies.
		
		injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule()
		{
			@Override
			protected void configure()
			{
				bindProperties(binder(), config);
				
				bind(A_SET_OF_STRINGS).annotatedWith(Whitelist.class).toProvider(WhitelistProvider.class);
				bind(Session.class).toProvider(SessionProvider.class);

				bind(ComponentMonitor.class).to(ComponentMonitorImpl.class);
				install(new MonitoringModule());
			}
		});
		
		return injector;
	}
}
