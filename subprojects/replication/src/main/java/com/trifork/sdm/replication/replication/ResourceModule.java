package com.trifork.sdm.replication.replication;

import com.google.inject.servlet.ServletModule;


public class ResourceModule extends ServletModule
{
	@Override
	protected void configureServlets()
	{
		filter("/replicate").through(ReplicationFilter.class);
		serve("/replicate").with(EntityServlet.class);
	}
}
