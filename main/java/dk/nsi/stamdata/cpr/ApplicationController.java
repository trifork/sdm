package dk.nsi.stamdata.cpr;

import static com.google.inject.name.Names.bindProperties;

import java.util.Properties;
import java.util.Set;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.GuiceServletContextListener;
import com.trifork.stamdata.ConfigurationLoader;

import dk.nsi.stamdata.cpr.annotations.Whitelist;

public class ApplicationController extends GuiceServletContextListener
{
	private static final String COMPONENT_NAME = "stamdata-cpr-ws";
	
	public static Injector injector;
	
	@Override
	protected Injector getInjector()
	{
		final Properties config = ConfigurationLoader.loadForName(COMPONENT_NAME);

		injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule()
		{
			@Override
			protected void configure()
			{
				bindProperties(binder(), config);
				
				bind(new TypeLiteral<Set<String>>() {}).annotatedWith(Whitelist.class).toProvider(WhitelistProvider.class);
			}
		});
		
		return injector;
	}
}
