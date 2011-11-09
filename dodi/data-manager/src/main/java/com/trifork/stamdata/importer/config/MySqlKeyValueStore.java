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

import static java.lang.String.format;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Inject;

import com.trifork.stamdata.Preconditions;

public class MySqlKeyValueStore implements KeyValueStore
{

    private static final int DB_FIELD_SIZE = 200;

    private final Connection connection;
    private String ownerId;

    /**
     * Creates a new instance.
     * 
     * @precondition The connection must have an active transaction.
     * 
     * @param connection
     *            The connection to use for fetching and storing.
     * @param ownerId
     *            The id of the owner, for instance a parser id.
     * @throws SQLException
     *             Thrown if the database is unreachable.
     */
    @Inject
    MySqlKeyValueStore(Connection connection, String ownerId) throws SQLException
    {
        this.connection = Preconditions.checkNotNull(connection, "connection");
        Preconditions.checkArgument(connection.getTransactionIsolation() != Connection.TRANSACTION_NONE, "The connection must have an open transaction.");

        Preconditions.checkNotNull(ownerId, "ownerId");
        Preconditions.checkArgument(ownerId.length() <= DB_FIELD_SIZE, "owerId can max be 200 characters.");

        this.ownerId = Preconditions.checkNotNull(ownerId);
    }

    @Override
    public String get(String key)
    {
        Preconditions.checkNotNull(key, "key");

        try
        {
            ResultSet rs = connection.createStatement().executeQuery(format("SELECT value FROM KeyValueStore WHERE ownerId = `%s` AND key = `%s`", ownerId, key));
            return rs.next() ? rs.getString(1) : null;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error accessing the MySQL key value store.", e);
        }
    }

    @Override
    public void put(String key, String value)
    {
        Preconditions.checkNotNull(key, "key");
        Preconditions.checkArgument(key.length() <= DB_FIELD_SIZE, "key can max be 200 characters.");
        try
        {
            if (value != null)
            {
                Preconditions.checkArgument(value.length() <= DB_FIELD_SIZE, "value can max be 200 characters.");

                connection.createStatement().execute(format("INSERT INTO KeyValueStore (owerId, key, value) VALUES ('%s','%s', '%s')", ownerId, key, value));
            } else
            {
                connection.createStatement().executeUpdate(format("DELETE FROM KeyValueStore WHERE ownerId = '%s' AND key = '%s'", ownerId, key));
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error accessing the MySQL key value store.", e);
        }
    }

    public void remove(String key)
    {
        Preconditions.checkNotNull(key, "key");
    }
}
