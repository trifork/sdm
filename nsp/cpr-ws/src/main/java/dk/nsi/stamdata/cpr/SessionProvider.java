package dk.nsi.stamdata.cpr;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.trifork.stamdata.models.cpr.Person;

public class SessionProvider implements Provider<Session>
{
	private static final String USERNAME_PROP = "db.connection.username";
	private static final String PASSWORD_PROP = "db.connection.password";
	private static final String JDBC_URL_PROP = "db.connection.jdbcURL";
	
	private final SessionFactory sessionFactory;
	
	@Inject
	SessionProvider(@Named(JDBC_URL_PROP) String jdbcURL, @Named(USERNAME_PROP) String username, @Named(PASSWORD_PROP) String password)
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
		
		sessionFactory = config.buildSessionFactory();
		sessionFactory.openSession().isConnected();
	}
	
	@Override
	public Session get()
	{
		return sessionFactory.openSession();
	}
}
