package dk.nsi.stamdata.cpr;

import static com.google.inject.name.Names.bindProperties;

import java.util.Set;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.GuiceServletContextListener;
import com.trifork.stamdata.ComponentMonitor;
import com.trifork.stamdata.ConfigurationLoader;
import com.trifork.stamdata.MonitoringModule;

import dk.nsi.dgws.DgwsModule;
import dk.nsi.stamdata.cpr.WhitelistProvider.Whitelist;

public class ComponentController extends GuiceServletContextListener
{
	private static final TypeLiteral<Set<String>> A_SET_OF_STRINGS = new TypeLiteral<Set<String>>() {};
	private static final String DISPLAY_SOAP_FAULT_STACK_TRACE = "com.sun.xml.ws.fault.SOAPFaultBuilder.disableCaptureStackTrace";
	public static final String COMPONENT_NAME = "stamdata-cpr-ws";
	
	@Override
	protected Injector getInjector()
	{
		return Guice.createInjector(Stage.PRODUCTION, new ComponentModule());
	}
	
	public static class ComponentModule extends AbstractModule
	{
		@Override
		protected void configure()
		{
			// We don't want JAX-WS to expose the stack trace
			// when an exception occurs.
			
			System.setProperty(DISPLAY_SOAP_FAULT_STACK_TRACE, "false");
			
			// Load the components configuration and bind it to named dependencies.
			
			bindProperties(binder(), ConfigurationLoader.loadForName(COMPONENT_NAME));

			// The whitelist controls which clients have access to protected
			// data and which that do not.
			
			// FIXME: Should it also restrict access to the service?
			
			bind(A_SET_OF_STRINGS).annotatedWith(Whitelist.class).toProvider(WhitelistProvider.class);
			
			install(new DgwsModule());
			
			install(new PersistenceModule());

			// Tell the monitoring module how to monitor the component.
			
			bind(ComponentMonitor.class).to(ComponentMonitorImpl.class);
			install(new MonitoringModule());
		}
	}
}
