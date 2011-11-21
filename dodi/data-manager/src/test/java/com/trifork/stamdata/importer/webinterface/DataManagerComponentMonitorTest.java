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

import com.google.common.collect.Sets;
import com.trifork.stamdata.importer.config.ConnectionManager;
import com.trifork.stamdata.importer.jobs.JobManager;
import com.trifork.stamdata.importer.parsers.ParserScheduler;
import com.trifork.stamdata.importer.parsers.ParserState;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DataManagerComponentMonitorTest
{
    private Set<ParserState> parsers;
    private DataManagerComponentMonitor monitor;
    private ConnectionManager connectionManager;
    private ParserScheduler scheduler;
    private JobManager manager;

    @Before
    public void setUp() throws Exception
    {
        ParserState parser = mock(ParserState.class);
        when(parser.isLocked()).thenReturn(false);
        
        parsers = Sets.newHashSet();
        parsers.add(parser);
        
        connectionManager = mock(ConnectionManager.class);
        when(connectionManager.isAvailable()).thenReturn(true);

        scheduler = mock(ParserScheduler.class);
        when(scheduler.isOk()).thenReturn(true);

        when(scheduler.isOk()).thenReturn(true);
        
        manager = mock(JobManager.class);
        when(manager.isOk()).thenReturn(true);
        
        monitor = new DataManagerComponentMonitor(connectionManager, parsers, scheduler, manager);
    }
    
    @Test
    public void shouldReturnTrueIfAllIsWell()
    {
        assertThat(monitor.isOk(), is(true));
    }

    @Test
    public void shouldReturnFalseIfSchedulerHasFailed()
    {
        when(scheduler.isOk()).thenReturn(false);

        assertThat(monitor.isOk(), is(false));
    }

    @Test
    public void shouldReturnFalseIfNoDbIsDown()
    {
        when(connectionManager.isAvailable()).thenReturn(false);

        assertThat(monitor.isOk(), is(false));
    }

    @Test
    public void shouldReturnFalseIfAnyParsersAreLocked()
    {
        ParserState parser = mock(ParserState.class);
        when(parser.isLocked()).thenReturn(true);
        parsers.add(parser);

        assertThat(monitor.isOk(), is(false));
    }
    
    @Test
    public void shouldReturnFalseIfJobsAreOverdue()
    {
        ParserState parser = mock(ParserState.class);
        when(parser.isOverdue()).thenReturn(true);
        parsers.add(parser);

        assertThat(monitor.isOk(), is(false));
    }
}
