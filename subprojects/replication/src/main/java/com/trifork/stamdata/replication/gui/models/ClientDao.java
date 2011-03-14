package com.trifork.stamdata.replication.gui.models;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import com.google.inject.Inject;


public class ClientDao {

	private final EntityManager em;

	@Inject
	ClientDao(EntityManager em) {

		this.em = em;
	}

	public Client find(String id) {

		return em.find(Client.class, id);
	}

	public Client findByCvr(String cvr) {

		TypedQuery<Client> query = em.createQuery("FROM Client WHERE cvr = :cvr", Client.class);
		query.setParameter("cvr", cvr);
		return query.getSingleResult();
	}

	public boolean delete(String id) {

		Query query = em.createQuery("DELETE Client WHERE id = :id");
		query.setParameter("id", id);
		return query.executeUpdate() == 1;
	}

	public Client create(String name, String cvr) {

		Client client = new Client(name, cvr);
		em.persist(client);
		return client;
	}

	public List<Client> findAll() {

		return em.createQuery("FROM Client ORDER BY name", Client.class).getResultList();
	}

	public void update(Client client) {

		em.persist(client);
	}
}
