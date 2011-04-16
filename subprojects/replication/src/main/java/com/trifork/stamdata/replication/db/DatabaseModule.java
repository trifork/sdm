// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package com.trifork.stamdata.replication.db;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.Configuration;

import com.google.common.collect.Sets;
import com.google.inject.Provides;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.Nullable;
import com.trifork.stamdata.replication.gui.models.Client;
import com.trifork.stamdata.replication.gui.models.User;
import com.trifork.stamdata.replication.logging.LogEntry;
import com.trifork.stamdata.replication.replication.views.Views;


public class DatabaseModule extends ServletModule {

	private SessionFactory sessionFactory;
	private final String driverClass;
	private final String hibernateDialect;
	private final String jdbcURL;
	private final String username;
	private final String password;

	public DatabaseModule(String driverClass, String hibernateDialect, String jdbcURL, String username, @Nullable String password) {
		
		this.driverClass = checkNotNull(driverClass);
		this.hibernateDialect = checkNotNull(hibernateDialect);
		this.jdbcURL = checkNotNull(jdbcURL);
		this.username = checkNotNull(username);
		this.password = password;
	}

	@Override
	protected final void configureServlets() {

		// DISCOVER ALL ENTITY CLASSES

		Set<Class<?>> classes = Sets.newHashSet();
		classes.addAll(Views.findAllViews());
		classes.add(User.class);
		classes.add(LogEntry.class);
		classes.add(Client.class);

		try {
			Configuration config = new Configuration();

			config.setProperty("hibernate.connection.driver_class", driverClass);
			config.setProperty("hibernate.dialect", hibernateDialect);
			config.setProperty("hibernate.connection.url", jdbcURL);

			config.setProperty("hibernate.connection.username", username);
			config.setProperty("hibernate.connection.password", password);

			config.setProperty("hibernate.connection.zeroDateTimeBehavior", "convertToNull");
			config.setProperty("hibernate.connection.characterEncoding", "utf8");

			// TODO: Not sure that the following 3 settings work.
			// They might have to be added to the JDBC url to have any effect.

			config.setProperty("hibernate.connection.useCursorFetch", "true");
			config.setProperty("hibernate.connection.useServerPrepStmts", "true");
			config.setProperty("hibernate.connection.defaultFetchSize", "1000");

			config.setProperty("hibernate.c3p0.min_size", "5");
			config.setProperty("hibernate.c3p0.max_size", "200");
			config.setProperty("hibernate.c3p0.timeout", "300");

			// Do not set "hibernate.c3p0.max_statements" it to anything above 0.
			// This might cause deadlocks. If you do set it set it to a very high
			// number, this will cost memory but give better performence.
			
			// The following two properties can be used to debug c3p0's connections.
			// They are commented out since they are quite expensive.
			
			// config.setProperty("hibernate.c3p0.unreturnedConnectionTimeout", "120");
			// config.setProperty("hibernate.c3p0.debugUnreturnedConnectionStackTraces", "true");

			config.setProperty("hibernate.current_session_context_class", "thread");
			config.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");

			for (Class<?> c : classes) {
				config.addAnnotatedClass(c);
			}

			sessionFactory = config.buildSessionFactory();
		}
		catch (Exception e) {
			addError(e);
		}

		filter("/*").through(PersistenceFilter.class);
	}

	@Provides
	protected Session provideSession() {

		return sessionFactory.getCurrentSession();
	}

	@Provides
	@RequestScoped
	protected StatelessSession provideStatelessSession() {

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
