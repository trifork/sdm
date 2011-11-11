package com.trifork.stamdata.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.trifork.stamdata.Preconditions;
import com.trifork.stamdata.persistence.SikredeFields;
import com.trifork.stamdata.persistence.SikredeRecordBuilder;
import com.trifork.stamdata.persistence.SikredeFields.SikredeFieldSpecification;
import com.trifork.stamdata.persistence.SikredeFields.SikredeType;

public class SikredeFetcher 
{

    private SikredeFields sikredeFields;

    public SikredeFetcher(SikredeFields sikredeFields)
    {
        this.sikredeFields = sikredeFields;
    }
    
    public SikredeRecord fetchSikredeRecordUsingCpr(Connection connection, String cpr) throws SQLException
    {
        PreparedStatement preparedStatement = createSelectStatementAsPreparedStatement(connection, "CPRnr", cpr);
        ResultSet resultSet = preparedStatement.executeQuery();
        return sikredeDataFromResultSet(resultSet);
    }
    
    public String createSelectStatementAsString(String key)
    {
        return "SELECT * FROM SikredeGenerated WHERE " + key + " = ?";
    }
    
    public PreparedStatement createSelectStatementAsPreparedStatement(Connection connection, String key, Object value) throws SQLException
    {
        PreparedStatement statement = connection.prepareStatement(createSelectStatementAsString(key));
        statement.setObject(1, value);
        return statement;
    }
    
    /**
     * Assumes the result set is pointing to a record (i.e. that next() was called at least once on the ResultSet
     * @throws SQLException 
     */
    public SikredeRecord sikredeDataFromResultSet(ResultSet resultSet) throws SQLException
    {
        Preconditions.checkNotNull(resultSet);
        Preconditions.checkArgument(!resultSet.isBeforeFirst());
        Preconditions.checkArgument(!resultSet.isAfterLast());
        
        SikredeRecordBuilder builder = new SikredeRecordBuilder(sikredeFields);
        
        for(SikredeFieldSpecification fieldSpecification : sikredeFields.getFieldSpecificationsInCorrectOrder())
        {
            String key = fieldSpecification.name;
            if(fieldSpecification.type == SikredeType.NUMERICAL)
            {
                // TODO: Explicit check of returned type
                builder.field(key, resultSet.getInt(key));
            }
            else if(fieldSpecification.type == SikredeType.ALFANUMERICAL)
            {
                builder.field(key, resultSet.getString(key));
            }
            else
            {
                throw new AssertionError("Invalid field specifier used");
            }
        }
        
        SikredeRecord record = builder.build();
        
        return record;
    }
}
