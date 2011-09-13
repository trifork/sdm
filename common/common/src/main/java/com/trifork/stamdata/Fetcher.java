package com.trifork.stamdata;

import static com.trifork.stamdata.Preconditions.checkNotNull;
import static java.lang.String.format;

import java.sql.SQLException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.joda.time.Instant;

import com.google.inject.Inject;
import com.trifork.stamdata.models.TemporalEntity;

public class Fetcher
{
	private final Session session;

	@Inject
	Fetcher(Session session)
	{
		this.session = checkNotNull(session);
	}

	public <T extends TemporalEntity> T fetch(Class<T> type, Object id) throws SQLException
	{
		return fetch(Instant.now(), type, id);
	}

	@SuppressWarnings("unchecked")
	public <T extends TemporalEntity> T fetch(Instant instant, Class<T> type, Object id) throws SQLException
	{
		checkNotNull(instant, "instant");
		checkNotNull(type, "type");
		checkNotNull(id, "id");

		// The database uses open/closed validity intervals: [a;b[
		// When a record is 'closed' (no longer valid) its ValidTo
		// is set to the same value as its ValidFrom. Therefore the
		// ValidTo must be checked using the '<' operator.
		
		String keyColumn = Entities.getIdColumnNameOfEntity(type);
		String entityName = type.getCanonicalName();
		
		Query query = session.createQuery(format("FROM %s WHERE %s = :id AND ValidFrom <= :instant AND :instant < ValidTo", entityName, keyColumn));

		query.setParameter("id", id);
		query.setTimestamp("instant", instant.toDate());
		
		// This query should only ever return a single result or the database's
		// structure has been corrupted. So we want an exception to be thrown.
		
		return (T) query.uniqueResult();
	}
}
