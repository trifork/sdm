package com.trifork.sdm.replication.admin;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;

import com.trifork.sdm.replication.GuiceTest;
import com.trifork.sdm.replication.admin.models.*;


public class UserRepositoryTest extends GuiceTest
{
	private UserRepository repository;


	@Before
	public void setUp()
	{
		repository = getInjector().getInstance(UserRepository.class);
	}


	@Test
	public void can_find_user_by_id() throws Exception
	{
		// Arrange
		User user = repository.create("name", "cpr", "cvr");

		// Act
		User foundUser = repository.find(user.getId());

		// Assert
		assertUser(user, foundUser);
	}


	@Test
	public void can_find_all_users() throws Exception
	{
		// Arrange
		repository.create("name1", "cpr1", "cvr1");
		repository.create("name2", "cpr2", "cvr2");

		// Act
		List<User> allUsers = repository.findAll();

		// Assert
		// TODO: This should say == 2 once we have fixed the in memory db.
		assertTrue(allUsers.size() > 1);
	}


	@Test
	public void can_delete_user() throws Exception
	{
		// Arrange
		User user = repository.create("name", "cpr", "cvr");

		// Act
		repository.destroy(user.getId());

		// Assert
		assertNull(repository.find(user.getId()));
	}


	private void assertUser(User expected, User actual)
	{
		assertThat(actual.getId(), is(expected.getId()));
		assertThat(actual.getName(), is(expected.getName()));
		assertThat(actual.getCpr(), is(expected.getCpr()));
		assertThat(actual.getCvr(), is(expected.getCvr()));
	}
}
