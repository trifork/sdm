package com.trifork.stamdata.replication.db;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import com.google.inject.Provides;
import com.google.inject.servlet.RequestScoped;
import com.trifork.stamdata.replication.util.ConfiguredModule;


public class DatabaseModule extends ConfiguredModule {

	private EntityManagerFactory emFactory;

	@Override
	public void configureServlets() {

		Map<String, Object> config = new HashMap<String, Object>();
		config.put("hibernate.connection.url", getDatabaseURL());
		config.put("hibernate.connection.username", getConfig().getString("db.warehouse.username"));
		config.put("hibernate.connection.password", getConfig().getString("db.warehouse.password"));
		config.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");

		emFactory = Persistence.createEntityManagerFactory("manager1", config);

		filter("/*").through(PersistenceFilter.class);
	}

	protected String getDatabaseURL() {

		String host = getConfig().getString("db.warehouse.host");
		int port = getConfig().getInt("db.warehouse.port");
		String schema = getConfig().getString("db.warehouse.schema");
		String options = "zeroDateTimeBehavior=convertToNull&characterEncoding=UTF-8";

		return String.format("jdbc:mysql://%s:%d/%s?%s", host, port, schema, options);
	}

	@Provides
	@RequestScoped
	public EntityManager provideEntityManager() {

		return emFactory.createEntityManager();
	}
}
