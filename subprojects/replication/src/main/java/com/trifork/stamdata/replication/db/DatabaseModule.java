package com.trifork.stamdata.replication.db;

import java.io.IOException;
import java.util.Set;

import javax.persistence.Entity;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.Configuration;
import com.google.inject.Provides;
import com.trifork.stamdata.replication.util.ConfiguredModule;


public class DatabaseModule extends ConfiguredModule {

	public DatabaseModule() throws IOException {

		super();
	}

	private SessionFactory sessionFactory;

	@Override
	public void configureServlets() {

		try {
			Configuration config = new Configuration();

			for (Class<?> c : classes) {
				config.addAnnotatedClass(c);
			}

			sessionFactory = config.configure().buildSessionFactory();
		}
		catch (Exception e) {
			addError(e);
		}

		filter("/*").through(PersistenceFilter.class);
	}

	@Provides
	public Session provideSession() {

		return sessionFactory.getCurrentSession();
	}

	@Provides
	public StatelessSession provideStatelessSession() {

		// Hibernate provides a command-oriented API that can be used for
		// streaming data to and from the database in the form of detached
		// objects. A StatelessSession has no persistence context associated
		// with it and does not provide many of the higher-level life cycle
		// semantics. In particular, a stateless session does not implement a
		// first-level cache nor interact with any second-level or query cache.
		// It does not implement transactional write-behind or automatic dirty
		// checking. Operations performed using a stateless session never
		// cascade to associated instances. Collections are ignored by a
		// stateless session. Operations performed via a stateless session
		// bypass Hibernate's event model and interceptors. Due to the lack of a
		// first-level cache, Stateless sessions are vulnerable to data aliasing
		// effects. A stateless session is a lower-level abstraction that is
		// much closer to the underlying JDBC.

		return sessionFactory.openStatelessSession();
	}
}
