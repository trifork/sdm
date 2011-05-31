package com.trifork.stamdata.authorization;


import static com.trifork.stamdata.authorization.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.inject.Inject;

/**
 *  Data access object for authorizations.
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
