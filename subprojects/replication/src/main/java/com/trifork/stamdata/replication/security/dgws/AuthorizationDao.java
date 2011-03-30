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
