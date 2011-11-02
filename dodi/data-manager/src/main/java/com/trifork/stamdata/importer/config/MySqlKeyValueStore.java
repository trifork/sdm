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
