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


import com.google.inject.*;
import com.trifork.stamdata.importer.ComponentModule;
import com.trifork.stamdata.importer.config.OwnerIdentifier;
import com.trifork.stamdata.importer.config.KeyValueStore;
import org.joda.time.Instant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParserModuleTest
{
    private Injector injector;
    private ParserScope scope;
    private ParserContext context;

    @Before
    public void setUp()
    {
        context = mock(ParserContext.class);
        when(context.identifier()).thenReturn("foo");

        injector = Guice.createInjector(new ComponentModule());

        scope = injector.getInstance(ParserScope.class);
    }

    @After
    public void tearDown()
    {
        try { scope.exit(); } catch (Throwable t) { }
    }

    @Test
    public void testThatTransactionTimeIsBoundToASingleInstantInParserScope() throws InterruptedException
    {
        scope.enter(context);

        Instant time1 = injector.getInstance(Instant.class);

        Thread.sleep(2);

        Instant time2 = injector.getInstance(Instant.class);

        assertEquals(time1, time2);
    }

    @Test
    public void testThatASingleConnectionIsAvailableParserScope()
    {
        scope.enter(context);

        Connection connection1 = injector.getInstance(Connection.class);
        Connection connection2 = injector.getInstance(Connection.class);

        assertSame(connection1, connection2);
    }

    @Test(expected = ProvisionException.class)
    public void testThatAConnectionIsNotAvailableOutSideParserScope()
    {
        injector.getInstance(Connection.class);
    }

    @Test
    public void testThatAKeyValueStoreIsAvailableToTheParsers()
    {
        scope.enter(context);
        
        injector.getInstance(KeyValueStore.class);
    }
    
    @Test
    public void testThatTheDataOwnerIdIsBoundToTheParsersIdentifier()
    {
        when(context.identifier()).thenReturn("bar");
        
        scope.enter(context);
        
        String dataOwnerId = injector.getInstance(Key.get(String.class, OwnerIdentifier.class));
        
        assertThat(dataOwnerId, is("bar"));
    }
}
