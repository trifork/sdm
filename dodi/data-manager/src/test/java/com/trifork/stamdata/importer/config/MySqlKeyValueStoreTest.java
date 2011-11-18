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
package com.trifork.stamdata.importer.config;

import com.trifork.stamdata.importer.parsers.Parser;
import com.trifork.stamdata.importer.parsers.Parsers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MySqlKeyValueStoreTest
{
    private Connection connection;

    private final String owner1Id = "default_owner";
    private final String owner2Id = "other_owner";

    private MySqlKeyValueStore owner1Store;
    private MySqlKeyValueStore owner2Store;

    @Before
    public void setUp() throws Exception
    {
        connection = new ConnectionManager().getConnection();

        owner1Store = new MySqlKeyValueStore(owner1Id, connection);
        owner2Store = new MySqlKeyValueStore(owner2Id, connection);
    }

    @After
    public void tearDown() throws Exception
    {
        connection.rollback();
        connection.close();
    }

    @Test
    public void testPutValueForKeyGetsStoredInTheDatabaseForTheCorrectOwner() throws SQLException
    {
        owner1Store.put("foo", "bar");

        String storedValue = owner1Store.get("foo");

        assertEquals("bar", storedValue);
    }

    @Test
    public void testPutStoresTheValueForTheCorrectOwner() throws SQLException
    {
        String key = "foo";

        owner1Store.put(key, "fez");
        owner2Store.put(key, "baz");

        String value1 = owner1Store.get(key);
        String value2 = owner2Store.get(key);

        assertThat(value1, is("fez"));
        assertThat(value2, is("baz"));
    }

    @Test
    public void testThatStoringNullInAKeyDeletesTheRowInTheDatabase() throws SQLException
    {
        owner1Store.put("foo", "bar");
        owner1Store.put("foo", null);

        String value = owner1Store.get("foo");

        assertThat(value, is(nullValue()));
    }

    @Test
    public void testThatGettingANonExistingKeyReturnsNullAndDoesNotThrowAnException()
    {
        String value = owner1Store.get("foo");

        assertThat(value, is(nullValue()));
    }
}
