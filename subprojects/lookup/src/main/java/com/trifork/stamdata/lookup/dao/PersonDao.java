package com.trifork.stamdata.lookup.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.views.cpr.BarnRelation;
import com.trifork.stamdata.views.cpr.Beskyttelse;
import com.trifork.stamdata.views.cpr.Civilstand;
import com.trifork.stamdata.views.cpr.Foedselsregistreringsoplysninger;
import com.trifork.stamdata.views.cpr.Folkekirkeoplysninger;
import com.trifork.stamdata.views.cpr.ForaeldremyndighedsRelation;
import com.trifork.stamdata.views.cpr.MorOgFaroplysninger;
import com.trifork.stamdata.views.cpr.Person;
import com.trifork.stamdata.views.cpr.Statsborgerskab;
import com.trifork.stamdata.views.cpr.Udrejseoplysninger;
import com.trifork.stamdata.views.cpr.UmyndiggoerelseVaergeRelation;

public class PersonDao {
	private static final Logger logger = LoggerFactory.getLogger(PersonDao.class);
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
			else {
				logger.error("Ukendt foraelderkode: {}, cpr={}", oplysninger.foraelderkode, cpr);
			}
		}
		List<ForaeldremyndighedsRelation> foraeldreMyndighedIndehavere = getCurrentRecordsByCpr(ForaeldremyndighedsRelation.class, cpr);
		List<ForaeldremyndighedsRelation> foraeldremyndighedBoern = getForaeldremyndighedBoern(
				cpr, boern);
		List<Beskyttelse> beskyttelser = getCurrentRecordsByCpr(Beskyttelse.class, cpr);
		return new CurrentPersonData(person, folkekirkeoplysninger, statsborgerskab, fr, civilstand, udrejseoplysninger, vaerge, vaergemaal, boern, morOplysinger, farOplysinger, foraeldreMyndighedIndehavere, foraeldremyndighedBoern, beskyttelser);
	}

	private List<ForaeldremyndighedsRelation> getForaeldremyndighedBoern(
			String cpr, List<BarnRelation> boern) {
		List<ForaeldremyndighedsRelation> parentAuthorityThroughParenthood = getParentAuthorityThroughParenthood(boern, cpr);
		List<ForaeldremyndighedsRelation> parentAuthorityThroughOther = getCurrentRecordsByProperty(ForaeldremyndighedsRelation.class, "relationCpr", cpr);
		List<ForaeldremyndighedsRelation> foraeldremyndighedBoern = new ArrayList<ForaeldremyndighedsRelation>();
		foraeldremyndighedBoern.addAll(parentAuthorityThroughParenthood);
		foraeldremyndighedBoern.addAll(parentAuthorityThroughOther);
		return foraeldremyndighedBoern;
	}
	
	@SuppressWarnings("unchecked")
	private List<ForaeldremyndighedsRelation> getParentAuthorityThroughParenthood(List<BarnRelation> boern, String parentCpr) {
		if(boern.isEmpty()) {
			return Collections.emptyList();
		}
		List<String> childrenCprs = new ArrayList<String>();
		for(BarnRelation barnRelation : boern) {
			childrenCprs.add(barnRelation.barnCPR);
		}
		Date now = new Date();
		Criteria crit =  session.createCriteria(ForaeldremyndighedsRelation.class)
		.add(Restrictions.le("validFrom", now))
		.add(Restrictions.or(Restrictions.isNull("validTo"), Restrictions.ge("validTo", now)))
		.add(Restrictions.or(Restrictions.eq("typeKode", "0003"), Restrictions.eq("typeKode", "0004")))
		.add(Restrictions.in("cpr", childrenCprs));
		return crit.list();
	}

	private List<UmyndiggoerelseVaergeRelation> getVaergemaal(String cpr) {
		return getCurrentRecordsByProperty(UmyndiggoerelseVaergeRelation.class, "relationCpr", cpr);
	}
	
	private List<BarnRelation> getBoern(String cpr) {
		return getCurrentRecordsByCpr(BarnRelation.class, cpr);
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
