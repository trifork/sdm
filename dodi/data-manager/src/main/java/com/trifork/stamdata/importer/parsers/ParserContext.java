package com.trifork.stamdata.importer.parsers;

import com.google.inject.Injector;
import com.trifork.stamdata.importer.jobs.DirectoryInbox;
import com.trifork.stamdata.importer.jobs.ImportTimeManager;
import com.trifork.stamdata.importer.jobs.Inbox;
import com.trifork.stamdata.importer.parsers.Parser;
import org.joda.time.DateTime;

public class ParserContext
{
    private Class<? extends Parser> parserClass;
    private int minimumImportFrequency;

    public ParserContext(Class<? extends Parser> parserClass, int minimumImportFrequency)
    {
        this.parserClass = parserClass;
        this.minimumImportFrequency = minimumImportFrequency;
    }

    public Class<? extends Parser> getParserClass()
    {
        return parserClass;
    }

    public int getMinimumImportFrequency()
    {
        return minimumImportFrequency;
    }

    /**
     * Indicated whether a file delivery is overdue.
     *
     * If no files have previously been imported, this method always returns
     * false.
     *
     * @return true if the parser expected files but has not received any.
     */
    public boolean isOverdue()
    {
        return hasBeenRun() && getNextDeadline().isBeforeNow();
    }

    /**
     * The deadline for when the next files have to have been imported.
     *
     * The returned date will always be at midnight to avoid the day of time
     * slipping everytime a new batch is imported.
     *
     * @return the timestamp with the deadline.
     */
    public DateTime getNextDeadline()
    {
        return getLatestRunTime().plusDays(minimumImportFrequency).toDateMidnight().toDateTime();
    }

    public DateTime getLatestRunTime()
    {
        return ImportTimeManager.getLastImportTime(identifier());
    }

    public boolean hasBeenRun()
    {
        return getLatestRunTime() != null;
    }

    public String identifier()
    {
        return Parsers.getIdentifier(getParserClass());
    }

    public boolean isOK(Injector injector)
    {
        // HACK: The inbox should not be here. Make a ParserManager class that knows stuff like this.

        Injector childInjector = injector.createChildInjector(ParserModule.using(this));
        Inbox inbox = childInjector.getInstance(Inbox.class);
        
        return !inbox.isLocked();
    }

    public String getHumanName()
    {
        return Parsers.getName(parserClass);
    }
}
