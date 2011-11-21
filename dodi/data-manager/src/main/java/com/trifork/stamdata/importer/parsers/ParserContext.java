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

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.trifork.stamdata.importer.jobs.ImportTimeManager;
import org.joda.time.DateTime;

public class ParserContext implements ParserState
{
    @Inject
    private ParserScope scope;
    
    @Inject
    private Provider<Inbox> inboxProvider;

    private Class<? extends Parser> parserClass;
    private int minimumImportFrequency;
    private boolean isRunning = false;

    public ParserContext(Class<? extends Parser> parserClass, int minimumImportFrequency)
    {
        this.parserClass = parserClass;
        this.minimumImportFrequency = minimumImportFrequency;
    }

    public Class<? extends Parser> getParserClass()
    {
        return parserClass;
    }

    @Override
    public int minimumImportFrequency()
    {
        return minimumImportFrequency;
    }

    @Override
    public boolean isOverdue()
    {
        return hasBeenRun() && nextDeadline().isBeforeNow();
    }

    @Override
    public DateTime nextDeadline()
    {
        return latestRunTime().plusDays(minimumImportFrequency).toDateMidnight().toDateTime();
    }

    @Override
    public DateTime latestRunTime()
    {
        return ImportTimeManager.getLastImportTime(identifier());
    }

    @Override
    public boolean hasBeenRun()
    {
        return latestRunTime() != null;
    }

    @Override
    public String identifier()
    {
        return Parsers.getIdentifier(getParserClass());
    }

    @Override
    public boolean isLocked()
    {
        scope.enter(this);
        
        try
        {
            Inbox inbox = inboxProvider.get();
            return inbox.isLocked();
        }
        finally
        {
            scope.exit();    
        }
    }

    @Override
    public String name()
    {
        return Parsers.getName(parserClass);
    }

    @Override
    public boolean isInProgress()
    {
        return isRunning;
    }

    public void isInProgress(boolean isRunning)
    {
        this.isRunning = isRunning;
    }
}
