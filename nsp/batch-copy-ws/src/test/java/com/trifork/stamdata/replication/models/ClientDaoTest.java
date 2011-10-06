/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */
package com.trifork.stamdata.replication.models;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Set;

import org.hibernate.classic.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.trifork.stamdata.replication.DatabaseHelper;
import com.trifork.stamdata.replication.security.dgws.Authorization;
import com.trifork.stamdata.views.Views;

public class ClientDaoTest {
	private Session session;
	private ClientDao dao;
	private Client createdClient;
	private static DatabaseHelper db;

	@BeforeClass
	public static void init() throws Exception {
		Set<Class<?>> classes = Sets.newHashSet();
		classes.addAll(Views.findAllViews());
		classes.add(Authorization.class);
		classes.add(Client.class);
		db = new DatabaseHelper(classes.toArray(new Class[] {}));
		
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

