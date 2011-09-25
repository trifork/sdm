package dk.nsi.stamdata.cpr;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.models.cpr.Person;
import com.trifork.stamdata.models.sikrede.SikredeYderRelation;
import com.trifork.stamdata.models.sikrede.Yderregister;

public class PersistenceModule extends ServletModule
{
	@Override
	protected void configureServlets()
	{
		bind(Session.class).toProvider(PersistenceManager.class);
		filter("/*").through(PersistenceManager.class);
	}
	
	@Singleton
	public static class PersistenceManager implements Provider<Session>, Filter
	{
		public static String HIBERNATE_SESSION_KEY = "dk.nsi.stamdata.cpr.session";
		
		private static final String USERNAME_PROP = "db.connection.username";
		private static final String PASSWORD_PROP = "db.connection.password";
		private static final String JDBC_URL_PROP = "db.connection.jdbcURL";
		
		private final SessionFactory sessionFactory;
		private final ThreadLocal<Session> session = new ThreadLocal<Session>();
		
		@Inject
		PersistenceManager(@Named(JDBC_URL_PROP) String jdbcURL, @Named(USERNAME_PROP) String username, @Named(PASSWORD_PROP) String password)
		{
			Configuration config = new Configuration();

			config.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
			config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect");
			config.setProperty("hibernate.connection.url", jdbcURL);

			config.setProperty("hibernate.connection.username", username);
			config.setProperty("hibernate.connection.password", password);

			config.setProperty("hibernate.connection.zeroDateTimeBehavior", "convertToNull");
			config.setProperty("hibernate.connection.characterEncoding", "utf8");

			config.setProperty("hibernate.current_session_context_class", "thread");
			config.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");

			config.addAnnotatedClass(Person.class);
			config.addAnnotatedClass(Yderregister.class);
			config.addAnnotatedClass(SikredeYderRelation.class);

			sessionFactory = config.buildSessionFactory();
			sessionFactory.openSession().isConnected();
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
			}
			
			return session;
		}
		
		@Override
		public void init(FilterConfig config) throws ServletException
		{
		}
		
		@Override
		public void destroy()
		{
		}
		
		@Override
		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
		{
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
			}
			finally
			{
				try
				{
					session.close();
				}
				catch (Exception ex)
				{
					
				}
				
				// Reset the thread local.
				
				this.session.remove();
			}
		}
	}
}
