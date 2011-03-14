package com.trifork.sdm.replication.replication;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.google.inject.Inject;
import com.trifork.sdm.replication.replication.models.Record;
import com.trifork.sdm.replication.util.HistoryOffset;

public class RecordDao {

	private final EntityManager em;


	@Inject
	RecordDao(EntityManager em) {
		this.em = em;
	}


	public <T extends Record> List<T> find(Class<T> type, HistoryOffset revision, int limit) {
		 
		// CREATE A QUERY
		// 

		String SQL = "FROM " + type.getName() + " WHERE (recordID > :recordID AND modifiedDate = :modifiedDate) OR (modifiedDate > :modifiedDate) ORDER BY recordID, modifiedDate";

		TypedQuery<T> query = em.createQuery(SQL, type);
		query.setParameter("recordID", revision.getRecordID());
		query.setParameter("modifiedDate", revision.getModifiedDate());
		query.setMaxResults(limit);

		// FETCH THE RECORDS
		//

		return query.getResultList();
	}
}
