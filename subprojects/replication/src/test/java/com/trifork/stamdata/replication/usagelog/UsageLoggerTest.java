package com.trifork.stamdata.replication.usagelog;

import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.trifork.stamdata.replication.DatabaseHelper;
import com.trifork.stamdata.replication.replication.views.usagelog.UsageLogEntry;


public class UsageLoggerTest {
	private static Session session;
	private UsageLogger dao;

	@BeforeClass
	public static void init() throws Exception {
		DatabaseHelper db = new DatabaseHelper(UsageLogEntry.class);
		session = db.openSession();
		Query query = session.createQuery("delete from UsageLogEntry");
		query.executeUpdate();
	}

	@Before
	public void setUp() {
		dao = new UsageLogger(session);
		session.beginTransaction();
	}

	@After
	public void tearDown() {
		session.getTransaction().rollback();
	}
	
	@Test
	public void canSaveUsageLogInformation() {
		dao.log("CVR:12345678", "/my/objects/v1", 30);
		
		List<?> logs = session.createCriteria(UsageLogEntry.class).list();
		assertEquals(1, logs.size());
		UsageLogEntry entry = (UsageLogEntry) logs.get(0);
		assertEquals("CVR:12345678", entry.clientId);
		assertNotNull(entry.modifiedDate);
		assertEquals("/my/objects/v1", entry.type);
		assertEquals(30, entry.amount);
	}

}
