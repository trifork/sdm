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

import static com.trifork.stamdata.importer.tools.SLALoggerHolder.getSLALogger;
import com.google.inject.Inject;
import com.trifork.stamdata.importer.config.ConnectionManager;
import com.trifork.stamdata.importer.parsers.annotations.ParserScoped;
import com.trifork.stamdata.persistence.RecordPersister;
import dk.sdsd.nsp.slalog.api.SLALogItem;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Pushes data sets to the parsers from their inbox and works as a safety net for the parser.
 * <p/>
 * Parser executors will check for ready data sets in the parsers inbox and if any are ready
 * start a transaction.
 * <p/>
 * <h2>Responsibilities</h2>
 * <ul>
 * <li>Check for data and pass it to the parser.</li>
 * <li>Start and end database transactions.</li>
 * <li>Set appropriate logging parameters for the parser.</li>
 * <li>Update the parsers' import time table.</li>
 * <li>Catch any exceptions thrown during execution and report the error.</li>
 * </ul>
 *
 * @author Thomas Børlum <thb@trifork.com>
 */
@ParserScoped
public class ParserExecutor implements Runnable {
    private static final Logger logger = Logger.getLogger(ParserExecutor.class);

    private final Parser parser;
    private final Inbox inbox;
    private final Connection connection;
    private final ParseTimeManager timeManager;
    private final RecordPersister persister;
    private final ParserContext context;

    @Inject
    ParserExecutor(Parser parser, Inbox inbox, Connection connection, ParseTimeManager timeManager, RecordPersister persister, ParserContext context) {
        this.parser = parser;
        this.inbox = inbox;
        this.connection = connection;
        this.timeManager = timeManager;
        this.persister = persister;
        this.context = context;
    }

    @Override
    public void run() {
        // Push the logging context.
        //
        Map<String, String> loggingContext = new HashMap<String, String>();
        Hashtable<String, String> startContext = MDC.getContext();
        if (startContext != null) {
            loggingContext.putAll(startContext);
        }

        // It is important that we catch everything
        // so we know if an import goes pare-shaped.
        //
        // If so we can rollback the transaction and
        // report the error.
        //
        try {
            if (!inbox.isLocked()) execute();
        } catch (Exception e) {
            logger.error("Parser execution failed!", e);

            ConnectionManager.rollbackQuietly(connection);

            // Further attempts to run this parser will
            // result in noop.
            //
            inbox.lock();
        } finally {
            // Make absolutely sure that the parser is not marked as running.
            //
            context.isInProgress(false);

            ConnectionManager.closeQuietly(connection);

            // Pop the logging context.
            // This might be null.
            //
            if (MDC.getContext() != null) MDC.getContext().clear();

            if (loggingContext != null) {
                //MDC.setContextMap(loggingContext);
                for (String key : loggingContext.keySet()) {
                    MDC.put(key, loggingContext.get(key));
                }
            }
        }
    }

    // TODO: A Circuit Breaker Guice interceptor here would make the system very robust.
    private void execute() throws Exception {
        String parserIdentifier = Parsers.getIdentifier(parser);
        MDC.put("parser", parserIdentifier);

        File dataSet = checkInbox();
        System.out.println("Inbox checked - DataSet: " + (dataSet != null ? StringUtils.join(dataSet.list(), ", ") : null));
        if (dataSet != null) {
        	System.out.println("Executing parser " + parserIdentifier + " -- " + parser.getClass().getCanonicalName());
            SLALogItem slaLogItem = getSLALogger().createLogItem("Executing parser " + parserIdentifier, parser.getClass().getCanonicalName());
            try {
            context.isInProgress(true);

            MDC.put("input", dataSet.getName());

            logger.info("Executing parser.");

            parser.process(dataSet, persister);

            timeManager.update();

            // It is important that we commit before
            // we advance the inbox. Since it is not done
            // in a transaction we must make sure that items
            // are actually stored before removing the item
            // from the inbox. If removing the item fails
            // the parser will complain the next time we try
            // to import the item.
            //
            connection.commit();

            // Once the import is complete
            // we can remove of the data set
            // from the inbox.
            //
            inbox.advance();

            logger.info("Import successful.");
                
                slaLogItem.setCallResultOk();
                slaLogItem.store();
                
            } catch (Exception e) {
                slaLogItem.setCallResultError("Parser " + parserIdentifier + " failed - Cause: " + e.getMessage());
                slaLogItem.store();

                throw e;
            }
        }
    }

    private File checkInbox() throws IOException {
        inbox.update();
        return inbox.top();
    }

    public Class<? extends Parser> parser() {
        return parser.getClass();
    }
}
