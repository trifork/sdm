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

import static com.trifork.stamdata.Preconditions.checkArgument;
import static com.trifork.stamdata.Preconditions.checkNotNull;
import static java.lang.String.format;

import java.sql.*;


import com.google.inject.Inject;
import com.trifork.stamdata.Nullable;

/**
 * A key value store that uses MySQL to as backend.
 *
 * @author Thomas Børlum <thb@trifork.com>
 */
public class MySqlKeyValueStore implements KeyValueStore
{
    private static final int DB_FIELD_SIZE = 200;

    private final String dataOwnerId;
    private final Connection connection;

    /**
     * Creates a new instance.
     * 
     * @precondition The connection must have an active transaction.
     * 
     * @param connection
     *            The connection to use for fetching and storing.
     * @param dataOwnerId
     *            An identifier that identifies the owner of the stored data.
     * @throws SQLException
     *             Thrown if the database is unreachable.
     */
    @Inject
    MySqlKeyValueStore(@DataOwnerId String dataOwnerId, Connection connection) throws SQLException
    {
        this.connection = checkNotNull(connection, "connection");
        checkArgument(connection.getTransactionIsolation() != Connection.TRANSACTION_NONE, "The connection must have an active transaction.");

        this.dataOwnerId = checkNotNull(dataOwnerId, "dataOwnerId");
        checkArgument(this.dataOwnerId.length() <= DB_FIELD_SIZE, "The parser's id can max be 200 characters.");
    }

    @Override
    public String get(String key)
    {
        checkNotNull(key, "key");
        checkArgument(key.length() <= DB_FIELD_SIZE, String.format("Keys can be a maximum of %d characters long.", DB_FIELD_SIZE));

        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT value FROM KeyValueStore WHERE ownerId = ? AND id = ?");
            statement.setObject(1, dataOwnerId);
            statement.setObject(2, key);

            ResultSet rs = statement.executeQuery();

            return rs.next() ? rs.getString(1) : null;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error accessing the MySQL key value store.", e);
        }
    }

    @Override
    public void put(String key, @Nullable String value)
    {
        checkNotNull(key, "key");
        checkArgument(key.length() <= DB_FIELD_SIZE, "key can max be 200 characters.");

        // The databases 'key' column is called 'id' because 'key' is a
        // keyword in SQL.

        try
        {
            PreparedStatement statement;

            if (value != null)
            {
                checkArgument(value.length() <= DB_FIELD_SIZE, "value can max be 200 characters.");

                statement = connection.prepareStatement("INSERT INTO KeyValueStore (ownerId, id, value) VALUES (?, ?, ?)");
                statement.setObject(3, value);
            }
            else
            {
                statement = connection.prepareStatement("DELETE FROM KeyValueStore WHERE ownerId = ? AND id = ?");
            }

            statement.setObject(1, dataOwnerId);
            statement.setObject(2, key);

            statement.executeUpdate();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error accessing the MySQL key value store.", e);
        }
    }
}
