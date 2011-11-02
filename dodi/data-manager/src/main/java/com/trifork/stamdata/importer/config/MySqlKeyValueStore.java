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

import java.sql.Connection;
import java.sql.SQLException;

import javax.inject.Inject;

import com.trifork.stamdata.Preconditions;

public class MySqlKeyValueStore implements KeyValueStore {

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
     *            Thrown if the database is unreachable.
     */
    @Inject
    MySqlKeyValueStore(Connection connection, String ownerId) throws SQLException {
        
        this.connection = Preconditions.checkNotNull(connection, "connection");
        Preconditions.checkArgument(connection.getTransactionIsolation() != Connection.TRANSACTION_NONE, "The connection must have an open transaction.");
        
        this.ownerId = Preconditions.checkNotNull(ownerId);
    }

    @Override
    public String get(String key) {

        Preconditions.checkNotNull(key, "key");
        
        return null;
    }

    @Override
    public void set(String key, String value) {

        Preconditions.checkNotNull(key, "key");

    }
}
