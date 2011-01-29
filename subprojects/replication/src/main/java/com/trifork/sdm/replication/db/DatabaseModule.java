package com.trifork.sdm.replication.db;

import static com.google.inject.matcher.Matchers.*;
import static com.trifork.sdm.replication.db.properties.Database.*;
import static com.trifork.sdm.replication.db.properties.Transactions.*;

import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.commons.configuration.*;
import org.apache.commons.configuration.ConfigurationException;

import com.google.inject.*;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.trifork.sdm.replication.db.properties.Transactional;


public class DatabaseModule extends AbstractModule
{
	@Override
	public void configure()
	{
		// NOTE: We load the configuration file another time, since we have
		// to bind the 'at config time' we can't put it off.

		try
		{
			PropertiesConfiguration properties = new PropertiesConfiguration("config.properties");

			// Make it easy to do transactions.

			bindTransactionManager(transaction(WAREHOUSE), properties.getString("db.warehouse.username"), properties.getString("db.warehouse.password"), properties.getString("db.warehouse.host"), properties.getInt("db.warehouse.port"), properties.getString("db.warehouse.schema"));

			bindTransactionManager(transaction(ADMINISTRATION), properties.getString("db.administration.username"), properties.getString("db.administration.password"), properties.getString("db.administration.host"), properties.getInt("db.administration.port"), properties.getString("db.administration.schema"));
		}
		catch (ConfigurationException e)
		{
			addError("Could not bind database connections during setup.", e);
		}
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
