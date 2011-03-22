package com.trifork.stamdata.replication.replication;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import org.hibernate.ejb.QueryHints;
import com.google.inject.Inject;
import com.trifork.stamdata.replication.replication.views.View;


public class RecordDao {

	private final EntityManager em;

	@Inject
	RecordDao(EntityManager em) {

		this.em = em;
	}

	public <T extends View> List<T> findPage(Class<T> type, String recordId, Date modifiedDate, int limit) {

		// TODO: This convertion should not take place here.
		
		BigInteger id = new BigInteger(recordId);
		
		// CREATE A QUERY
		//

		String SQL = "FROM " + type.getName() + " WHERE (recordID = :recordID AND modifiedDate > :modifiedDate) OR (recordID > :recordID) ORDER BY recordID, modifiedDate";

		TypedQuery<T> query = em.createQuery(SQL, type);
		query.setParameter("recordID", id);
		query.setParameter("modifiedDate", modifiedDate);
		query.setMaxResults(limit);
		query.setHint(QueryHints.HINT_READONLY, true);

		// FETCH THE RECORDS
		//

		return query.getResultList();
	}
}
