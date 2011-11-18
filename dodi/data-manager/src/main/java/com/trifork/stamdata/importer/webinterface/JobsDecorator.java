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
package com.trifork.stamdata.importer.webinterface;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.trifork.stamdata.importer.jobs.Job;
import com.trifork.stamdata.importer.jobs.JobManager;
import com.trifork.stamdata.importer.parsers.ParserContext;
import com.trifork.stamdata.importer.parsers.ParserScheduler;
import org.joda.time.DateTime;

/**
 * Uses both the old job manager and the new job scheduler to
 * list all running jobs.
 */
public class JobsDecorator
{
    private final ParserScheduler scheduler;
    private final JobManager jobManager;

    @Inject
    JobsDecorator(ParserScheduler scheduler, JobManager jobManager) {

        this.scheduler = scheduler;
        this.jobManager = jobManager;
    }

    public Iterable<Job> getJobs()
    {
        return Iterables.concat(decoratedSchedulerJobs(), jobManager.getJobIterator());
    }
    
    private Iterable<Job> decoratedSchedulerJobs()
    {
        return Iterables.transform(scheduler.getParsers(), new Function<ParserContext, Job>()
        {
            @Override
            public Job apply(ParserContext input)
            {
                return new ParserContextDecorator(input);
            }
        });
    }

    public boolean areAnyJobsOverdue()
    {
        for (Job job : getJobs())
        {
            if (job.isOverdue()) return true;
        }

        return false;
    }

    public boolean areAllJobsRunning()
    {
        for (Job job : getJobs())
        {
            if (!job.isOK()) return false;
        }

        return true;
    }

    private class ParserContextDecorator implements Job
    {
        private final ParserContext context;

        public ParserContextDecorator(ParserContext context)
        {
            this.context = context;
        }

        @Override
        public String identifier()
        {
            return context.identifier();
        }

        @Override
        public String getHumanName()
        {
            return context.getHumanName();
        }

        @Override
        public boolean isOK()
        {
            return context.isOK();
        }

        @Override
        public boolean isOverdue()
        {
            return context.isOverdue();
        }

        @Override
        public DateTime getLatestRunTime()
        {
            return context.getLatestRunTime();
        }

        @Override
        public boolean hasBeenRun()
        {
            return getLatestRunTime() != null;
        }

        @Override
        public boolean isExecuting()
        {
            return context.isRunning();
        }

        @Override
        public void run()
        {
            // Noop this is just a decorator.
        }
    }
}
