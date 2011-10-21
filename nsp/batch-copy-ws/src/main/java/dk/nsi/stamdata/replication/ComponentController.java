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

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.ConfigurationLoader;
import com.trifork.stamdata.persistence.PersistenceModule;

import dk.nsi.dgws.DenGodeWebServiceFilter;
import dk.nsi.stamdata.replication.webservice.RegistryModule;
import dk.nsi.stamdata.views.ViewModule;


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

            // A previous version on this component used the property
            // 'security' to determine what type of security to use.
            // To avoid the operator to have to change deployment we
            // still support this property.

            if ("dgwsTest".equalsIgnoreCase(props.getProperty("security")))
            {
                bindConstant().annotatedWith(Names.named(DenGodeWebServiceFilter.USE_TEST_FEDERATION_PARAMETER)).to("true");
            }
            
            install(new ViewModule());

            install(new PersistenceModule());
            install(new RegistryModule());
        }
    }


    private class WebserviceModule extends ServletModule
    {
        @Override
        protected void configureServlets()
        {
            //filterRegex("(?!/status)/.*").through(DenGodeWebServiceFilter.class);
            //filterRegex("/*").through(PersistenceFilter.class);
            //filterRegex("(?!/status|/authenticate)/.*").through(StatelessPersistenceFilter.class);
            
            //bind(ComponentMonitor.class).to(ComponentMonitorImpl.class);
            //install(new MonitoringModule());
            
            //serveRegex("/.*/.*/v.*").with(ViewServlet.class);
            
            //bind(SLALoggingServletFilter.class).in(Scopes.SINGLETON);
            //filter("/authenticate").through(SLALoggingServletFilter.class);
        }
    }
}
