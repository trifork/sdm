package com.trifork.stamdata.importer.parsers;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.ScopeAnnotation;
import com.trifork.stamdata.importer.config.ConnectionManager;
import com.trifork.stamdata.importer.config.DataOwnerId;
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
    @DataOwnerId
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
