package com.trifork.stamdata.lookup.dao;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.trifork.stamdata.views.cpr.Civilstand;
import com.trifork.stamdata.views.cpr.Foedselsregistreringsoplysninger;
import com.trifork.stamdata.views.cpr.Folkekirkeoplysninger;
import com.trifork.stamdata.views.cpr.Person;
import com.trifork.stamdata.views.cpr.Statsborgerskab;
import com.trifork.stamdata.views.cpr.Udrejseoplysninger;
import com.trifork.stamdata.views.cpr.UmyndiggoerelseVaergeRelation;

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
		Foedselsregistreringsoplysninger fr = getCurrentRecordByCpr(Foedselsregistreringsoplysninger.class, cpr);
		Civilstand civilstand = getCurrentRecordByCpr(Civilstand.class, cpr);
		Udrejseoplysninger udrejseoplysninger = getCurrentRecordByCpr(Udrejseoplysninger.class, cpr);
		UmyndiggoerelseVaergeRelation vaerge = getCurrentRecordByCpr(UmyndiggoerelseVaergeRelation.class, cpr);
		List<UmyndiggoerelseVaergeRelation> vaergemaal = getVaergemaal(cpr);
		return new CurrentPersonData(person, folkekirkeoplysninger, statsborgerskab, fr, civilstand, udrejseoplysninger, vaerge, vaergemaal);
	}

	private <T> T getCurrentRecordByCpr(Class<T> entityType, String cpr) {
		return entityType.cast(session.createCriteria(entityType)
				.add(Restrictions.eq("cpr", cpr))
				.add(Restrictions.le("validFrom", new Date()))
				.addOrder(Order.desc("validFrom"))
				.setMaxResults(1)
				.uniqueResult());
	}
	
	@SuppressWarnings("unchecked")
	private List<UmyndiggoerelseVaergeRelation> getVaergemaal(String cpr) {
		Date now = new Date();
		return (List<UmyndiggoerelseVaergeRelation>) session.createCriteria(UmyndiggoerelseVaergeRelation.class)
				.add(Restrictions.eq("relationCpr", cpr))
				.add(Restrictions.le("validFrom", now))
				.add(Restrictions.ge("validTo", now))
				.addOrder(Order.desc("validFrom"))
				.list();
	}
}
