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

	public User find(String cvr, String cpr) throws SQLException {

		Query query = session.createQuery("FROM User WHERE (cpr = :cpr AND cvr = :cvr)");
		query.setParameter("cpr", cpr);
		query.setParameter("cvr", cvr);
		return (User) query.uniqueResult();
	}

	public User create(String name, String cpr, String cvr) {

		User user = new User(name, cpr, cvr);
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
