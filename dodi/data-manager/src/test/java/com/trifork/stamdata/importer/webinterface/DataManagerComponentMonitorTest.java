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

import com.trifork.stamdata.importer.config.ConnectionManager;
import com.trifork.stamdata.importer.parsers.ParserScheduler;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DataManagerComponentMonitorTest
{
    private DataManagerComponentMonitor monitor;
    private JobsDecorator jobs;
    private ConnectionManager dbStatus;
    private ParserScheduler scheduler;

    @Before
    public void setUp() throws Exception
    {
        jobs = mock(JobsDecorator.class);
        dbStatus = mock(ConnectionManager.class);
        scheduler = mock(ParserScheduler.class);

        when(jobs.areAnyJobsOverdue()).thenReturn(false);
        when(jobs.areAllJobsRunning()).thenReturn(true);
        when(dbStatus.isAvailable()).thenReturn(true);
        when(scheduler.isOk()).thenReturn(true);
        
        monitor = new DataManagerComponentMonitor(dbStatus, jobs, scheduler);
    }
    
    @Test
    public void shouldReturnTrueIfAllIsWell()
    {
        assertTrue(monitor.isOk());
    }

    @Test
    public void shouldReturnFalseIfSchedulerHasFailed()
    {
        when(scheduler.isOk()).thenReturn(false);
        assertFalse(monitor.isOk());
    }

    @Test
    public void shouldReturnFalseIfNoDbIsDown()
    {
        when(dbStatus.isAvailable()).thenReturn(false);
        assertFalse(monitor.isOk());
    }

    @Test
    public void shouldReturnFalseIfJobsHaveFailed()
    {
        when(jobs.areAllJobsRunning()).thenReturn(false);
        assertFalse(monitor.isOk());
    }
    
    @Test
    public void shouldReturnFalseIfJobsAreOverdue()
    {
        when(jobs.areAnyJobsOverdue()).thenReturn(true);
        assertFalse(monitor.isOk());
    }
}
