// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package com.trifork.stamdata.replication.logging;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.inject.Inject;

/**
 * Logs audit messages to the database.
 * 
 * This class is also used as a data access class to the log entries.
 * 
 * @see LogController
 * 
 * @author Thomas Børlum (thb@trifork.com)
 */
public class DatabaseAuditLogger implements AuditLogger {

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

	public void log(String message, Object... args) {

		write(format(message, args));
	}

	public void write(String message) {

		LogEntry entry = new LogEntry(message);
		em.persist(entry);
	}
}
