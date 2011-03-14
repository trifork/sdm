package com.trifork.sdm.replication.db;

import static com.google.inject.matcher.Matchers.*;
import static com.trifork.sdm.replication.db.properties.Database.*;
import static com.trifork.sdm.replication.db.properties.Transactions.*;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;
import javax.sql.DataSource;

import com.google.inject.Key;
import com.google.inject.Provides;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.trifork.sdm.replication.db.properties.Transactional;
import com.trifork.sdm.replication.util.ConfiguredModule;

public class DatabaseModule extends ConfiguredModule {

	private EntityManagerFactory emFactory;


	@Override
	public void configureServlets() {
		// Make it easy to do transactions.

		bindTransactionManager(transaction(WAREHOUSE), getConfig().getString("db.warehouse.username"), getConfig().getString("db.warehouse.password"), getConfig().getString("db.warehouse.host"), getConfig().getInt("db.warehouse.port"), getConfig().getString("db.warehouse.schema"));

		bindTransactionManager(transaction(ADMINISTRATION), getConfig().getString("db.administration.username"), getConfig().getString("db.administration.password"), getConfig().getString("db.administration.host"), getConfig().getInt("db.administration.port"), getConfig().getString("db.administration.schema"));

		// Unfortunately currently some of the code uses JPA and some
		// of it does not. This is a bit inconsistent but works for now.

		Map<String, Object> config = new HashMap<String, Object>();
		config.put("hibernate.connection.url", getDatabaseURL());
		config.put("hibernate.connection.username", getConfig().getString("db.warehouse.username"));
		config.put("hibernate.connection.password", getConfig().getString("db.warehouse.password"));
		config.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
		config.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

		emFactory = Persistence.createEntityManagerFactory("manager1", config);
	}


	protected String getDatabaseURL() {

		String host = getConfig().getString("db.warehouse.host");
		int port = getConfig().getInt("db.warehouse.port");
		String schema = getConfig().getString("db.warehouse.schema");
		String options = "zeroDateTimeBehavior=convertToNull";

		return String.format("jdbc:mysql://%s:%d/%s?%s", host, port, schema, options);
	}


	protected void bindTransactionManager(Transactional transaction, String username, String password, String host, int port, String schema) {
		DataSource dataSource = createDataSource(username, password, host, port, schema);
		TransactionManager manager = new TransactionManager(dataSource);

		bindInterceptor(any(), annotatedWith(transaction), manager);
		bind(Key.get(Connection.class, transaction)).toProvider(manager);
	}


	protected DataSource createDataSource(String username, String password, String host, int port, String schema) {
		MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();

		dataSource.setUser(username);
		dataSource.setPassword(password);
		dataSource.setServerName(host);
		dataSource.setPort(port);
		dataSource.setDatabaseName(schema);

		return dataSource;
	}


	@Provides
	public EntityManager provideEntityManager() {

		return emFactory.createEntityManager();
	}
}
