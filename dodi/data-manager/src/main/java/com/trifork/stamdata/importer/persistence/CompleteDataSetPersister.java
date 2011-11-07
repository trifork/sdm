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
package com.trifork.stamdata.importer.persistence;

import static java.lang.String.format;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;

import com.google.common.collect.Sets;
import com.trifork.stamdata.Entities;
import com.trifork.stamdata.models.TemporalEntity;

public class CompleteDataSetPersister implements Persister
{
    private final Connection connection;
    private final Set<Class<?>> visitedEntitySets;
    private final Instant transactionTime;

    public CompleteDataSetPersister(Connection connection, Instant transactionTime)
    {
        this.connection = connection;
        this.transactionTime = transactionTime;
        this.visitedEntitySets = Sets.newHashSet();
    }
    
    @Override
    public void persistCompleteDataset(CompleteDataset<? extends TemporalEntity>... dataset) throws Exception
    {
        throw new NotImplementedException("Not implemented.");
    }

    @Override
    public <T extends TemporalEntity> void persistDeltaDataset(Dataset<T> dataset) throws Exception
    {
        throw new NotImplementedException("Not implemented.");
    }

    @Override
    public Connection getConnection()
    {
        throw new NotImplementedException("Not implemented.");
    }

    @Override
    public void persist(Object record) throws SQLException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        // Close any records from previous.
        //
        // We keep track of which types we have closed so we only
        // do it once. Else we will close the ones we insert too.
        //
        prepare(record.getClass());
        
        // Store the record.
        //
        // If the exact same record was in the previous data set
        // the record is simply reopened.
        //
        insert(record);
    }
    
    private void prepare(Class<?> recordType) throws SQLException
    {
        // Only close the previous records once.
        //
        if (!visitedEntitySets.contains(recordType))
        {
            String tableName = Entities.getEntityTypeDisplayName(recordType);
            PreparedStatement statement = connection.prepareStatement(format("UPDATE %s SET ValidTo = ? WHERE ValidTo IS NULL", tableName));
            statement.setObject(1, transactionTime.toDate());
            statement.execute();
            statement.close();
            visitedEntitySets.add(recordType);
        }
    }
    
    private void insert(Object record) throws IllegalArgumentException, SQLException, IllegalAccessException, InvocationTargetException
    {
        // If the currently active records contain a record identical to the inserted
        // one, extend that record and don't insert the new record.
        //
        // This will not trigger an update and the record will not be propagated to
        // subscribers. There is no need for that since the record has not changed.
        //
        if (updateDublicateFromPreviousStatement(record.getClass())) return;
        
        // Otherwise we simply insert the new record.
        //
        String tableName = Entities.getEntityTypeDisplayName(record.getClass());
        String columnNames = constructInsertColumnNameList(record.getClass());
        String columnParams = constructInsertColumnValueList(record.getClass());
        PreparedStatement statement = connection.prepareStatement(format("INSERT INTO %s (%s) VALUES (%s)", tableName, columnNames, columnParams));
        
        int parameterIndex = 1;
        for (Method column : Entities.getColumns(record.getClass()))
        {
            Object value = column.invoke(record);
            statement.setObject(parameterIndex++, value);
        }
        
        statement.setObject(parameterIndex++, transactionTime.toDate());
        statement.setObject(parameterIndex++, transactionTime.toDate());
        statement.setObject(parameterIndex++, transactionTime.toDate());
    }
    
    private boolean updateDublicateFromPreviousStatement(Object record) throws SQLException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        String tableName = Entities.getEntityTypeDisplayName(record.getClass());
        PreparedStatement statement = connection.prepareStatement(format("UPDATE %s SET ValidTo = NULL WHERE %s", tableName, constructWhereClause(record.getClass())));
        
        int parameterIndex = 1;
        for (Method column : Entities.getColumns(record.getClass()))
        {
            Object value = column.invoke(record);
            statement.setObject(parameterIndex++, value);
        }
        
        statement.setObject(parameterIndex++, transactionTime.toDate());
        
        int numChanges = statement.executeUpdate();
        if (numChanges > 1) throw new IllegalStateException("Multiple rows where marked as current. This is a programming error.");
        return numChanges == 1;
    }
    
    private String constructInsertColumnNameList(Class<?> recordType)
    {
        final String VALID_FROM = "ValidFrom";
        final String MODIFIED_DATE = "ModifiedDate";
        final String CREATED_DATE = "CreatedDate";
        
        StringBuilder columnNames = new StringBuilder();
        
        for (Method column : Entities.getColumns(recordType))
        {
            String name = Entities.getColumnName(column);
            columnNames.append(name).append(",");
        }
        
        columnNames.append(VALID_FROM).append(",");
        columnNames.append(MODIFIED_DATE).append(",");
        columnNames.append(CREATED_DATE);
        
        return columnNames.toString();
    }
    
    private String constructInsertColumnValueList(Class<?> recordType)
    {
        final int VALID_FROM = 1;
        final int MODIFIED_DATE = 1;
        final int CREATED_DATE = 1;
        
        int numColumns = Entities.getColumns(recordType).size() + VALID_FROM + MODIFIED_DATE + CREATED_DATE;
        
        return StringUtils.repeat("?", ",", numColumns);
    }
    
    private String constructWhereClause(Class<?> recordType)
    {
        StringBuilder whereClause = new StringBuilder();
        
        for (Method column : Entities.getColumns(recordType))
        {
            String columnName = Entities.getOutputFieldName(column);
            whereClause.append(columnName).append(" = ").append("?").append(" AND ");
        }
        
        whereClause.append("ValidTo = ?");
        
        return whereClause.toString();
    }
}
