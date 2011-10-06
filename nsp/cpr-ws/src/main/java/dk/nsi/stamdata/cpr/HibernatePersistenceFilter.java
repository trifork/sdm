/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */
package dk.nsi.stamdata.cpr;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;

import dk.nsi.stamdata.cpr.models.Person;
import dk.nsi.stamdata.cpr.models.SikredeYderRelation;
import dk.nsi.stamdata.cpr.models.Yderregister;

@Singleton
public class HibernatePersistenceFilter implements Provider<Session>, Filter
{
	public static String HIBERNATE_SESSION_KEY = "dk.nsi.stamdata.cpr.session";

	private static final String USERNAME_PROP = "db.connection.username";
	private static final String PASSWORD_PROP = "db.connection.password";
	private static final String JDBC_URL_PROP = "db.connection.jdbcURL";

	private final SessionFactory sessionFactory;
	private final ThreadLocal<Session> session = new ThreadLocal<Session>();


	@Inject
	HibernatePersistenceFilter(@Named(JDBC_URL_PROP) String jdbcURL, @Named(USERNAME_PROP) String username, @Named(PASSWORD_PROP) String password)
	{
		Configuration config = new Configuration();

		config.setProperty("hibernate.connection.url", jdbcURL);
		config.setProperty("hibernate.connection.username", username);
		config.setProperty("hibernate.connection.password", password);
		config.setProperty("hibernate.archive.autodetection", "class, hbm");

		config.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
		config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect");

		config.setProperty("hibernate.connection.zeroDateTimeBehavior", "convertToNull");
		config.setProperty("hibernate.connection.characterEncoding", "utf8");

		config.setProperty("hibernate.current_session_context_class", "thread");
		config.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");

		// Use a C3P0 connection pool.
		// The default connection pool is not meant for production use.

		config.setProperty("hibernate.c3p0.min_size", "5");
		config.setProperty("hibernate.c3p0.max_size", "20");
		config.setProperty("hibernate.c3p0.timeout", "200");

		// Add annotated classes to track.

		config.addAnnotatedClass(Person.class);
		config.addAnnotatedClass(Yderregister.class);
		config.addAnnotatedClass(SikredeYderRelation.class);

		sessionFactory = config.buildSessionFactory();
		Session session = sessionFactory.openSession();
		
		System.out.println("Listing mapped classes:");
		
		for (ClassMetadata meta : sessionFactory.getAllClassMetadata().values())
		{
		    System.out.println("MAPPED CLASS: " + meta.getEntityName());
		}
	}


	@Override
	public Session get()
	{
		Session session = this.session.get();

		if (session == null)
		{
			// This is the case when the persistence filter is
			// not used. You have to manage the transaction manually.

			session = sessionFactory.openSession();
			this.session.set(session);
		}

		return session;
	}


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		// Each request is wrapped in a transaction.

		Session session = null;

		try
		{
			session = sessionFactory.openSession();
			session.beginTransaction();

			this.session.set(session);

			chain.doFilter(request, response);

			session.getTransaction().commit();
		}
		catch (Exception e)
		{
			try
			{
				session.getTransaction().rollback();
			}
			catch (Exception ex)
			{

			}
			
			// Let other filters handle the exception.
			
			throw new ServletException(e);
		}
		finally
		{
			this.session.remove();
		}
	}


	@Override
	public void init(FilterConfig config) throws ServletException {}


	@Override
	public void destroy() {}
}
