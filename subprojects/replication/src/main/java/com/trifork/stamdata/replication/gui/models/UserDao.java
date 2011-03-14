package com.trifork.stamdata.replication.gui.models;

import java.sql.SQLException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import com.google.inject.Inject;


public class UserDao {

	private final EntityManager em;

	@Inject
	public UserDao(EntityManager em) {

		this.em = em;
	}

	public User find(String id) {

		return em.find(User.class, id);
	}

	public User find(String cvr, String cpr) throws SQLException {

		TypedQuery<User> query = em.createQuery("FROM User WHERE (cpr = :cpr AND cvr = :cvr)", User.class);
		query.setParameter("cpr", cpr);
		query.setParameter("cvr", cvr);
		return query.getSingleResult();
	}

	public User create(String name, String cpr, String cvr) {

		User user = new User(name, cpr, cvr);
		em.persist(user);
		return user;
	}

	public List<User> findAll() {

		return em.createQuery("FROM User ORDER BY name", User.class).getResultList();
	}

	public void delete(String id) {

		Query query = em.createQuery("DELETE User WHERE (id = :id)");
		query.setParameter("id", id);
		query.executeUpdate();
	}

	public boolean exists(String userCPR, String userCVR) {

		Query query = em.createQuery("SELECT COUNT(u) FROM User u WHERE (cpr = :cpr, cvr = :cvr)");
		query.setParameter("cpr", userCPR);
		query.setParameter("cvr", userCVR);
		return 1 == (Long) query.getSingleResult();
	}
}
