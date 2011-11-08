package com.trifork.stamdata.importer.jobs;

import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Callables;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Injector;
import com.trifork.stamdata.importer.parsers.Parser;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.*;

/**
 * New implementation of job manager for Sikrede.
 * All new parsers should use this.
 */
public class JobScheduler
{
    private final ExecutorService schedulerExecutor;
    private final ExecutorService jobExecutor;

    private final Iterable<Class<? extends Parser>> parsers;
    private final ConcurrentLinkedQueue<Class<? extends Parser>> inProgress;

    private final Injector injector;

    @Inject
    JobScheduler(Injector injector)
    {
        this.injector = injector;

        // There are two collections for parsers. One with parsers that are
        // in progress and the other for a list of all parsers.
        //
        parsers = new LinkedList();
        inProgress = new ConcurrentLinkedQueue();

        // We only need a single thread to check for new files to import.
        //
        schedulerExecutor = Executors.newSingleThreadExecutor();

        // There is no particular reason why this number is set like this.
        // Good rule of thumb is to use the number of cores plus one.
        //
        int numberOfThreads = Runtime.getRuntime().availableProcessors() + 1;
        jobExecutor = Executors.newFixedThreadPool(numberOfThreads);
    }

    public void start()
    {
        // The scheduler's loop should run as often as possible.
        //
        final int INITIAL_DELAY = 0;
        final int DELAY_BETWEEN_CHECKS = 0;
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
                // TODO: There is a tiny chance of 'live locking' here.

                for (final Class<? extends Parser> parser : Iterables.cycle(parsers))
                {
                    if (inProgress.contains(parser)) continue;
                    inProgress.add(parser);

                    executeParser(parser).addListener(freeParser(parser), MoreExecutors.sameThreadExecutor());
                }
            }
        };
    }

    private Runnable freeParser(final Class<? extends Parser> parser)
    {
        return new Runnable()
        {
            @Override
            public void run()
            {
                inProgress.remove(parser);
            }
        };
    }

    private ListenableFuture executeParser(Class<? extends Parser> parserClass)
    {
        Injector parserInjector = injector.createChildInjector(ParserModule.using(parserClass));

        ParserExecutor executor = parserInjector.getInstance(ParserExecutor.class);

        ListenableFutureTask<Void> task = new ListenableFutureTask(executor, null);
        jobExecutor.submit(task);
        return task;
    }
}
