package com.trifork.sdm.replication.replication;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.google.inject.Inject;
import com.trifork.sdm.replication.replication.annotations.Registry;
import com.trifork.sdm.replication.replication.models.Record;
import com.trifork.sdm.replication.util.HistoryOffset;

public class RegistryDao {

	private final EntityManager em;
	private final Map<String, Class<? extends Record>> registry;


	@Inject
	RegistryDao(EntityManager em, @Registry Map<String, Class<? extends Record>> registry) {
		this.em = em;
		this.registry = registry;
	}


	public List<? extends Record> find(String entityName, HistoryOffset revision, int limit) {
		
		// CREATE A QUERY
		// 

		String SQL = "FROM " + entityName + " WHERE (recordID > :recordID AND modifiedDate = :modifiedDate) OR (modifiedDate > :modifiedDate) ORDER BY recordID, modifiedDate";

		Class<? extends Record> c = registry.get(entityName);

		TypedQuery<? extends Record> query = em.createQuery(SQL, c);
		query.setParameter("recordID", revision.getRecordID());
		query.setParameter("modifiedDate", revision.getModifiedDate());
		query.setMaxResults(limit);

		// FETCH THE RECORDS
		//

		return query.getResultList();
	}
}
