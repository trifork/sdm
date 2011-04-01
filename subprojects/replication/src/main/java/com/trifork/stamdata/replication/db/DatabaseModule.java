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

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;

import javax.persistence.Entity;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.google.inject.Provides;
import com.trifork.stamdata.replication.util.ConfiguredModule;


public class DatabaseModule extends ConfiguredModule {

	public DatabaseModule() throws IOException {

		super();
	}

	private SessionFactory sessionFactory;

	@Override
	public void configureServlets() {

		// TODO: Is this needed in JBOSS?

		try {
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		}
		catch (SQLException e) {
			System.out.println("Oops! Got a MySQL error: " + e.getMessage());
		}
		
		Reflections reflector = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.getUrlsForCurrentClasspath()).setScanners(new TypeAnnotationsScanner()));
		Set<Class<?>> classes = reflector.getTypesAnnotatedWith(Entity.class);

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
