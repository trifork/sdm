package com.trifork.stamdata.replication;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;


public class DatabaseHelper {

	private SessionFactory sessionFactory;

	public DatabaseHelper(Class<?>... entities) throws Exception {

		final PropertiesConfiguration props = new PropertiesConfiguration(getClass().getClassLoader().getResource("config.properties"));

		Configuration config = new Configuration();

		config.setProperty("hibernate.connection.driver_class", props.getString("db.connection.driverClass"));
		config.setProperty("hibernate.dialect", props.getString("db.connection.sqlDialect"));
		config.setProperty("hibernate.connection.url", props.getString("db.connection.jdbcURL"));

		config.setProperty("hibernate.connection.username", props.getString("db.connection.username"));
		config.setProperty("hibernate.connection.password", props.getString("db.connection.password", null));

		config.setProperty("hibernate.connection.zeroDateTimeBehavior", "convertToNull");
		config.setProperty("hibernate.connection.characterEncoding", "utf8");

		config.setProperty("hibernate.current_session_context_class", "thread");
		config.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");

		for (Class<?> entity : entities) {
			config.addAnnotatedClass(entity);
		}

		sessionFactory = config.buildSessionFactory();

	}

	public Session openSession() {
		return sessionFactory.openSession();
	}
	
	public StatelessSession openStatelessSession() {
		return sessionFactory.openStatelessSession();
	}
}
