
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

package com.trifork.stamdata.replication.gui.models;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
public class UserDao {

	private final Session session;

	@Inject
	public UserDao(Session session) {

		this.session = session;
	}

	public User find(String id) {

		return (User) session.load(User.class, id);
	}

	public User findByCvrAndRid(String cvr, String rid) {
		Query query = session.createQuery("FROM User WHERE (rid = :rid AND cvr = :cvr)");
		query.setParameter("rid", rid);
		query.setParameter("cvr", cvr);
		return (User) query.uniqueResult();
		
	}
	
	public User find(String cvr, String cpr) throws SQLException {

		Query query = session.createQuery("FROM User WHERE (cpr = :cpr AND cvr = :cvr)");
		query.setParameter("cpr", cpr);
		query.setParameter("cvr", cvr);
		return (User) query.uniqueResult();
	}

	public User create(String name, String cpr, String cvr, String rid) {

		User user = new User(name, cpr, cvr, rid);
		session.save(user);
		return user;
	}

	@SuppressWarnings("unchecked")
	public List<User> findAll() {

		return session.createQuery("FROM User ORDER BY name").list();
	}

	public void delete(String id) {

		Query query = session.createQuery("DELETE User WHERE (id = :id)");
		query.setParameter("id", id);
		query.executeUpdate();
	}

	public boolean exists(String userCPR, String userCVR) {

		Query query = session.createQuery("SELECT COUNT(u) FROM User u WHERE (cpr = :cpr, cvr = :cvr)");
		query.setParameter("cpr", userCPR);
		query.setParameter("cvr", userCVR);
		return 1 == (Long) query.uniqueResult();
	}
}
