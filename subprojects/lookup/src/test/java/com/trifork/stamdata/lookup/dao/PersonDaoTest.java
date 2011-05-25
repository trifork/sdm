package com.trifork.stamdata.lookup.dao;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.hibernate.classic.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.trifork.stamdata.replication.DatabaseHelper;
import com.trifork.stamdata.views.cpr.Person;

public class PersonDaoTest {
	private static DatabaseHelper db;
	private Session session;
	private PersonDao dao;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		db = new DatabaseHelper("lookup", Person.class);
		Session session = db.openSession();
		session.createQuery("delete from Person").executeUpdate();
		session.close();
	}

	@Before
	public void before() {
		session = db.openSession();
		session.getTransaction().begin();
		dao = new PersonDao(session);
	}
	
	@After
	public void after() {
		session.getTransaction().rollback();
		session.close();
	}
	
	@Test
	public void givesCurrentPersonDataWithContentsFromUnderlyingView() {
		session.save(person(at(2005, Calendar.JANUARY, 5)));
		
		CurrentPersonData person = dao.get("1020304050");
		assertEquals("1020304050", person.getCprNumber());
		assertEquals(at(2005, Calendar.JANUARY, 5), person.getValidFrom());
	}
	
	@Test
	public void getsNewestPersonRecordIfNoRecordIsInTheFuture() {
		session.save(person(at(2005, Calendar.JANUARY, 5)));
		session.save(person(at(2010, Calendar.FEBRUARY, 10)));
		
		CurrentPersonData person = dao.get("1020304050");
		assertEquals(at(2010, Calendar.FEBRUARY, 10), person.getValidFrom());
	}
	
	@Test
	public void getsCurrentPersonRecordIfRecordsInTheFuture() {
		session.save(person(at(2005, Calendar.JANUARY, 5)));
		session.save(person(at(2010, Calendar.FEBRUARY, 10)));
		session.save(person(at(2110, Calendar.FEBRUARY, 10)));
		
		CurrentPersonData person = dao.get("1020304050");
		assertEquals(at(2010, Calendar.FEBRUARY, 10), person.getValidFrom());
	}
	
	private Person person(Date validFrom) {
		Person result = new Person();
		result.cpr = "1020304050";
		result.koen = "M";
		result.foedselsdato = at(1975, Calendar.MAY, 12);
		result.modifiedBy = "AHJ";
		result.createdBy = "AHJ";
		result.validFrom = validFrom;
		result.modifiedDate = validFrom;
		result.createdDate = at(2005, Calendar.JANUARY, 5);
		return result;
	}
	
	private Date at(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		return calendar.getTime();
	}
}
