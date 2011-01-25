package com.trifork.sdm.replication.db;


import static com.google.inject.matcher.Matchers.*;

import java.sql.*;
import java.util.Date;

import javax.inject.Provider;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.trifork.sdm.replication.settings.*;
import com.trifork.sdm.replication.util.PropertyModule;
import com.trifork.stamdata.Record;


public class DatabaseModule extends PropertyModule
{
	@Override
	public void configure()
	{
		final String url = property("db.url");
		final String schema = property("db.schema");
		final String username = property("db.username");
		final String password = property("db.password");
		
		bindConstant().annotatedWith(DbPassword.class).to(password);
		bindConstant().annotatedWith(DbUsername.class).to(username);
		bindConstant().annotatedWith(DbURL.class).to(url);
		bindConstant().annotatedWith(MainDB.class).to(schema);
		bindConstant().annotatedWith(DuplicateDB.class).to(property("db.duplicate.schema"));
		bindConstant().annotatedWith(HousekeepingDB.class).to(property("db.housekeeping.schema"));
		bindConstant().annotatedWith(AdminDB.class).to(property("db.admin.schema"));

		Provider<Connection> provider = new Provider<Connection>()
		{
			@Override
			public Connection get()
			{
				Connection connection = null;
				try
				{
					connection = DriverManager.getConnection(url + schema, username, password);
					connection.setAutoCommit(false);
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
				
				return connection;
			}
		};
		
		bindInterceptor(any(), annotatedWith(Transactional.class), new TransactionManager(provider));

		install(new FactoryModuleBuilder().implement(Query.class, MySQLQuery.class).build(QueryFactory.class));
	}


	public interface QueryFactory
	{
		Query create(Class<? extends Record> entity, long recordId, Date since, int pageSize);
	}
}
