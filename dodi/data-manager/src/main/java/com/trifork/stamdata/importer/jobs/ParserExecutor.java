package com.trifork.stamdata.importer.jobs;

import com.trifork.stamdata.importer.parsers.Parser;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

public class ParserExecutor implements Runnable
{
    private final Parser parser;
    private final Inbox inbox;

    @Inject
    ParserExecutor(Parser parser, Inbox inbox)
    {
        this.parser = parser;
        this.inbox = inbox;
    }

    @Override
    public void run()
    {
        try
        {

        }
        catch (Exception e)
        {
            
        }
    }

    private File checkInbox() throws IOException
    {
        inbox.update();

        // TODO: Log the size of the inbox, so we can see if it starts growing.

        return inbox.peek();
    }
}
