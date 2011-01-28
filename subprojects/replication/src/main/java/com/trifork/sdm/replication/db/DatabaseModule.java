package com.trifork.sdm.replication.db;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;
import static com.trifork.sdm.replication.db.properties.Database.ADMINISTRATION;
import static com.trifork.sdm.replication.db.properties.Database.WAREHOUSE;
import static com.trifork.sdm.replication.db.properties.Transactions.transaction;

import java.sql.Connection;

import javax.sql.DataSource;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.trifork.sdm.replication.db.properties.Transactional;

public class DatabaseModule extends AbstractModule
{
	@Override
	public void configure()
	{
		// Make it easy to do transactions.

		bindTransactionManager(transaction(WAREHOUSE), "root", "MyNewPass", "localhost", 3306, "sdm_warehouse");
		bindTransactionManager(transaction(ADMINISTRATION), "root", "MyNewPass", "localhost", 3306, "sdm_administration");
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
