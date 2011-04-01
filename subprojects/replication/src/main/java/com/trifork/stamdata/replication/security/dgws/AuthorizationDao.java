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

package com.trifork.stamdata.replication.security.dgws;

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

	public boolean isTokenStillValid(byte[] authorizationToken) {

		Query q = em.createQuery("SELECT COUNT(a) FROM Authorization a WHERE token = :token");
		q.setParameter("token", authorizationToken);
		return 1 == (Long) q.uniqueResult();
	}

	public void save(Authorization authorization) {

		em.persist(authorization);
	}
}
