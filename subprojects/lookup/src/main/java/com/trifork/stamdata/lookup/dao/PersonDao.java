package com.trifork.stamdata.lookup.dao;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.trifork.stamdata.views.cpr.BarnRelation;
import com.trifork.stamdata.views.cpr.Civilstand;
import com.trifork.stamdata.views.cpr.Foedselsregistreringsoplysninger;
import com.trifork.stamdata.views.cpr.Folkekirkeoplysninger;
import com.trifork.stamdata.views.cpr.MorOgFaroplysninger;
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
		List<BarnRelation> boern = getBoern(cpr);
		List<MorOgFaroplysninger> morOgFarOplysninger = getCurrentRecordsByCpr(MorOgFaroplysninger.class, cpr);
		MorOgFaroplysninger morOplysinger = null, farOplysinger = null;
		for(MorOgFaroplysninger oplysninger : morOgFarOplysninger) {
			if("M".equals(oplysninger.foraelderkode)) {
				morOplysinger = oplysninger;
			}
			else if ("F".equals(oplysninger.foraelderkode)) {
				farOplysinger = oplysninger;
			}
		}
		return new CurrentPersonData(person, folkekirkeoplysninger, statsborgerskab, fr, civilstand, udrejseoplysninger, vaerge, vaergemaal, boern, morOplysinger, farOplysinger);
	}

	private List<UmyndiggoerelseVaergeRelation> getVaergemaal(String cpr) {
		return getCurrentRecordsByProperty(UmyndiggoerelseVaergeRelation.class, "relationCpr", cpr);
	}
	
	private List<BarnRelation> getBoern(String cpr) {
		return getCurrentRecordsByCpr(BarnRelation.class, cpr);
	}
	
	private List<BarnRelation> getForaeldre(String cpr) {
		return getCurrentRecordsByProperty(BarnRelation.class, "barnCPR", cpr);
	}
	
	private Criteria buildValidCriteria(Class<?> entityClass) {
		Date now = new Date();
		return session.createCriteria(entityClass)
		.add(Restrictions.le("validFrom", now))
		.add(Restrictions.or(Restrictions.isNull("validTo"), Restrictions.ge("validTo", now)))
		.addOrder(Order.desc("validFrom"));
	}

	private <T> T getCurrentRecordByCpr(Class<T> entityType, String cpr) {
		return entityType.cast(buildValidCriteria(entityType)
				.add(Restrictions.eq("cpr", cpr))
				.setMaxResults(1)
				.uniqueResult());
	}
	
	private <T> List<T> getCurrentRecordsByCpr(Class<T> entityType, String cpr) {
		return getCurrentRecordsByProperty(entityType, "cpr", cpr);
	}
	
	@SuppressWarnings("unchecked")
	private <T> List<T> getCurrentRecordsByProperty(Class<T> entityType, String propertyName, String propertyValue) {
		return (List<T>) buildValidCriteria(entityType).add(Restrictions.eq(propertyName, propertyValue)).list();
	}
	
}
