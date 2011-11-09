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

package dk.nsi.stamdata.replication;

import static com.google.inject.name.Names.bindProperties;

import java.util.Properties;

import ch.qos.logback.classic.helpers.MDCInsertingServletFilter;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.Stage;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.ComponentMonitor;
import com.trifork.stamdata.ConfigurationLoader;
import com.trifork.stamdata.MonitoringModule;
import com.trifork.stamdata.persistence.PersistenceFilter;
import com.trifork.stamdata.persistence.PersistenceModule;
import com.trifork.stamdata.persistence.StatelessPersistenceFilter;

import dk.nsi.dgws.DenGodeWebServiceFilter;
import dk.nsi.dgws.DenGodeWebServiceModule;
import dk.nsi.stamdata.replication.models.AuthenticationModule;
import dk.nsi.stamdata.replication.monitoring.ComponentMonitorImpl;
import dk.nsi.stamdata.views.ViewModule;
import dk.sdsd.nsp.slalog.ws.SLALoggingServletFilter;


public class ComponentController extends GuiceServletContextListener
{
    private static final String COMPONENT_NAME = "stamdata-batch-copy-ws";


    @Override
    protected Injector getInjector()
    {
        return Guice.createInjector(Stage.PRODUCTION, new ComponentModule(), new WebserviceModule());
    }


    public static class ComponentModule extends AbstractModule
    {
        @Override
        protected void configure()
        {
            Properties props = ConfigurationLoader.loadForName(COMPONENT_NAME);
            bindProperties(binder(), props);

            // A previous version of this component used the property
            // 'security' to determine what type of security to use (e.g. none, DGWS, Two-way-SSL).
            // To avoid the operator (Netic) to have to change deployment (i.e. puppet scripts) we
            // still support this property.

            String useTestSTS = "dgwsTest".equalsIgnoreCase(props.getProperty("security")) ? "true" : "false";
            bindConstant().annotatedWith(Names.named(DenGodeWebServiceFilter.USE_TEST_FEDERATION_PARAMETER)).to(useTestSTS);
            
            install(new ViewModule());
            install(new PersistenceModule());
            install(new AuthenticationModule());
        }
    }


    private class WebserviceModule extends ServletModule
    {
        @Override
        protected void configureServlets()
        {
            String ALL_EXCEPT_STATUS_PAGE = "(?!/status)/.*";
            
            filterRegex(ALL_EXCEPT_STATUS_PAGE).through(DenGodeWebServiceFilter.class);

            filterRegex("/.*").through(PersistenceFilter.class);
            filterRegex(ALL_EXCEPT_STATUS_PAGE).through(StatelessPersistenceFilter.class);
            
            bind(ComponentMonitor.class).to(ComponentMonitorImpl.class);
            install(new MonitoringModule());
            
            // Logging
            
            // The mandatory SLA filter.
            //
            bind(SLALoggingServletFilter.class).in(Scopes.SINGLETON);
            filterRegex(ALL_EXCEPT_STATUS_PAGE).through(SLALoggingServletFilter.class);
            
            // Inserts IP and other goodies into the MDC.
            //
            bind(MDCInsertingServletFilter.class).in(Scopes.SINGLETON);
            filterRegex(ALL_EXCEPT_STATUS_PAGE).through(MDCInsertingServletFilter.class);
            
            // Security
            
            install(new DenGodeWebServiceModule());
        }
    }
}
