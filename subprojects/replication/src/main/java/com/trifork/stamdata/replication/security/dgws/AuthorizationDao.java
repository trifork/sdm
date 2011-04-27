
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

package com.trifork.stamdata.replication.security.dgws;

import static com.trifork.stamdata.Preconditions.checkNotNull;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.inject.Inject;


public class AuthorizationDao {

	private final Session em;

	@Inject
	AuthorizationDao(Session em) {

		this.em = em;
	}

	public boolean isClientAuthorized(String cvr, String viewName) {

		Query q = em.createQuery("SELECT COUNT(a) FROM Authorization a WHERE cvr = :cvr AND viewName = :viewName");
		q.setParameter("cvr", cvr);
		q.setParameter("viewName", viewName);
		return 1 == (Long) q.uniqueResult();
	}

	public boolean isTokenValid(byte[] authorizationToken, String viewName) {

		checkNotNull(authorizationToken);
		checkNotNull(viewName);

		Query q = em.createQuery("SELECT COUNT(a) FROM Authorization a WHERE token = :token AND viewName = :viewName AND expiresAt > NOW()");
		q.setParameter("token", authorizationToken);
		q.setParameter("viewName", viewName);
		return 0 < (Long) q.uniqueResult();
	}

	public void save(Authorization authorization) {

		em.persist(authorization);
	}

	public String findCvr(byte[] authorizationToken) {
		checkNotNull(authorizationToken);

		Query q = em.createQuery("SELECT cvr FROM Authorization a WHERE token = :token AND expiresAt > NOW()");
		q.setParameter("token", authorizationToken);
		return q.uniqueResult().toString();
	}
}
