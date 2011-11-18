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
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.trifork.stamdata.importer.config.ConnectionManager;
import com.trifork.stamdata.importer.config.DataOwnerId;
import com.trifork.stamdata.importer.config.KeyValueStore;
import com.trifork.stamdata.importer.config.MySqlKeyValueStore;
import org.joda.time.Instant;
import org.xml.sax.SAXException;

import javax.inject.Singleton;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.sql.Connection;
import java.sql.SQLException;

import static com.trifork.stamdata.Preconditions.checkNotNull;

/**
 * @author Thomas Børlum <thb@trifork.com>
 */
public class ParserModule extends AbstractModule
{
    private final ParserContext parserContext;

    public ParserModule(ParserContext parserContext)
    {
        this.parserContext = checkNotNull(parserContext, "parserContext");
    }

    public static ParserModule using(ParserContext parserClass)
    {
        return new ParserModule(parserClass);
    }

    @Override
    protected void configure()
    {
        bind(ParserContext.class).toInstance(parserContext);
        bindConstant().annotatedWith(DataOwnerId.class).to(parserContext.identifier());

        bind(Parser.class).to(parserContext.getParserClass()).in(Scopes.SINGLETON);
        bind(Inbox.class).to(DirectoryInbox.class).in(Scopes.SINGLETON);

        bind(KeyValueStore.class).to(MySqlKeyValueStore.class);

        bind(Instant.class).toInstance(Instant.now());
    }

    @Provides
    @Singleton // Singleton because this module is only used in per parser injectors.
    private Connection provideConnection() throws SQLException
    {
        // We want to use the same connection (transaction) for all the sub-modules
        // of a parser execution.

        return new ConnectionManager().getConnection();
    }

    @Provides // Needed by YderregisterParser
    private SAXParser provideSaxParser() throws SAXException, ParserConfigurationException
    {
        return SAXParserFactory.newInstance().newSAXParser();
    }
}
