package com.trifork.stamdata.replication.util;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.inject.Inject;


public class DatabaseAuditLogger {

	private final Session em;

	@Inject
	DatabaseAuditLogger(Session em) {

		this.em = em;
	}

	@SuppressWarnings("unchecked")
	public List<LogEntry> findAll(int offset, int count) {

		checkArgument(offset >= 0);
		checkArgument(count > 0);
		
		Query query = em.createQuery("FROM LogEntry ORDER BY createdAt DESC");
		query.setMaxResults(10);
		query.setFirstResult(offset);
		return query.list();
	}

	public boolean write(String message, Object... args) {

		return write(format(message, args));
	}

	public boolean write(String message) {

		LogEntry entry = new LogEntry(message);
		em.persist(entry);
		return true;
	}
}
