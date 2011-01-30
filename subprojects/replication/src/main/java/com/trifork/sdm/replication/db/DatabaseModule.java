package com.trifork.sdm.replication.db;

import static com.google.inject.matcher.Matchers.*;
import static com.trifork.sdm.replication.db.properties.Database.*;
import static com.trifork.sdm.replication.db.properties.Transactions.*;

import java.sql.Connection;

import javax.sql.DataSource;

import com.google.inject.Key;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.trifork.sdm.replication.db.properties.Transactional;
import com.trifork.sdm.replication.util.ConfiguredModule;


public class DatabaseModule extends ConfiguredModule
{
	@Override
	public void configureServlets()
	{
		// @formatter:off
		
		// Make it easy to do transactions.

		bindTransactionManager(transaction(WAREHOUSE),
			getConfig().getString("db.warehouse.username"),
			getConfig().getString("db.warehouse.password"),
			getConfig().getString("db.warehouse.host"),
			getConfig().getInt("db.warehouse.port"),
			getConfig().getString("db.warehouse.schema")
		);

		bindTransactionManager(transaction(ADMINISTRATION),
			getConfig().getString("db.administration.username"),
			getConfig().getString("db.administration.password"),
			getConfig().getString("db.administration.host"),
			getConfig().getInt("db.administration.port"),
			getConfig().getString("db.administration.schema")
		);
	
		// @formatter:on
	}


	protected void bindTransactionManager(Transactional transaction, String username, String password, String host, int port, String schema)
	{
		DataSource dataSource = createDataSource(username, password, host, port, schema);
		TransactionManager manager = new TransactionManager(dataSource);

		bindInterceptor(any(), annotatedWith(transaction), manager);
		bind(Key.get(Connection.class, transaction)).toProvider(manager);
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
