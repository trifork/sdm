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
package com.trifork.stamdata.importer;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.ComponentMonitor;
import com.trifork.stamdata.MonitoringModule;
import com.trifork.stamdata.importer.config.ConfigurationLoader;
import com.trifork.stamdata.importer.config.OldParserContext;
import com.trifork.stamdata.importer.config.ParserConfiguration;
import com.trifork.stamdata.importer.jobs.JobManager;
import com.trifork.stamdata.importer.parsers.ParserContext;
import com.trifork.stamdata.importer.parsers.ParserScheduler;
import com.trifork.stamdata.importer.parsers.ParserModule;
import com.trifork.stamdata.importer.webinterface.DataManagerComponentMonitor;
import com.trifork.stamdata.importer.webinterface.GUIServlet;
import org.apache.commons.configuration.CompositeConfiguration;

import java.util.Set;

import static com.trifork.stamdata.importer.config.ParserConfiguration.bindOldParsers;
import static com.trifork.stamdata.importer.config.ParserConfiguration.bindParsers;

/**
 * @author Thomas Børlum <thb@trifork.com>
 */
public class ComponentModule extends ServletModule
{
    @Override
    protected void configureServlets()
    {
        install(new ParserModule());

        final CompositeConfiguration config = ConfigurationLoader.loadConfiguration();

        // Bind the configured parsers.
        // TODO: The parsers' classes should not be named in the configuration file.
        //
        bindParsers(config, binder());
        bindOldParsers(config, binder());

        // HACK: Because we are not using the shared ConfigurationLoader (yet!) we cannot easily bind properties
        // to named constants.
        //
        bindConstant().annotatedWith(Names.named("rootDir")).to(config.getString("rootDir"));
        bindConstant().annotatedWith(Names.named("file.stabilization.period")).to(config.getInt("file.stabilization.period"));

        // Serve the status servlet.
        //
        bind(ComponentMonitor.class).to(DataManagerComponentMonitor.class);
        install(new MonitoringModule());

        serve("/").with(GUIServlet.class);

        // Bind the services.
        //
        bind(JobManager.class).in(Scopes.SINGLETON);
        bind(ParserScheduler.class).in(Scopes.SINGLETON);
    }
}
