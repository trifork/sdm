package com.trifork.sdm.replication;


import com.google.inject.AbstractModule;
import com.trifork.sdm.replication.db.DatabaseModule;


public class XMLRecordPersisterModule extends AbstractModule
{

	@Override
	protected void configure()
	{
		install(new DatabaseModule());

		bind(JdbcXMLRecordPersister.class).to(JdbcXMLRecordPersister.class);
	}
}
