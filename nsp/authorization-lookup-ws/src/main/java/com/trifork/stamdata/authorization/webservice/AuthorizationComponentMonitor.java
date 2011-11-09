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

package com.trifork.stamdata.authorization.webservice;

import javax.inject.Provider;
import javax.inject.Singleton;

import org.hibernate.Session;

import com.google.inject.Inject;
import com.trifork.stamdata.ComponentMonitor;

/**
 * Status monitor that check if the database connection is up.
 * 
 * @author Thomas Børlum <thb@trifork.com>
 */
@Singleton
public class AuthorizationComponentMonitor implements ComponentMonitor
{
    private final Provider<Session> session;

    @Inject
    AuthorizationComponentMonitor(Provider<Session> session)
    {
        this.session = session;
    }

    @Override
    public boolean isOk()
    {
        session.get().createSQLQuery("SELECT 1").setCacheable(false).uniqueResult();
        
        return true;
    }
}
