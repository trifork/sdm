package com.trifork.stamdata.persistence;

import com.trifork.stamdata.Nullable;
import dk.nsi.stamdata.security.DenGodeWebServiceFilter;
import org.joda.time.Instant;

import javax.inject.Inject;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: borlum
 * Date: 11/17/11
 * Time: 12:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class RecordFetcher
{
    private final Connection connection;

    @Inject
    RecordFetcher(Connection connection)
    {
        this.connection = connection;
    }

    public Record fetchCurrent(String key, RecordSpecification recordSpecification) throws SQLException
    {
        String queryString = String.format("SELECT * FROM %s WHERE %s = ? AND validTo IS NULL", recordSpecification.getTable(), recordSpecification.getKeyColumn());
        PreparedStatement preparedStatement = connection.prepareStatement(queryString);
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
    }

    public List<RecordMetadata> fetchSince(RecordSpecification recordSpecification, long fromPID, Instant fromModifiedDate, int limit) throws SQLException
    {
        String queryString = String.format("SELECT * FROM %s WHERE " +
                "(PID > ? AND ModifiedDate = ?) OR " +
                "PID > ? OR " +
                "(PID = ? AND ModifiedDate > ?) " +
                "ORDER BY PID, ModifiedDate LIMIT %d", recordSpecification.getTable(), limit);
        PreparedStatement preparedStatement = connection.prepareStatement(queryString);

        Timestamp fromModifiedDateAsTimestamp = new Timestamp(fromModifiedDate.getMillis());
        preparedStatement.setObject(1, fromPID);
        preparedStatement.setTimestamp(2, fromModifiedDateAsTimestamp);
        preparedStatement.setObject(3, fromPID);
        preparedStatement.setObject(4, fromPID);
        preparedStatement.setTimestamp(5, fromModifiedDateAsTimestamp);

        ResultSet resultSet = preparedStatement.executeQuery();

        List<RecordMetadata> result = new ArrayList();
        while(resultSet.next())
        {
            Instant validFrom = new Instant(resultSet.getTimestamp("ValidFrom"));
            Instant validTo = new Instant(resultSet.getTimestamp("ValidTo"));
            Instant modifiedDate = new Instant(resultSet.getTimestamp("ModifiedDate"));
            Long pid = (Long) resultSet.getObject("PID");
            Record record = createRecordFromResultSet(recordSpecification, resultSet);
            RecordMetadata recordMetadata = new RecordMetadata(validFrom, validTo, modifiedDate, pid, record);
            result.add(recordMetadata);
        }

        return result;
    }

    private Record createRecordFromResultSet(RecordSpecification recordSpecification, ResultSet resultSet) throws SQLException
    {
        RecordBuilder builder = new RecordBuilder(recordSpecification);

        for (RecordSpecification.FieldSpecification fieldSpec : recordSpecification.getFieldSpecs())
        {
            String fieldName = fieldSpec.name;

            if (fieldSpec.type == RecordSpecification.RecordFieldType.NUMERICAL)
            {
                // TODO: Explicit check of returned type
                builder.field(fieldName, resultSet.getInt(fieldName));
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

        return builder.build();
    }
}
