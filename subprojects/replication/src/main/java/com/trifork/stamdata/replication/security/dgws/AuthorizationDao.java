package com.trifork.stamdata.replication.security.dgws;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import com.google.inject.Inject;


public class AuthorizationDao {

	private final EntityManager em;

	@Inject
	AuthorizationDao(EntityManager em) {

		this.em = em;
	}

	public boolean isClientAuthorized(String cvr, String viewName) {

		Query q = em.createQuery("COUNT(a) FROM Authorization a WHERE cvr = :cvr AND viewName = :viewName");
		q.setParameter("cvr", cvr);
		q.setParameter("viewName", viewName);
		return 1 == (Long) q.getSingleResult();
	}

	public boolean isTokenStillValid(byte[] authorizationToken) {

		Query q = em.createQuery("SELECT COUNT(a) FROM Authorization a WHERE token = :token");
		q.setParameter("token", authorizationToken);
		return 1 == (Long) q.getSingleResult();
	}

	public void save(Authorization authorization) {

		em.persist(authorization);
	}
}
