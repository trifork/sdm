package com.trifork.sdm.replication.db;


import static com.google.inject.matcher.Matchers.*;
import static com.google.inject.name.Names.*;

import java.sql.Connection;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.sql.DataSource;

import com.google.inject.*;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.trifork.sdm.replication.db.TransactionManager.Transactional;


public class DatabaseModule extends AbstractModule
{
	@Override
	public void configure()
	{
		requireBinding(Key.get(String.class, named("db.username")));
		requireBinding(Key.get(String.class, named("db.password")));
		requireBinding(Key.get(String.class, named("db.host")));
		requireBinding(Key.get(String.class, named("db.port")));

		// Make it easy to do transactions.

		TransactionManager manager = new TransactionManager();

		// Make sure we inject the JDBC support instance,
		// once configuration is completed.

		requestInjection(manager);

		// Make the appropriate bindings.

		bindInterceptor(any(), annotatedWith(Transactional.class), manager);
		bind(Connection.class).toProvider(manager);
	}


	@Provides
	@Singleton
	protected DataSource provideDataSource(@Named("db.username") String username, @Named("db.password") String password, @Named("db.host") String host, @Named("db.port") int port)
	{
		MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();

		dataSource.setUser(username);
		dataSource.setPassword(password);
		dataSource.setServerName(host);
		dataSource.setPort(port);

		// The default schema is 'mysql', I doubt that
		// you anyone would actually change this by
		// configuration on the db.

		dataSource.setDatabaseName("mysql");

		return dataSource;
	}
}
