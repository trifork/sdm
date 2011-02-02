package com.trifork.sdm.replication.replication;


import java.util.*;

import com.google.inject.TypeLiteral;
import com.google.inject.servlet.ServletModule;
import com.trifork.sdm.replication.replication.properties.Routes;
import com.trifork.stamdata.*;


public class ResourceModule extends ServletModule
{
	@Override
	protected void configureServlets()
	{
		// Map the entities to their names.

		Map<String, Class<? extends Record>> routes = new HashMap<String, Class<? extends Record>>();

		for (Class<? extends Record> entity : Entities.all())
		{
			String name = Entities.getName(entity);
			routes.put(name, entity);
		}

		// Bind the map to the generic type annotated with @Routes.

		// @formatter:off
		bind(new TypeLiteral<Map<String, Class<? extends Record>>>()
		{
		}).annotatedWith(Routes.class).toInstance(routes);
		// @formatter:on

		// Serve the replication service

		filter("/replicate").through(ReplicationFilter.class);
		serve("/replicate").with(ReplicationServlet.class);
	}
}
