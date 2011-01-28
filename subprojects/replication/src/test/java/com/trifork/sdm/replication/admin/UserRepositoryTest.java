package com.trifork.sdm.replication.admin;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.List;

import org.junit.Test;

import com.trifork.sdm.replication.admin.models.User;

public class UserRepositoryTest extends RepositoryTest {
	@Test
	public void can_find_user_by_id() throws Exception {
		// Arrange
		User user = userRepository.create("name", "cpr", "cvr");
		
		// Act
		User foundUser = userRepository.find(user.getId());
		
		// Assert
		assertUser(user, foundUser);
	}
	
	@Test
	public void can_find_all_users() throws Exception {
		// Arrange
		userRepository.create("name1", "cpr1", "cvr1");
		userRepository.create("name2", "cpr2", "cvr2");
		
		// Act
		List<User> allUsers = userRepository.findAll();
		
		// Assert
		assertTrue(allUsers.size() > 1);
	}
	
	@Test
	public void can_delete_user() throws Exception {
		// Arrange
		User user = userRepository.create("name", "cpr", "cvr");
		
		// Act
		userRepository.destroy(user.getId());
		
		// Assert
		assertNull(userRepository.find(user.getId()));
	}

	private void assertUser(User expected, User actual) {
		assertThat(actual.getId(), is(expected.getId()));
		assertThat(actual.getName(), is(expected.getName()));
		assertThat(actual.getCpr(), is(expected.getCpr()));
		assertThat(actual.getCvr(), is(expected.getCvr()));
	}
}
