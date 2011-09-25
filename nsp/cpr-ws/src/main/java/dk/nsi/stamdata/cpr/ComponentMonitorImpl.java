package dk.nsi.stamdata.cpr;

import static com.trifork.stamdata.Preconditions.checkNotNull;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.trifork.stamdata.ComponentMonitor;

public class ComponentMonitorImpl implements ComponentMonitor
{
	private static final Logger logger = LoggerFactory.getLogger(ComponentMonitorImpl.class);
	private final Provider<Session> sessions;
	
	@Inject
	ComponentMonitorImpl(Provider<Session> sessions)
	{
		this.sessions = checkNotNull(sessions, "sessions");
	}
	
	@Override
	public boolean isOK()
	{
		try
		{
			// We don't want to cache the result here since the connection might actually
			// be lost at any second.
			
			Session session = sessions.get();
			session.createQuery("SELECT 1 FROM Person").setCacheable(false).uniqueResult();
			session.disconnect().close();
			
			return true;
		}
		catch (Exception e)
		{
			logger.error("Could not connect to the database.", e);
			
			return false;
		}
	}
}
