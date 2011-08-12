package com.trifork.stamdata;

import java.sql.Connection;
import java.util.Properties;

import com.google.inject.*;
import com.google.inject.name.Names;
import com.trifork.stamdata.importer.ApplicationContextListener;
import com.trifork.stamdata.importer.persistence.ConnectionPool;


public class Helpers
{
	public static final String FAKE_TIME_GAP = "10";

	public static Connection getConnection()
	{
		final Properties properties = ConfigurationLoader.getForComponent(ApplicationContextListener.COMPONENT_NAME);

		Injector injector = Guice.createInjector(new AbstractModule()
		{
			@Override
			protected void configure()
			{
				Names.bindProperties(binder(), properties);
				bind(ConnectionPool.class);
			}
		});

		return injector.getInstance(ConnectionPool.class).getConnection();
	}
}
