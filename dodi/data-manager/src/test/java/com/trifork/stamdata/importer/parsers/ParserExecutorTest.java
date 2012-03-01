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

import com.trifork.stamdata.importer.config.ConnectionManager;
import com.trifork.stamdata.importer.config.KeyValueStore;
import com.trifork.stamdata.importer.parsers.annotations.ParserInformation;
import com.trifork.stamdata.persistence.RecordPersister;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.sql.Connection;
import java.text.DateFormat;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Parsers.class)
public class ParserExecutorTest
{
    private ParserExecutor executor;
    
    private Parser parser;
    private Inbox inbox;
    private Connection connection;
    private ParseTimeManager timeManager;
    private RecordPersister persister;
    
    private File RANDOM_FILE = new File("random");
    private ParserContext context;

    @Before
    public void setUp() throws Exception
    {
        parser = mock(Parser.class);
        mockStatic(Parsers.class);
        when(Parsers.getIdentifier(any(Parser.class))).thenReturn("foo");
        when(Parsers.getIdentifier(any(Class.class))).thenReturn("foo");
        when(Parsers.getName(any(Class.class))).thenReturn("Foo");
        
        inbox = mock(Inbox.class);
        when(inbox.top()).thenReturn(RANDOM_FILE);
        
        persister = mock(RecordPersister.class);

        connection = mock(Connection.class);
        timeManager = mock(ParseTimeManager.class);

        context = mock(ParserContext.class);

        executor = new ParserExecutor(parser, inbox, connection, timeManager, persister, context);
    }

    @Test
    public void shouldLockAParserIfItFails() throws Exception
    {
        doThrow(new RuntimeException()).when(parser).process(any(File.class), any(RecordPersister.class));

        executor.run();

        verify(inbox).lock();
    }

    @Test
    public void shouldSetTheIsRunningFlagOnTheContextIfTheInboxIsNotEmpty()
    {
        // TODO
    }

    @Test
    public void shouldNotSetTheIsRunningFlagOnTheContextIfTheInboxIsEmpty()
    {
        // TODO
    }

    @Test
    public void shouldRollbackIfItFails() throws Exception
    {
        doThrow(new RuntimeException()).when(parser).process(any(File.class), any(RecordPersister.class));

        executor.run();

        verify(connection).rollback();
        verify(inbox, times(0)).advance();
    }

    @Test
    public void shouldNotAttemptExecutionIfTheInboxIsLocked() throws Exception
    {
        when(inbox.isLocked()).thenReturn(true);

        executor.run();

        verify(parser, times(0)).process(any(File.class), any(RecordPersister.class));
    }

    @Test
    public void shouldNotAttemptExecutionIfTheInboxIsEmpty() throws Exception
    {
        when(inbox.top()).thenReturn(null);

        executor.run();

        verify(parser, times(0)).process(any(File.class), any(RecordPersister.class));
    }

    @Test
    public void shouldCallUpdateBeforeCheckingTheInboxForItems() throws Exception
    {
        executor.run();

        InOrder inOrder = inOrder(inbox);

        inOrder.verify(inbox).update();
        inOrder.verify(inbox).top();
    }

    @Test
    public void shouldCallProcessOnParserBeforeAdvancingTheInbox() throws Exception
    {
        // If this is not done, we might not get things saved in the database before
        // deleting the item.
        //
        executor.run();

        InOrder inOrder = inOrder(connection, inbox);

        inOrder.verify(connection).commit();
        inOrder.verify(inbox).advance();
    }

    @Test
    public void shouldSetTheLatestRunTimestampIfItActuallyRan() throws Exception
    {
        executor.run();

        verify(timeManager).update();
    }

    @Test
    public void shouldNotSetTheLatestRunTimestampIfItDidNotRun() throws Exception
    {
        when(inbox.top()).thenReturn(null);

        executor.run();

        verifyZeroInteractions(timeManager);
    }
}
