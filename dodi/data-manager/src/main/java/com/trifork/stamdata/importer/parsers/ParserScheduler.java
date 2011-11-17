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
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.*;

/**
 * New implementation of job manager for Sikrede.
 * All new parsers should use this.
 */
public class ParserScheduler
{
    private static final Logger logger = LoggerFactory.getLogger(ParserScheduler.class);

    private final ExecutorService schedulerExecutor;
    private final ExecutorService jobExecutor;

    private final Iterable<ParserContext> parsers;
    private final ConcurrentLinkedQueue<Class<? extends Parser>> inProgress;

    private final Injector injector;

    private boolean hasFailed = false;

    @Inject
    ParserScheduler(Injector injector, Set<ParserContext> parsers)
    {
        this.injector = injector;

        // There are two collections for parsers. One with parsers that are
        // in advance and the other for a list of all parsers.
        //
        this.parsers = parsers;
        inProgress = new ConcurrentLinkedQueue();

        // We only need a single thread to check for new files to import.
        //
        schedulerExecutor = Executors.newSingleThreadExecutor();

        // There is no particular reason for the thread pool readyCount.
        // Good rule of thumb is to use the number of cores plus one.
        //
        int numberOfThreads = Runtime.getRuntime().availableProcessors() + 1;
        jobExecutor = Executors.newFixedThreadPool(numberOfThreads);
    }

    public void start()
    {
        // The scheduler loop should run as often as possible.
        //
        schedulerExecutor.submit(runLoop());
    }

    public void stop()
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
                catch (Exception e)
                {
                    hasFailed = true;
                    logger.error("The scheduler crashed. This is a fatal error. Execution has stopped.", e);
                }
            }
        };
    }

    private void loop()
    {
        // TODO: There is a tiny chance of 'live locking' here.
        // This could potentially starve some parsers.
        //
        for (ParserContext parser : Iterables.cycle(parsers))
        {
            // Only start parsers not already in advance.
            //
            if (inProgress.contains(parser.getParserClass())) continue;

            retain(parser);

            // Run the parser, and once it completes (success or otherwise)
            // release the parser to the waiting list.
            //
            ListenableFuture<Void> execution = executeParser(parser);

            execution.addListener(release(parser), MoreExecutors.sameThreadExecutor());
        }
    }

    private void retain(ParserContext parserContext)
    {
        inProgress.add(parserContext.getParserClass());
    }

    private ListenableFuture executeParser(ParserContext parserContext)
    {
        // Create a child injector for the parser to limit its scope.
        //
        Injector parserInjector = injector.createChildInjector(ParserModule.using(parserContext));

        ParserExecutor executor = parserInjector.getInstance(ParserExecutor.class);

        ListenableFutureTask<Void> task = new ListenableFutureTask(executor, null);

        jobExecutor.execute(task);

        return task;
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
        return !hasFailed;
    }
}
