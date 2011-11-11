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

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.trifork.stamdata.Preconditions;
import com.trifork.stamdata.importer.jobs.sikrede.SikredeFields.SikredeFieldSpecification;
import com.trifork.stamdata.importer.jobs.sikrede.SikredeFields.SikredeType;
import com.trifork.stamdata.importer.persistence.Persister;
import com.trifork.stamdata.persistence.SikredeRecord;

public class SikredePersisterUsingNewArchitecture {

    private SikredeFields sikredeFields;
    private final Persister persister;

    public SikredePersisterUsingNewArchitecture(SikredeFields sikredeFields, Persister persister)
    {
        this.sikredeFields = sikredeFields;
        this.persister = persister;
    }
    
    public void persistRecordWithValidityDate(SikredeRecord record, String key, DateTime timestampOfInsertion) throws SQLException
    {
        Connection connection = persister.getConnection();
        PreparedStatement statement = createSelectStatementAsPreparedStatement(connection, key, record.getField(key));
        ResultSet resultSet = statement.executeQuery();

        // TODO: This might be slow when many records with the same key are loaded: in that case detecting this would be better handled on the server
        SikredeRecord recordThatIsCurrentlyValid = null;
        int numberOfRecordsWithSameKey = 0;
        while(resultSet.next())
        {
            numberOfRecordsWithSameKey++;
            
            // It is not permitted to insert records with a timestamp earlier than records already in the database
            if(timestampOfInsertion.isBefore(getValidFrom(resultSet)))
            {
                throw new IllegalArgumentException("The supplied timestamp is earlier than the valid from time of record with the same key alread present in the database");
            }

            if(validToDateIsSet(resultSet))
            {
                // It is not permitted to insert records with a timestamp before the end time of other records (except the case where valid to is not set)
                if(timestampOfInsertion.isBefore(getValidTo(resultSet)))
                {
                    throw new IllegalArgumentException("The supplied timestamp is earlier than the valid to time of a record with the same key alread present in the database");
                }
            }
            else
            {
                if(recordThatIsCurrentlyValid != null)
                {
                    throw new IllegalStateException("Database is in an invalid state. Several records with the same key \"" + key + "\" are still valid.");
                }
                
                recordThatIsCurrentlyValid = sikredeDataFromResultSet(resultSet);
            }
        }
        
        if(recordThatIsCurrentlyValid == null && numberOfRecordsWithSameKey > 0)
        {
            throw new IllegalStateException("Database is in an invalid state. Records with same key exists, but none of them are currently active.");
        }

        if(recordThatIsCurrentlyValid != null)
        {
            updateValidToDateForRecord(recordThatIsCurrentlyValid, key, timestampOfInsertion);
        }
        
        uncheckedInsertRecordWithValidityDate(record, timestampOfInsertion);
    }
    
    boolean validToDateIsSet(ResultSet resultSet) throws SQLException
    {
        return (resultSet.getDate("ValidTo") != null);
    }

    DateTime getValidFrom(ResultSet resultSet) throws SQLException 
    {
        return new DateTime(resultSet.getDate("ValidFrom"));
    }

    DateTime getValidTo(ResultSet resultSet) throws SQLException 
    {
        if(validToDateIsSet(resultSet))
        {
            return new DateTime(resultSet.getDate("ValidTo"));
        }
        else
        {
            return null;
        }
    }
    
    private void uncheckedInsertRecordWithValidityDate(SikredeRecord record, DateTime timestampOfInsertion) throws SQLException 
    {
        PreparedStatement statement = insertPreparedStatement(persister.getConnection());
        insertValuesIntoPreparedStatement(statement, record, timestampOfInsertion);
        statement.executeUpdate();
    }

    private void updateValidToDateForRecord(SikredeRecord record, String key, DateTime timestampOfInsertion) throws SQLException 
    {
        PreparedStatement statement = updateValidToPreparedStatement(persister.getConnection(), key);
        updateValuesIntoPreparedStatement(statement, record, key, timestampOfInsertion);
        statement.executeUpdate();
    }
    
    String insertStatementString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append("INSERT INTO SikredeGenerated (");
        
        List<String> fieldNames = new ArrayList<String>();
        List<String> questionMarks = new ArrayList<String>();
        for(SikredeFieldSpecification fieldSpecification: sikredeFields.getFieldSpecificationsInCorrectOrder())
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
    
    PreparedStatement insertPreparedStatement(Connection connection) throws SQLException
    {
        return connection.prepareStatement(insertStatementString());
    }
    
    void insertValuesIntoPreparedStatement(PreparedStatement preparedStatement, SikredeRecord record, DateTime validFrom) throws SQLException
    {
        if(!sikredeFields.conformsToSpecifications(record))
        {
            throw new IllegalArgumentException("Supplied values do not conform to fields in sikrede");
        }
        
        int index = 1;
        for(SikredeFieldSpecification fieldSpecification: sikredeFields.getFieldSpecificationsInCorrectOrder())
        {
            if(fieldSpecification.type == SikredeType.ALFANUMERICAL)
            {
                preparedStatement.setString(index, (String) record.get(fieldSpecification.name));
            } 
            else if(fieldSpecification.type == SikredeType.NUMERICAL)
            {
                preparedStatement.setInt(index, (Integer) record.get(fieldSpecification.name));
            }
            else
            {
                throw new AssertionError("SikredeType was not set correctly in Sikrede specification");
            }
            index++;
        }
        
        preparedStatement.setDate(index, new Date(validFrom.getMillis()));
    }
    
    ////////////////////////////////
    
    String createSelectStatementAsString(String key)
    {
        return "SELECT * FROM SikredeGenerated WHERE " + key + " = ?";
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
    SikredeRecord sikredeDataFromResultSet(ResultSet resultSet) throws SQLException
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
        
        if(!sikredeFields.conformsToSpecifications(record))
        {
            throw new IllegalStateException("ResultSet did not contain valid values as specified");
        }
        
        return record;
    }

    PreparedStatement updateValidToPreparedStatement(Connection connection, String key) throws SQLException 
    {
        return connection.prepareStatement("UPDATE SikredeGenerated SET ValidTo = ? WHERE " + key + " = ?");
    }

    void updateValuesIntoPreparedStatement(PreparedStatement statement, SikredeRecord record,
            String key, DateTime timestampOfInsertion) throws SQLException 
    {
        statement.setDate(1, new Date(timestampOfInsertion.getMillis()));
        statement.setObject(2, record.get(key));
    }
}
