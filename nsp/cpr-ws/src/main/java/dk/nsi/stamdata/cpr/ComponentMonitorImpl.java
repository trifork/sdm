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

import static com.trifork.stamdata.Preconditions.checkNotNull;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.trifork.stamdata.ComponentMonitor;
import com.trifork.stamdata.persistence.Transactional;

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
	@Transactional
	public boolean isOk()
	{
		try
		{
			// We don't want to cache the result here since the connection might actually
			// be lost at any second.
			
			Session session = sessions.get();
			session.createSQLQuery("SELECT 1 FROM Person").setCacheable(false).uniqueResult();
			
			return true;
		}
		catch (Exception e)
		{
			logger.error("Could not connect to the database.", e);
			
			return false;
		}
	}
}
