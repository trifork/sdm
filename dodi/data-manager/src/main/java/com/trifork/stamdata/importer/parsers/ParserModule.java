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
package com.trifork.stamdata.importer.parsers;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.trifork.stamdata.importer.config.ConnectionManager;
import com.trifork.stamdata.importer.config.OwnerIdentifier;
import com.trifork.stamdata.importer.config.KeyValueStore;
import com.trifork.stamdata.importer.config.MySqlKeyValueStore;
import com.trifork.stamdata.importer.parsers.annotations.ParserScoped;
import org.joda.time.Instant;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Provides the bindings needed for a parser execution.
 *
 * Object in this module are all bound in {@link ParserScope}.
 *
 * @author Thomas Børlum <thb@trifork.com>
 */
public class ParserModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        ParserScope parserScope = new ParserScope();

        // Tell Guice about the scope.
        //
        bindScope(ParserScoped.class, parserScope);

        // Make our scope instance injectable.
        //
        bind(ParserScope.class).toInstance(parserScope);
        
        // The key value store is also MySQL.
        //
        bind(KeyValueStore.class).to(MySqlKeyValueStore.class);
        
        // TODO: At the moment everyone uses a Directory inbox.
        //
        bind(Inbox.class).to(DirectoryInbox.class).in(ParserScoped.class);

        bind(ParserContext.class)
                .toProvider(ParserScope.<ParserContext>seededKeyProvider())
                .in(ParserScoped.class);
    }

    @Provides
    @ParserScoped
    private Connection provideConnection() throws SQLException
    {
        // We want to use the same connection (transaction) for all the sub-modules
        // of a parser execution.
        //
        return new ConnectionManager().getConnection();
    }

    @Provides
    @ParserScoped
    private SAXParser provideSaxParser() throws SAXException, ParserConfigurationException
    {
        // Needed by YderregisterParser
        //
        return SAXParserFactory.newInstance().newSAXParser();
    }

    @Provides
    @OwnerIdentifier
    private String provideDataOwnerId(ParserContext context)
    {
        return context.identifier();
    }

    @Provides
    @ParserScoped
    private Parser provideParser(ParserContext context, Injector injector)
    {
        return injector.getInstance(context.getParserClass());
    }

    @Provides
    @ParserScoped
    private Instant provideTransactionTime()
    {
        return Instant.now();
    }
}
