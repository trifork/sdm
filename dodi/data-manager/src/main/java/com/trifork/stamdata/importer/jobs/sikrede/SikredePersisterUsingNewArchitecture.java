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

import org.joda.time.DateTime;

import com.trifork.stamdata.importer.persistence.Persister;

public class SikredePersisterUsingNewArchitecture {

    private final SikredeSqlStatementCreator statementCreator;
    private final Persister persister;

    public SikredePersisterUsingNewArchitecture(SikredeSqlStatementCreator statementCreator, Persister persister)
    {
        this.statementCreator = statementCreator;
        this.persister = persister;
    }
    
    public void persistRecordWithValidityDate(SikredeRecord record, String key, DateTime timestampOfInsertion) throws SQLException
    {
        Connection connection = persister.getConnection();
        PreparedStatement statement = statementCreator.createSelectStatementAsPreparedStatement(connection, key, record.getField(key));
        ResultSet resultSet = statement.executeQuery();

        // TODO: This might be slow when many records with the same key are loaded: in that case detecting this would be better handled on the server
        SikredeRecord recordThatIsCurrentlyValid = null;
        int numberOfRecordsWithSameKey = 0;
        while(resultSet.next())
        {
            numberOfRecordsWithSameKey++;
            
            if(validToDateIsSet(resultSet))
            {
                // TODO: Examine that the new timestamp is after the date found in the result to ensure we are not "pushing it in"
            }
            else
            {
                if(recordThatIsCurrentlyValid != null)
                {
                    throw new IllegalStateException("Database is in an invalid state. Several records are still valid.");
                }
                
                recordThatIsCurrentlyValid = statementCreator.sikredeDataFromResultSet(resultSet);
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
    
    private boolean validToDateIsSet(ResultSet resultSet) throws SQLException
    {
        return (resultSet.getDate("ValidTo") != null);
    }

    public DateTime getValidFrom(ResultSet resultSet) throws SQLException 
    {
        return new DateTime(resultSet.getDate("ValidFrom"));
    }

    public DateTime getValidtTo(ResultSet resultSet) throws SQLException 
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
        PreparedStatement statement = statementCreator.insertPreparedStatement(persister.getConnection());
        statementCreator.insertValuesIntoPreparedStatement(statement, record, timestampOfInsertion);
        statement.executeUpdate();
    }

    private void updateValidToDateForRecord(SikredeRecord record, String key, DateTime timestampOfInsertion) throws SQLException 
    {
        PreparedStatement statement = statementCreator.updateValidToPreparedStatement(persister.getConnection(), key);
        statementCreator.updateValuesIntoPreparedStatement(statement, record, key, timestampOfInsertion);
        statement.executeUpdate();
    }
}
