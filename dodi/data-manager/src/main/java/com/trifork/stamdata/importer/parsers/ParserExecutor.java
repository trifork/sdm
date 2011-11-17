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

import com.trifork.stamdata.importer.jobs.Inbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Pushes data sets to the parsers from their inbox and works as a safety net for the parser.
 * 
 * Parser executors will check for ready data sets in the parsers inbox and if any are ready
 * start a transaction.
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *     <li>Check for data and pass it to the parser.</li>
 *     <li>Start and end database transactions.</li>
 *     <li>Set appropriate logging parameters for the parser.</li>
 *     <li>Catch any exceptions thrown during execution and report the error.</li>
 * </ul>
 */
public class ParserExecutor implements Runnable
{
    private static final Logger logger = LoggerFactory.getLogger(ParserExecutor.class);
    
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
        // Push the logging context.
        //
        Map<String, String> loggingContext = MDC.getCopyOfContextMap();
        
        // It is important that we catch everything
        // so we know if an import goes pare-shaped.
        //
        // If so we can rollback the transaction and
        // report the error.
        //
        try
        {
            if (!inbox.isLocked()) execute();
        }
        catch (Exception e)
        {
            logger.error("Parser execution failed!", e);

            // Further attempts to run this parser will
            // result in noop.
            //
            inbox.lock();
        }
        finally
        {
            // Pop the logging context.
            //
            MDC.setContextMap(loggingContext);
        }
    }

    private void execute() throws IOException
    {
        MDC.put("parser", parser.identifier());

        inbox.update();

        File dataSet = checkInbox();

        if (dataSet != null)
        {
            MDC.put("data_set", dataSet.getName());
            logger.info("Executing parser.");

            parser.process(dataSet);

            // Once the import is complete
            // we can remove of the data set
            // from the inbox.
            //
            inbox.advance();

            logger.info("Import successful.");
        }
        else
        {
            logger.debug("Inbox empty.");
        }
    }

    private File checkInbox() throws IOException
    {
        inbox.update();
        return inbox.top();
    }
}
