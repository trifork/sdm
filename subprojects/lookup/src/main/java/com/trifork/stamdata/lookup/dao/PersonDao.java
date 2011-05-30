package com.trifork.stamdata.lookup.dao;

import java.util.Date;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.trifork.stamdata.views.cpr.Folkekirkeoplysninger;
import com.trifork.stamdata.views.cpr.Person;
import com.trifork.stamdata.views.cpr.Statsborgerskab;

public class PersonDao {

	private final Session session;

	@Inject
	public PersonDao(Session session) {
		this.session = session;
	}
	
	public CurrentPersonData get(String cpr) {
		Person person = getCurrentRecordByCpr(Person.class, cpr);
		Folkekirkeoplysninger folkekirkeoplysninger = getCurrentRecordByCpr(Folkekirkeoplysninger.class, cpr);
		Statsborgerskab statsborgerskab = getCurrentRecordByCpr(Statsborgerskab.class, cpr);
		return new CurrentPersonData(person, folkekirkeoplysninger, statsborgerskab);
	}
	
	private <T> T getCurrentRecordByCpr(Class<T> entityType, String cpr) {
		return entityType.cast( session
		.createCriteria(entityType)
		.add(Restrictions.eq("cpr", cpr))
		.add(Restrictions.le("validFrom", new Date()))
		.addOrder(Order.desc("validFrom"))
		.setMaxResults(1)
		.uniqueResult());
		
	}
}
