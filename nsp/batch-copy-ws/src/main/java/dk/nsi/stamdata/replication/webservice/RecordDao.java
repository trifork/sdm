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

package dk.nsi.stamdata.replication.webservice;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.inject.Inject;

import dk.nsi.stamdata.views.View;

public class RecordDao
{
	private final Session session;

	@Inject
	RecordDao(Session session)
	{
		this.session = checkNotNull(session);
	}

	@SuppressWarnings("unchecked")
    public <T extends View> List<T> findPage(Class<T> type, String recordId, Date modifiedDate, int limit)
	{
		checkNotNull(type);
		checkNotNull(recordId);
		checkNotNull(modifiedDate);
		checkArgument(limit > 0);

		// TODO: This conversion should not take place here.

		BigInteger id = new BigInteger(recordId);

		// CREATE A QUERY
		//
		// A record that has changed can be identified by looking at its
		// modified date,
		// in conjunction to its PID.
		//
		// This is because the importer imports new records in batches and uses
		// the same
		// time stamp for each of the records in to batch.

		String SQL = " FROM " + type.getName() + " WHERE ((recordID > :recordID AND modifiedDate = :modifiedDate) OR (recordID > :recordID) OR (recordID = :recordID AND modifiedDate > :modifiedDate)) ORDER BY recordID, modifiedDate";

        Query query = session.createQuery(SQL);
		query.setParameter("recordID", id);
		query.setParameter("modifiedDate", modifiedDate);
		query.setMaxResults(limit);
		
		// FETCH THE RECORDS
		//
		
		return (List<T>) query.list();
	}
}
