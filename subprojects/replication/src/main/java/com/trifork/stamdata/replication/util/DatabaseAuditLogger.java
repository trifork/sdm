package com.trifork.stamdata.replication.util;

import static com.google.inject.internal.Preconditions.checkArgument;
import static java.lang.String.format;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import com.google.inject.Inject;


public class DatabaseAuditLogger {

	private final EntityManager em;

	@Inject
	DatabaseAuditLogger(EntityManager em) {

		this.em = em;
	}

	public List<LogEntry> findAll(int offset, int count) {

		checkArgument(offset >= 0);
		checkArgument(count > 0);
		
		TypedQuery<LogEntry> query = em.createQuery("FROM LogEntry ORDER BY createdAt DESC", LogEntry.class);
		query.setMaxResults(10);
		query.setFirstResult(offset);
		return query.getResultList();
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
