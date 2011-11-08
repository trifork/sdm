package com.trifork.stamdata.importer.jobs;

import com.google.inject.AbstractModule;
import com.trifork.stamdata.importer.parsers.Parser;

public class ParserModule extends AbstractModule
{
    private final Class<? extends Parser> parserClass;

    public ParserModule(Class<? extends Parser> parserClass)
    {
        this.parserClass = parserClass;
    }

    @Override
    protected void configure()
    {
        bind(Parser.class).to(parserClass);
        bind(Inbox.class).to(DirectoryInbox.class);
    }

    public static ParserModule using(Class<? extends Parser> parserClass)
    {
        return new ParserModule(parserClass);
    }
}
