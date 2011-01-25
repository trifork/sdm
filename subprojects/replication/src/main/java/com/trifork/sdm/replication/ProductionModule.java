package com.trifork.sdm.replication;


import com.google.inject.AbstractModule;
import com.trifork.sdm.replication.admin.AdminstrationModule;
import com.trifork.sdm.replication.db.DatabaseModule;
import com.trifork.sdm.replication.gateway.GatewayModule;
import com.trifork.sdm.replication.gateway.RandomSecretModule;
import com.trifork.sdm.replication.replication.ResourceModule;


public class ProductionModule extends AbstractModule
{
	@Override
	protected void configure()
	{
		install(new DatabaseModule());
		install(new RandomSecretModule());
		install(new GatewayModule());
		install(new AdminstrationModule());
		install(new ResourceModule());
	}
}
