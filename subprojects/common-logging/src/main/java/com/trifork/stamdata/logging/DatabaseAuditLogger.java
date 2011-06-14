
// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
//
// Contributor(s): Contributors are attributed in the source code
// where applicable.
//
// The Original Code is "Stamdata".
//
// The Initial Developer of the Original Code is Trifork Public A/S.
//
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
//
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.logging;

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
