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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.trifork.stamdata.Preconditions;
import com.trifork.stamdata.persistence.RecordSpecification.FieldSpecification;
import com.trifork.stamdata.persistence.RecordSpecification.RecordFieldType;

//FIXME: This class has all the fetcher methods I moved Friday morning ;( Will have to be moved
public class RecordPersister
{
    private final Connection connection;
    private final Instant transactionTime;

    @Inject
    public RecordPersister(Connection connection, Instant transactionTime)
    {
        this.connection = connection;
        this.transactionTime = transactionTime;
    }

    public Long persist(Record record, RecordSpecification specification) throws SQLException
    {
    	Long result = null;
        Preconditions.checkNotNull(record);
        Preconditions.checkNotNull(specification);
        Preconditions.checkArgument(specification.conformsToSpecifications(record));

        /*
        PreparedStatement statement = connection.prepareStatement(format("UPDATE %s SET validTo = ?, modifiedDate = ? WHERE %s = ? AND validTo IS NULL", specification.getTable(), specification.getKeyColumn()));
        statement.setObject(1, transactionTime.toDate());
        statement.setObject(2, transactionTime.toDate());
        statement.setObject(3, record.get(specification.getKeyColumn()));
        statement.close();
        */

        // Data dumps from Yderregister and "Sikrede" contains history information and are therefore handled
        // differently from all other register types. The data contained in each input record is appended directly
        // to the database instead of updating existing records.

        // We need to return auto generated index in case we need to make foreign keys

/*        try {
            connection.setAutoCommit(false);*/
            /*
            String selectForUpdate = "SELECT count(*) FROM " + specification.getTable() + " WHERE ValidTo IS NULL AND " + specification.getKeyColumn() + " = ?";
            
            PreparedStatement selectStatement = connection.prepareStatement(selectForUpdate);
            populateSelectStatement(selectStatement, record, specification);
            ResultSet resultSet = selectStatement.executeQuery();
            
            if (resultSet != null && resultSet.next()) {
            	long rowCount = resultSet.getLong(1);
    			if (rowCount == 1) {
                    PreparedStatement updateRecordStatement = connection.prepareStatement(createUpdateStatementSql(specification));
                    populateUpdateStatement(updateRecordStatement, record, specification);
                    
                    int updatedRows = updateRecordStatement.executeUpdate();
                    updateRecordStatement.close();
                    
                    Preconditions.checkState(1 == updatedRows, "A single row should have been updated - but updatedRows are: " + updatedRows);
            	} else if (rowCount > 1) {
            		throw new AssertionError("More than 1 row exists for " + specification.getTable() + " with ValidTo NULL");
            	}
            }*/
            
            PreparedStatement insertRecordStatement = connection.prepareStatement(createInsertStatementSql(specification), PreparedStatement.RETURN_GENERATED_KEYS);

            populateInsertStatement(insertRecordStatement, record, specification);
            insertRecordStatement.executeUpdate();
            ResultSet rs = insertRecordStatement.getGeneratedKeys();
            if (rs != null) {
            	rs.next();
            	result = rs.getLong(1);
            }
            
//            connection.commit();
            insertRecordStatement.close();
/*        } catch (Exception e) {
			if (connection != null) {
				connection.rollback();
				connection.setAutoCommit(true);
			}
        	throw new SQLException(e);
		}
        connection.setAutoCommit(true);*/
        return result;
    }
    
    private boolean checkIfRecordIsUpdated(Record record, RecordSpecification specification)
    {
    	
    	return false;
    }
    /*
    public void populateSelectStatement(PreparedStatement preparedStatement, Record record, RecordSpecification recordSpecification) throws SQLException
    {
    	Preconditions.checkNotNull(recordSpecification.getKeyColumn());
    	// Preconditions.checkArgument(recordSpecification.getKeyColumn(), recordSpecification., errorMessage)
    	String keyColumn = recordSpecification.getKeyColumn();
    	for (FieldSpecification fieldSpecification: recordSpecification.getFieldSpecs())
    	{
    		if (keyColumn != null && keyColumn.equals(fieldSpecification.name)) {
    			RecordFieldType recordFieldType = fieldSpecification.type;
    			if (recordFieldType.equals(RecordFieldType.ALPHANUMERICAL)) {
    				preparedStatement.setString(1, (String)record.get(keyColumn));
    			} else if (recordFieldType.equals(RecordFieldType.NUMERICAL)) {
    				preparedStatement.setLong(1, (Long) record.get(keyColumn));
    			} else {
					throw new IllegalArgumentException("Only alfanumerical and numerical 'keys' are supported and record value must not be null");
				}
    		}
    	}
    	
    }
    
    public void populateUpdateStatement(PreparedStatement preparedStatement, Record record, RecordSpecification recordSpecification) throws SQLException
    {
    	Preconditions.checkNotNull(recordSpecification.getKeyColumn());
    	
    	int index = 1;
    	
    	preparedStatement.setTimestamp(index++, new Timestamp(transactionTime.getMillis()));
    	
    	String keyColumn = recordSpecification.getKeyColumn();
    	for (FieldSpecification fieldSpecification: recordSpecification.getFieldSpecs())
    	{
    		if (keyColumn != null && keyColumn.equals(fieldSpecification.name)) {
    			RecordFieldType recordFieldType = fieldSpecification.type;
    			if (recordFieldType.equals(RecordFieldType.ALPHANUMERICAL)) {
    				preparedStatement.setString(index++, (String)record.get(keyColumn));
    			} else if (recordFieldType.equals(RecordFieldType.NUMERICAL)) {
    				preparedStatement.setLong(index++, (Long) record.get(keyColumn));
    			} else {
					throw new IllegalArgumentException("Only alfanumerical and numerical 'keys' are supported");
				}
    		}
    	}
    	
    }
*/
    public void populateInsertStatement(PreparedStatement preparedStatement, Record record, RecordSpecification recordSpec) throws SQLException
    {
        Preconditions.checkArgument(recordSpec.conformsToSpecifications(record), "The record does not conform to it's spec.");

        int index = 1;

        for (FieldSpecification fieldSpecification: recordSpec.getFieldSpecs())
        {
            if(fieldSpecification.persistField)
            {
            	Object fieldVal = record.get(fieldSpecification.name);
            	if (fieldSpecification.type == RecordSpecification.RecordFieldType.ALPHANUMERICAL)
                {
            		if (fieldVal == null && fieldSpecification.allowNull) {
                		preparedStatement.setNull(index, java.sql.Types.VARCHAR);
                	} else {
                		preparedStatement.setString(index, (String) fieldVal);
                	}
                }
                else if (fieldSpecification.type == RecordSpecification.RecordFieldType.NUMERICAL)
                {
                	if (fieldVal == null && fieldSpecification.allowNull) {
                		preparedStatement.setNull(index, java.sql.Types.BIGINT);
                	} else {
                		preparedStatement.setLong(index, (Long) fieldVal);
                	}
                }
                else
                {
                    throw new AssertionError("RecordType was not set correctly in the specification");
                }

                index++;
            }
        }

        preparedStatement.setTimestamp(index++, new Timestamp(transactionTime.getMillis()));
        preparedStatement.setTimestamp(index++, new Timestamp(transactionTime.getMillis()));
    }

    public String createInsertStatementSql(RecordSpecification specification)
    {
        StringBuilder builder = new StringBuilder();

        builder.append("INSERT INTO ").append(specification.getTable()).append(" (");

        List<String> fieldNames = Lists.newArrayList();
        List<String> questionMarks = Lists.newArrayList();

        for (FieldSpecification fieldSpecification: specification.getFieldSpecs())
        {
            if(fieldSpecification.persistField)
            {
                fieldNames.add(fieldSpecification.name);
                questionMarks.add("?");
            }
        }

        fieldNames.add("ValidFrom");
        questionMarks.add("?");

        fieldNames.add("ModifiedDate");
        questionMarks.add("?");

        builder.append(StringUtils.join(fieldNames, ", "));
        builder.append(") VALUES (");
        builder.append(StringUtils.join(questionMarks, ", "));
        builder.append(")");

        return builder.toString();
    }
    
    public String createUpdateStatementSql(RecordSpecification specification)
    {
        StringBuilder builder = new StringBuilder();

        builder.append("UPDATE ").append(specification.getTable()).append(" SET ValidTo = ? WHERE " + specification.getKeyColumn() + " = ? AND ValidTo IS NULL");

        return builder.toString();
    }
}
