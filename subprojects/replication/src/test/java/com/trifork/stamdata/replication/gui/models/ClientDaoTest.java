package com.trifork.stamdata.replication.gui.models;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.trifork.stamdata.replication.DatabaseHelper;

public class ClientDaoTest {
	private Session session;
	private ClientDao dao;
	private Client createdClient;
	private static DatabaseHelper db;

	@BeforeClass
	public static void init() throws Exception {
		db = new DatabaseHelper(Client.class);
		Session session = db.openSession();
		session.beginTransaction();
		session.createQuery("delete from Client").executeUpdate();
		session.getTransaction().commit();
		session.close();
	}

	@Before
	public void setUp() {
		session = db.openSession();
		dao = new ClientDao(session);
		session.beginTransaction();
		createdClient = dao.create("test", "CVR:12345678-UID:1203980293");
	}

	@After
	public void tearDown() {
		session.getTransaction().rollback();
		session.close();
	}

	@Test
	public void canFindByCvr() {
		assertNotNull(dao.findByCvr("12345678"));
	}
	
	@Test
	public void willNotReturnWrongCvr() {
		assertNull(dao.findByCvr("12345679"));
	}
	
	@Test
	public void canFindBySubjectSerialNumber() {
		assertNotNull(dao.findBySubjectSerialNumber("CVR:12345678-UID:1203980293"));
	}
	@Test
	public void willNotReturnWrongSsn() {
		assertNull(dao.findBySubjectSerialNumber("CVR:12341234-UID:1203980293"));
	}
	
	@Test
	public void canDelete() {
		dao.delete(createdClient.getId());
		assertNull(dao.findBySubjectSerialNumber("CVR:12345678-UID:1203980293"));
	}
	
	@Test
	public void can_find_client_by_id() throws Exception {
		Client fetchedClient = dao.find(createdClient.getId());
		assertThat(fetchedClient.getId(), equalTo(createdClient.getId()));
	}
	
	@Test
	public void can_find_all_clients() throws Exception {

		// Arrange
		dao.create("name1", "ssn1");
		dao.create("name2", "ssn2");

		// Act
		List<Client> result = dao.findAll();

		// Assert
		assertEquals(3, result.size());
	}
	
}

