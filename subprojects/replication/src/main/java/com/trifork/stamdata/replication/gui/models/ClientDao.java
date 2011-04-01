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

package com.trifork.stamdata.replication.gui.models;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.inject.Inject;


public class ClientDao {

	private final Session session;

	@Inject
	ClientDao(Session session) {

		this.session = session;
	}

	public Client find(String id) {

		return (Client) session.load(Client.class, id);
	}

	public Client findByCvr(String cvr) {

		Query query = session.createQuery("FROM Client WHERE cvr = :cvr");
		query.setParameter("cvr", cvr);
		return (Client) query.uniqueResult();
	}

	public boolean delete(String id) {

		Query query = session.createQuery("DELETE Client WHERE id = :id");
		query.setParameter("id", id);
		return query.executeUpdate() == 1;
	}

	public Client create(String name, String cvr) {

		Client client = new Client(name, cvr);
		session.persist(client);
		return client;
	}

	@SuppressWarnings("unchecked")
	public List<Client> findAll() {

		return session.createQuery("FROM Client ORDER BY name").list();
	}

	public void update(Client client) {

		session.persist(client);
	}
}
