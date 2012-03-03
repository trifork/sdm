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

import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.Nullable;
import com.trifork.stamdata.importer.config.ConfigurationLoader;
import com.trifork.stamdata.importer.jobs.JobManager;
import com.trifork.stamdata.importer.parsers.ParserModule;
import com.trifork.stamdata.importer.parsers.ParserScheduler;
import com.trifork.stamdata.importer.parsers.annotations.InboxRootPath;
import com.trifork.stamdata.persistence.Persistent;
import dk.sdsd.nsp.slalog.api.SLALogConfig;
import dk.sdsd.nsp.slalog.api.SLALogger;
import org.apache.commons.configuration.CompositeConfiguration;
import org.hibernate.SessionFactory;

import javax.inject.Named;
import java.io.File;
import java.util.Set;

import static com.trifork.stamdata.importer.config.ParserConfiguration.bindParsers;

/**
 * @author Thomas Børlum <thb@trifork.com>
 */
public class ComponentModule extends ServletModule
{
    @Override
    protected void configureServlets()
    {
        final CompositeConfiguration config = ConfigurationLoader.loadConfiguration();
        
        // HACK: Because we are not using the shared ConfigurationLoader (yet!) we cannot easily bind properties
        // to named constants.
        //
        String rootDir = config.getString("rootDir");
        
        bindConstant().annotatedWith(InboxRootPath.class).to(rootDir);
        bindConstant().annotatedWith(Names.named("file.stabilization.period")).to(config.getInt("file.stabilization.period"));

        // Bind the configured parsers.
        // TODO: The parsers' classes should not be named in the configuration file.
        //
        bindParsers(config, new File(rootDir), binder());

        install(new ParserModule());

        // Bind the services.
        //
        bind(JobManager.class).in(Scopes.SINGLETON);
        bind(ParserScheduler.class).in(Scopes.SINGLETON);
    }
}
