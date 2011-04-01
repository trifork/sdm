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

package com.trifork.stamdata.replication.replication;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;
import java.util.Date;

import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.StatelessSession;

import com.google.inject.Inject;
import com.trifork.stamdata.replication.replication.views.View;


public class RecordDao {

	private final StatelessSession em;

	@Inject
	RecordDao(StatelessSession em) {

		this.em = checkNotNull(em);
	}

	public <T extends View> ScrollableResults findPage(Class<T> type, String recordId, Date modifiedDate, int limit) {

		checkNotNull(type);
		checkNotNull(recordId);
		checkNotNull(modifiedDate);
		checkArgument(limit > 0);

		// TODO: This conversion should not take place here.
		
		BigInteger id = new BigInteger(recordId);
		
		// CREATE A QUERY
		//

		String SQL = "FROM " + type.getName() + " WHERE (recordID = :recordID AND modifiedDate > :modifiedDate) OR (recordID > :recordID) ORDER BY recordID, modifiedDate";

		Query query = em.createQuery(SQL);
		query.setParameter("recordID", id);
		query.setParameter("modifiedDate", modifiedDate);
		query.setMaxResults(limit);

		// FETCH THE RECORDS
		//

		return query.scroll(ScrollMode.SCROLL_SENSITIVE);
	}
}
