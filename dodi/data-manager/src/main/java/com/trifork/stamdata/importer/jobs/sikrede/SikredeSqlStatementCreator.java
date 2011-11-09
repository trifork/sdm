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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.trifork.stamdata.Preconditions;
import com.trifork.stamdata.importer.jobs.sikrede.SikredeFields.SikredeFieldSpecification;
import com.trifork.stamdata.importer.jobs.sikrede.SikredeFields.SikredeType;

public class SikredeSqlStatementCreator 
{

    private SikredeFields sikredeFields;

    public SikredeSqlStatementCreator(SikredeFields sikredeFields)
    {
        this.sikredeFields = sikredeFields;
    }
    
    public String insertStatementString()
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
        
        builder.append(StringUtils.join(fieldNames, ", "));
        builder.append(") VALUES (");
        builder.append(StringUtils.join(questionMarks, ", "));
        builder.append(")");
        
        return builder.toString();
    }
    
    public PreparedStatement insertPreparedStatement(Connection connection) throws SQLException
    {
        return connection.prepareStatement(insertStatementString());
    }
    
    public void insertValuesIntoPreparedStatement(PreparedStatement preparedStatement, SikredeRecord record) throws SQLException
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
    }
    
    ////////////////////////////////
    
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
        
        if(!sikredeFields.conformsToSpecifications(record))
        {
            throw new IllegalStateException("ResultSet did not contain valid values as specified");
        }
        
        return record;
    }
}
