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

import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.ComponentMonitor;
import com.trifork.stamdata.MonitoringModule;
import com.trifork.stamdata.authorization.security.SecurityModule;
import com.trifork.stamdata.persistence.PersistenceFilter;

import dk.nsi.stamdata.security.DenGodeWebServiceFilter;
import dk.nsi.stamdata.security.DenGodeWebServiceModule;

public class WebserviceModule extends ServletModule
{
    @Override
    protected void configureServlets()
    {
        install(new SecurityModule());
        filterRegex("(?!/status)/.*").through(DenGodeWebServiceFilter.class);
        install(new DenGodeWebServiceModule());
        
        filter("/*").through(PersistenceFilter.class);
        
        bind(ComponentMonitor.class).to(AuthorizationComponentMonitor.class);
        install(new MonitoringModule());
    }
}
