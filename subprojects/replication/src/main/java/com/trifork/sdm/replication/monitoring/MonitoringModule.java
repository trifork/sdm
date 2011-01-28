package com.trifork.sdm.replication.monitoring;

import com.google.inject.servlet.ServletModule;

public class MonitoringModule extends ServletModule
{
	@Override
	protected void configureServlets()
	{
		serve("/status").with(StatusServlet.class);
	}
}
