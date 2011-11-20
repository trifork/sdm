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

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * A service that given a set parser configurations dispatches an executor for the parse.
 *
 * @author Thomas Børlum <thb@trifork.com>
 */
public class ParserScheduler
{
    private static final Logger logger = LoggerFactory.getLogger(ParserScheduler.class);

    private final ScheduledExecutorService schedulerExecutor;
    private final ExecutorService jobExecutor;

    private final ParserScope scope;

    private final Iterable<ParserContext> parsers;
    private final ConcurrentLinkedQueue<Class<? extends Parser>> inProgress;

    private final Provider<ParserExecutor> executors;

    @Inject
    ParserScheduler(Provider<ParserExecutor> executors, Set<ParserContext> parsers, ParserScope scope)
    {
        this.executors = executors;
        this.scope = scope;

        // There are two collections for parsers. One with parsers that are
        // in advance and the other for a list of all parsers.
        //
        this.parsers = parsers;
        inProgress = new ConcurrentLinkedQueue();

        // We only need a single thread to check for new files to import.
        //
        schedulerExecutor = Executors.newSingleThreadScheduledExecutor();

        // There is no particular reason for the thread pool readyCount.
        // Good rule of thumb is to use the number of cores plus one.
        //
        int numberOfThreads = Runtime.getRuntime().availableProcessors() + 1;
        jobExecutor = Executors.newFixedThreadPool(numberOfThreads);
    }

    public void start()
    {
        // The scheduler loop quite often.
        // Don't run continuously or we'll hog a lot of CPU.
        //
        schedulerExecutor.scheduleWithFixedDelay(runLoop(), 0, 1, TimeUnit.SECONDS);
    }

    public void stop()
    {
        shutdown();
    }

    private void shutdown()
    {
        // Don't schedule any more jobs.
        //
        schedulerExecutor.shutdownNow();

        // Let the currently running jobs try and finish.
        //
        jobExecutor.shutdownNow();
    }

    private Runnable runLoop()
    {
        return new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    loop();
                }
                catch (Throwable t)
                {
                    // Parser exceptions such as input errors should never
                    // make it out here. If something does it must be a fatal
                    // error, and we stop execution.
                    //
                    shutdown();

                    logger.error("The scheduler crashed. This is a fatal error. Execution has stopped.",  t);
                }
            }
        };
    }

    private void loop()
    {
        // TODO: There is a tiny chance of 'live locking' here.
        // This could potentially starve some parsers.
        //
        for (ParserContext parser : parsers)
        {
            // Only start parsers not already in progress.
            //
            if (inProgress.contains(parser.getParserClass()))
            {
                continue;
            }

            retain(parser);

            // Run the parser, and once it completes (success or otherwise)
            // release the parser to the waiting list.
            //
            ListenableFuture<Void> execution = executeParser(parser);

            execution.addListener(release(parser), MoreExecutors.sameThreadExecutor());
        }
    }

    private ListenableFuture executeParser(ParserContext parserContext)
    {
        // Each parser is executed in its own scope. Thus any shared instances
        // will be available only to this parser and only for this execution.
        //
        scope.enter(parserContext);

        try
        {
            ListenableFutureTask<Void> task = new ListenableFutureTask(executors.get(), null);

            jobExecutor.execute(task);

            return task;
        }
        finally
        {
            scope.exit();
        }
    }

    private void retain(ParserContext parserContext)
    {
        inProgress.add(parserContext.getParserClass());
    }

    private Runnable release(final ParserContext parserContext)
    {
        return new Runnable()
        {
            @Override
            public void run()
            {
                // Remove the parser so it is no longer running.
                //
                inProgress.remove(parserContext.getParserClass());
            }
        };
    }

    /**
     * Gets all scheduled parsers.
     *
     * @return a possibly empty set of all parsers.
     */
    public Set<ParserContext> getParsers()
    {
        return Sets.newHashSet(parsers);
    }

    public boolean isOk()
    {
        return !schedulerExecutor.isTerminated();
    }
}
