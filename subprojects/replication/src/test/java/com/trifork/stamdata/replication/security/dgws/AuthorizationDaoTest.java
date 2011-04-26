package com.trifork.stamdata.replication.security.dgws;

import static com.trifork.stamdata.replication.TimeHelper.tomorrow;
import static com.trifork.stamdata.replication.TimeHelper.yesterday;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.trifork.stamdata.replication.DatabaseHelper;
import com.trifork.stamdata.replication.TokenHelper;
import com.trifork.stamdata.replication.mocks.MockEntity;


@RunWith(MockitoJUnitRunner.class)
public class AuthorizationDaoTest {

	private static Session session;
	private AuthorizationDao dao;
	private byte[] token = TokenHelper.createRandomToken();

	@BeforeClass
	public static void init() throws Exception {
		DatabaseHelper db = new DatabaseHelper(Authorization.class);
		session = db.openSession();
	}

	@Before
	public void setUp() {
		dao = new AuthorizationDao(session);
		session.beginTransaction();
	}

	@After
	public void tearDown() {
		session.getTransaction().rollback();
	}

	@Test
	public void should_return_true_if_the_requested_authorization_exists() {
		
		Authorization authorization = new Authorization(MockEntity.class, "12345678", tomorrow(), token);

		dao.save(authorization);

		assertTrue(dao.isTokenValid(token, "foo/bar/v1"));
	}

	@Test
	public void should_return_false_if_the_requested_authorization_does_not_exist() {

		assertFalse(dao.isTokenValid(token, "foo/bar/v1"));
	}
	
	@Test
	public void should_return_false_if_the_authorization_has_expired() {

		Authorization authorization = new Authorization(MockEntity.class, "12345678", yesterday(), token);

		dao.save(authorization);
		
		assertFalse(dao.isTokenValid(token, "foo/bar/v1"));
	}
	
	@Test
	public void should_return_false_if_the_authorization_is_not_valid_from_the_requested_view() {

		Authorization authorization = new Authorization(MockEntity.class, "12345678", tomorrow(), token);

		dao.save(authorization);
		
		assertFalse(dao.isTokenValid(token, "bas/baz/v2"));
	}
	
	@Test
	public void can_find_cvr_from_authentication_token() {
		Authorization authorization = new Authorization(MockEntity.class, "12345678", tomorrow(), token);

		dao.save(authorization);
		
		assertEquals("12345678", dao.findCvr(token));
	}
}
