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
package dk.nsi.stamdata.replication.monitoring;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.trifork.stamdata.ComponentMonitor;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import static com.trifork.stamdata.Preconditions.checkNotNull;

public class ComponentMonitorImpl implements ComponentMonitor {
    private static final Logger logger = Logger.getLogger(ComponentMonitorImpl.class);
    private final Provider<Session> sessions;

    @Inject
    ComponentMonitorImpl(Provider<Session> sessions) {
        this.sessions = checkNotNull(sessions, "sessions");
    }

    @Override
    public boolean isOk() {
        try {
            // We don't want to cache the result here since the connection might actually
            // be lost at any second.

            Session session = sessions.get();
            session.createSQLQuery("SELECT 1 FROM Person LIMIT 1").setCacheable(false).uniqueResult();

            return true;
        } catch (Exception e) {
            logger.error("Could not connect to the database.", e);

            return false;
        }
    }
}
