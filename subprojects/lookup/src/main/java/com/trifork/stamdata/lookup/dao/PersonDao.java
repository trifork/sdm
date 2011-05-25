package com.trifork.stamdata.lookup.dao;

import java.util.Date;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.trifork.stamdata.views.cpr.Person;

public class PersonDao {

	private final Session session;

	@Inject
	public PersonDao(Session session) {
		this.session = session;
	}
	
	public CurrentPersonData get(String cpr) {
		Person person = (Person) session
			.createCriteria(Person.class)
			.add(Restrictions.eq("cpr", cpr))
			.add(Restrictions.le("validFrom", new Date()))
			.addOrder(Order.desc("validFrom"))
			.setMaxResults(1)
			.uniqueResult();
		return new CurrentPersonData(person);
	}
}
