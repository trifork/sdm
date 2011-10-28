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

package com.trifork.stamdata.authorization.models;


import static com.trifork.stamdata.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.inject.Inject;

/**
 * Data access object for authorizations.
 * 
 * This class is thread safe.
 * 
 * @author Thomas Børlum (thb@trifork.com)
 */
public class AuthorizationDao {

	private final Session session;

	@Inject	
	AuthorizationDao(Session session) throws IOException {

		this.session = checkNotNull(session);
	}

	@SuppressWarnings("unchecked")
	public List<Authorization> getAuthorizations(String cpr) {

		Query query = session.createQuery("FROM Authorization WHERE cpr = :cpr");
		query.setParameter("cpr", cpr);

		List<Authorization> authorizations = query.list();

		return authorizations;
	}
}
