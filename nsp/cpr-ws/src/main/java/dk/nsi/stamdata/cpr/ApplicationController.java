package dk.nsi.stamdata.cpr;

import static com.google.inject.name.Names.bindProperties;

import java.util.Properties;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.ConfigurationLoader;

public class ApplicationController extends GuiceServletContextListener
{
	private static final String COMPONENT_NAME = "stamdata-cpr-ws";
	
	@Override
	protected Injector getInjector()
	{
		final Properties config = ConfigurationLoader.loadForName(COMPONENT_NAME);
		
		return Guice.createInjector(new ServletModule() {
			
			@Override
			protected void configureServlets()
			{
				bindProperties(binder(), config);
			}
		});
	}	
}
