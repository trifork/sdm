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
package com.trifork.stamdata.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.Instant;

import com.google.inject.Provider;


public class RecordFetcher
{
    private final Provider<Connection> connectionProvider;

    @Inject
    public RecordFetcher(Provider<Connection> connectionProvider)
    {
        this.connectionProvider = connectionProvider;
    }
    
    public Record fetchCurrent(String key, RecordSpecification recordSpecification, String lookupColumn) throws SQLException
    {
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        try {
            String queryString = String.format("SELECT * FROM %s WHERE %s = ? AND validTo IS NULL", recordSpecification.getTable(), lookupColumn);
            connection = connectionProvider.get();
            preparedStatement = connection.prepareStatement(queryString);
            preparedStatement.setObject(1, key);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                return createRecordFromResultSet(recordSpecification, resultSet);
            }
            else
            {
                return null;
            }
        } finally {
            if(preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
    
    public Record fetchCurrent(String key, RecordSpecification recordSpecification) throws SQLException
    {
        return fetchCurrent(key, recordSpecification, recordSpecification.getKeyColumn());
    }

    public List<RecordMetadata> fetchSince(RecordSpecification recordSpecification, long fromPID, Instant fromModifiedDate, int limit) throws SQLException
    {
        String queryString = String.format("SELECT * FROM %s WHERE " +
                "(PID > ? AND ModifiedDate = ?) OR " +
                "(ModifiedDate > ?) " +
                "ORDER BY ModifiedDate, PID LIMIT %d", recordSpecification.getTable(), limit);
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionProvider.get();
            preparedStatement = connection.prepareStatement(queryString);

            Timestamp fromModifiedDateAsTimestamp = new Timestamp(fromModifiedDate.getMillis());
            preparedStatement.setObject(1, fromPID);
            preparedStatement.setTimestamp(2, fromModifiedDateAsTimestamp);
            preparedStatement.setTimestamp(3, fromModifiedDateAsTimestamp);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<RecordMetadata> result = new ArrayList<RecordMetadata>();
            while(resultSet.next())
            {
                Instant validFrom = new Instant(resultSet.getTimestamp("ValidFrom"));
                // Validto can be null - and a new Instant with null as argument gives "now"
                Timestamp vto = resultSet.getTimestamp("ValidTo");
                Instant validTo = null;
                if(vto != null) {
                    validTo = new Instant(vto);
                }
                Instant modifiedDate = new Instant(resultSet.getTimestamp("ModifiedDate"));
                Long pid = (Long) resultSet.getObject("PID");
                Record record = createRecordFromResultSet(recordSpecification, resultSet);
                RecordMetadata recordMetadata = new RecordMetadata(validFrom, validTo, modifiedDate, pid, record);
                result.add(recordMetadata);
            }
            return result;
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    private Record createRecordFromResultSet(RecordSpecification recordSpecification, ResultSet resultSet) throws SQLException
    {
        RecordBuilder builder = new RecordBuilder(recordSpecification);

        for (RecordSpecification.FieldSpecification fieldSpec : recordSpecification.getFieldSpecs())
        {
            if(fieldSpec.persistField)
            {
                String fieldName = fieldSpec.name;

                if (fieldSpec.type == RecordSpecification.RecordFieldType.NUMERICAL)
                {
                    builder.field(fieldName, resultSet.getLong(fieldName));
                }
                else if (fieldSpec.type == RecordSpecification.RecordFieldType.ALPHANUMERICAL)
                {
                    builder.field(fieldName, resultSet.getString(fieldName));
                }
                else
                {
                    throw new AssertionError("Invalid field specifier used");
                }
            }
        }

        return builder.build();
    }
}
