// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package com.trifork.stamdata.replication.gui;


import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import com.trifork.stamdata.replication.gui.models.User;
import com.trifork.stamdata.replication.gui.models.UserDao;


@Ignore
public class UserRepositoryTest {

	private UserDao repository;


	@Before
	public void setUp() {
		
	}


	@Test
	public void can_find_user_by_id() throws Exception {
		// Arrange
		User user = repository.create("name", "cpr", "cvr");

		// Act
		User foundUser = repository.find(user.getId());

		// Assert
		assertUser(user, foundUser);
	}


	@Test
	public void can_find_all_users() throws Exception {
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
	public void can_delete_user() throws Exception {
		// Arrange
		User user = repository.create("name", "cpr", "cvr");

		// Act
		repository.delete(user.getId());

		// Assert
		assertNull(repository.find(user.getId()));
	}


	private void assertUser(User expected, User actual) {
		assertThat(actual.getId(), is(expected.getId()));
		assertThat(actual.getName(), is(expected.getName()));
		assertThat(actual.getCpr(), is(expected.getCpr()));
		assertThat(actual.getCvr(), is(expected.getCvr()));
	}
}
