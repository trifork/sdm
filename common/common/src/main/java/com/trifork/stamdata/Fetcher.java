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
package com.trifork.stamdata;

import static com.trifork.stamdata.Preconditions.checkNotNull;
import static java.lang.String.format;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.Type;
import org.joda.time.Instant;

import com.google.inject.Inject;
import com.trifork.stamdata.models.TemporalEntity;
import com.trifork.stamdata.persistence.Transactional;


public class Fetcher
{
    private final Session session;


    @Inject
    Fetcher(Session session)
    {
        this.session = checkNotNull(session);
    }


    @Transactional
    public <T extends TemporalEntity> T fetch(Class<T> type, Object id) throws SQLException
    {
        return fetch(Instant.now(), type, id);
    }


    @Transactional
    public <T extends TemporalEntity> List<T> fetch(Class<T> type, String column, Object value, Type sqlType) throws SQLException
    {
        return fetch(Instant.now(), type, column, value, sqlType);
    }


    @Transactional
    public <T extends TemporalEntity> List<T> fetch(Class<T> type, String column, Object value) throws SQLException
    {
        return fetch(Instant.now(), type, column, value);
    }


    @Transactional
    public <T extends TemporalEntity> List<T> fetch(Class<T> type, Map<String, Object> columnAndValue) throws SQLException
    {
        return fetch(Instant.now(), type, columnAndValue);
    }


    @SuppressWarnings("unchecked")
    @Transactional
    public <T extends TemporalEntity> T fetch(Instant instant, Class<T> type, Object id) throws SQLException
    {
        checkNotNull(instant, "instant");
        checkNotNull(type, "type");
        checkNotNull(id, "id");

        // The database uses open/closed validity intervals: [a;b[
        // When a record is 'closed' (no longer valid) its ValidTo
        // is set to the same value as its ValidFrom. Therefore the
        // ValidTo must be checked using the '<' operator.

        String keyColumn = Entities.getIdColumnName(type);
        String entityName = type.getCanonicalName();

        Query query = session.createQuery(format("FROM %s WHERE %s = :id AND ValidFrom <= :instant AND :instant < ValidTo", entityName, keyColumn));

        query.setParameter("id", id);
        query.setTimestamp("instant", instant.toDate());

        // This query should only ever return a single result or the database's
        // structure has been corrupted. So we want an exception to be thrown.

        return (T) query.uniqueResult();
    }


    @SuppressWarnings("unchecked")
    @Transactional
    public <T extends TemporalEntity> List<T> fetch(Instant instant, Class<T> type, String column, Object value, Type sqlType)
    {
        checkNotNull(instant, "instant");
        checkNotNull(type, "type");
        checkNotNull(column, "column");
        checkNotNull(value, "value");

        String entityName = type.getCanonicalName();
        Query query = session.createQuery(format("FROM %s WHERE %s = :value AND ValidFrom <= :instant AND :instant < ValidTo", entityName, column));

        query.setParameter("value", value, sqlType);
        query.setTimestamp("instant", instant.toDate());

        return (List<T>) query.list();
    }


    @SuppressWarnings("unchecked")
    @Transactional
    public <T extends TemporalEntity> List<T> fetch(Instant instant, Class<T> type, String column, Object value)
    {
        checkNotNull(instant, "instant");
        checkNotNull(type, "type");
        checkNotNull(column, "column");
        checkNotNull(value, "value");

        String entityName = type.getCanonicalName();
        Query query = session.createQuery(format("FROM %s WHERE %s = :value AND ValidFrom <= :instant AND :instant < ValidTo", entityName, column));

        query.setParameter("value", value);
        query.setTimestamp("instant", instant.toDate());

        return (List<T>) query.list();
    }


    @SuppressWarnings("unchecked")
    @Transactional
    public <T extends TemporalEntity> List<T> fetch(Instant instant, Class<T> type, Map<String, Object> columnAndValue)
    {
        checkNotNull(instant, "instant");
        checkNotNull(type, "type");
        checkNotNull(columnAndValue, "columnAndValue");
        Preconditions.checkArgument(!columnAndValue.isEmpty(), "You must specify a map containing column name -> value pairs");

        StringBuffer whereClause = new StringBuffer();
        for (String columnName : columnAndValue.keySet())
        {
            if (whereClause.length() > 0)
            {
                whereClause.append(" AND ");
            }
            whereClause.append(columnName + " = :" + columnName);
        }
        whereClause.append(" AND ValidFrom <= :instant AND :instant < ValidTo");

        String entityName = type.getCanonicalName();
        Query query = session.createQuery(format("FROM %s WHERE " + whereClause.toString(), entityName));
        query.setTimestamp("instant", instant.toDate());

        // Set remaining variables on query
        for (String columnName : columnAndValue.keySet())
        {
            query.setParameter(columnName, columnAndValue.get(columnName));
        }

        return (List<T>) query.list();
    }
}
