package com.trifork.sdm.replication.db;


import static com.google.inject.matcher.Matchers.*;
import static com.google.inject.name.Names.*;
import static com.trifork.sdm.replication.db.properties.Database.*;
import static com.trifork.sdm.replication.db.properties.Transactions.*;

import java.sql.Connection;

import javax.sql.DataSource;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.trifork.sdm.replication.db.properties.*;


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

		DataSource dataSource;
		
		dataSource = createDataSource("root", "", "localhost", 3306, "sdm");
		TransactionManager adminTM = new TransactionManager(dataSource);
		bindInterceptor(any(), annotatedWith(Transaction.class), adminTM);
		bind(Key.get(Connection.class, Transaction.class)).toProvider(adminTM);
		
		dataSource = createDataSource("root", "", "", 3306, "sdm_admin");
		TransactionManager mainTM = new TransactionManager(dataSource);
		bindInterceptor(any(), annotatedWith(transaction(SDM)), mainTM);
		bind(Key.get(Connection.class, AdminTransaction.class)).toProvider(mainTM);
	}


	protected DataSource createDataSource(String username, String password, String host, int port, String schema)
	{
		MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();

		dataSource.setUser(username);
		dataSource.setPassword(password);
		dataSource.setServerName(host);
		dataSource.setPort(port);
		dataSource.setDatabaseName(schema);

		return dataSource;
	}
}
