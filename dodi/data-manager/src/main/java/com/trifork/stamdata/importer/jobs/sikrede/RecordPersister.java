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
package com.trifork.stamdata.importer.jobs.sikrede;

import com.google.common.collect.Lists;
import com.trifork.stamdata.Preconditions;
import com.trifork.stamdata.importer.jobs.sikrede.RecordSpecification.FieldSpecification;
import com.trifork.stamdata.importer.jobs.sikrede.RecordSpecification.SikredeType;
import com.trifork.stamdata.importer.persistence.Persister;
import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;

import java.sql.*;
import java.util.List;

import static java.lang.String.format;

public class RecordPersister
{
    private RecordSpecification recordSpec;
    private final Persister persister;

    public RecordPersister(RecordSpecification recordSpecification, Persister persister)
    {
        this.recordSpec = recordSpecification;
        this.persister = persister;
    }

    public void persistRecordWithValidityDate(Record record, String key, Instant transactionTime) throws SQLException
    {
        Connection connection = persister.getConnection();
        PreparedStatement statement = createSelectStatementAsPreparedStatement(connection, key, record.getField(key));
        ResultSet resultSet = statement.executeQuery();

        // TODO: This might be slow when many records with the same key are loaded, in that case detecting this would be better handled on the server.
        Record recordThatIsCurrentlyValid = null;
        int numberOfRecordsWithSameKey = 0;

        // FIXME: I think not all cases are taken into account here.

        while (resultSet.next())
        {
            numberOfRecordsWithSameKey++;

            // It is not permitted to insert records with a timestamp earlier than records already in the database
            if (transactionTime.isBefore(getValidFrom(resultSet)))
            {
                throw new IllegalArgumentException("The supplied timestamp is earlier than the valid from time of record with the same key alread present in the database");
            }

            if (isValidToSet(resultSet))
            {
                // It is not permitted to insert records with a timestamp before the end time of other records (except the case where valid to is not set)
                if (transactionTime.isBefore(getValidTo(resultSet)))
                {
                    throw new IllegalArgumentException("The supplied timestamp is earlier than the valid to time of a record with the same key alread present in the database");
                }
            }
            else
            {
                if (recordThatIsCurrentlyValid != null)
                {
                    throw new IllegalStateException("Database is in an invalid state. Several records with the same key \"" + key + "\" are still valid.");
                }

                recordThatIsCurrentlyValid = createRecordUsingResultSet(resultSet);
            }
        }

        if (recordThatIsCurrentlyValid == null && numberOfRecordsWithSameKey > 0)
        {
            throw new IllegalStateException("Database is in an invalid state. Records with same key exists, but none of them are currently active.");
        }

        if (recordThatIsCurrentlyValid != null)
        {
            updateValidToOnRecord(recordThatIsCurrentlyValid, key, transactionTime);
        }

        uncheckedInsertRecordWithValidityDate(record, transactionTime);
    }

    private boolean isValidToSet(ResultSet resultSet) throws SQLException
    {
        return (resultSet.getTimestamp("ValidTo") != null);
    }

    Instant getValidFrom(ResultSet resultSet) throws SQLException
    {
        return new Instant(resultSet.getTimestamp("ValidFrom"));
    }

    Instant getValidTo(ResultSet resultSet) throws SQLException
    {
        return isValidToSet(resultSet) ? new Instant(resultSet.getTimestamp("ValidTo")) : null;
    }

    private void uncheckedInsertRecordWithValidityDate(Record record, Instant transactionTime) throws SQLException
    {
        PreparedStatement statement = createInsertStatement(persister.getConnection());
        populateStatement(statement, record, transactionTime);
        statement.executeUpdate();
    }

    private void updateValidToOnRecord(Record record, String key, Instant transactionTime) throws SQLException
    {
        PreparedStatement statement = updateValidToForRecordWithKey(persister.getConnection(), key);
        populateUpdateStatement(statement, record, key, transactionTime);
        statement.executeUpdate();
    }

    String createInsertStatementSql()
    {
        StringBuilder builder = new StringBuilder();

        builder.append("INSERT INTO SikredeGenerated (");

        List<String> fieldNames = Lists.newArrayList();
        List<String> questionMarks = Lists.newArrayList();

        for(FieldSpecification fieldSpecification: recordSpec.getFieldSpecificationsInCorrectOrder())
        {
            fieldNames.add(fieldSpecification.name);
            questionMarks.add("?");
        }

        fieldNames.add("ValidFrom");
        questionMarks.add("?");

        builder.append(StringUtils.join(fieldNames, ", "));
        builder.append(") VALUES (");
        builder.append(StringUtils.join(questionMarks, ", "));
        builder.append(")");

        return builder.toString();
    }

    private PreparedStatement createInsertStatement(Connection connection) throws SQLException
    {
        return connection.prepareStatement(createInsertStatementSql());
    }

    void populateStatement(PreparedStatement preparedStatement, Record record, Instant validFrom) throws SQLException
    {
        Preconditions.checkArgument(recordSpec.conformsToSpecifications(record), "The record does not conform to it's spec.");

        int index = 1;

        for (FieldSpecification fieldSpecification: recordSpec.getFieldSpecificationsInCorrectOrder())
        {
            if (fieldSpecification.type == SikredeType.ALFANUMERICAL)
            {
                preparedStatement.setString(index, (String) record.get(fieldSpecification.name));
            }
            else if (fieldSpecification.type == SikredeType.NUMERICAL)
            {
                preparedStatement.setInt(index, (Integer) record.get(fieldSpecification.name));
            }
            else
            {
                throw new AssertionError("SikredeType was not set correctly in Sikrede specification");
            }

            index++;
        }

        preparedStatement.setTimestamp(index, new Timestamp(validFrom.getMillis()));
    }

    ////////////////////////////////

    String createSelectStatementAsString(String key)
    {
        return format("SELECT * FROM SikredeGenerated WHERE %s = ?", key);
    }

    PreparedStatement createSelectStatementAsPreparedStatement(Connection connection, String key, Object value) throws SQLException
    {
        PreparedStatement statement = connection.prepareStatement(createSelectStatementAsString(key));
        statement.setObject(1, value);
        return statement;
    }

    /**
     * Assumes the result set is pointing to a record (i.e. that next() was called at least once on the ResultSet
     * @throws SQLException
     */
    Record createRecordUsingResultSet(ResultSet resultSet) throws SQLException
    {
        Preconditions.checkNotNull(resultSet);
        Preconditions.checkArgument(!resultSet.isBeforeFirst());
        Preconditions.checkArgument(!resultSet.isAfterLast());

        RecordBuilder builder = new RecordBuilder(recordSpec);

        for(FieldSpecification fieldSpec : recordSpec.getFieldSpecificationsInCorrectOrder())
        {
            String key = fieldSpec.name;
            
            if (fieldSpec.type == SikredeType.NUMERICAL)
            {
                // TODO: Explicit check of returned type
                builder.field(key, resultSet.getInt(key));
            }
            else if (fieldSpec.type == SikredeType.ALFANUMERICAL)
            {
                builder.field(key, resultSet.getString(key));
            }
            else
            {
                throw new AssertionError("Invalid field specifier used");
            }
        }

        Record record = builder.build();

        if (!recordSpec.conformsToSpecifications(record))
        {
            throw new IllegalStateException("ResultSet did not contain valid values as specified");
        }

        return record;
    }

    private PreparedStatement updateValidToForRecordWithKey(Connection connection, String key) throws SQLException
    {
        return connection.prepareStatement("UPDATE SikredeGenerated SET ValidTo = ? WHERE " + key + " = ?");
    }

    private void populateUpdateStatement(PreparedStatement statement, Record record, String key, Instant transactionTime) throws SQLException
    {
        statement.setTimestamp(1, new Timestamp(transactionTime.getMillis()));
        statement.setObject(2, record.get(key));
    }
}
