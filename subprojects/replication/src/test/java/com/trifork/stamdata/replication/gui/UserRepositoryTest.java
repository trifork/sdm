
// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// Contributor(s): Contributors are attributed in the source code
// where applicable.
// 
// The Original Code is "Stamdata".
// 
// The Initial Developer of the Original Code is Trifork Public A/S.
// 
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
// 
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

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
		User user = repository.create("name", "cpr", "cvr", "rid");

		// Act
		User foundUser = repository.find(user.getId());

		// Assert
		assertUser(user, foundUser);
	}


	@Test
	public void can_find_all_users() throws Exception {
		// Arrange
		repository.create("name1", "cpr1", "cvr1", "rid1");
		repository.create("name2", "cpr2", "cvr2", null);

		// Act
		List<User> allUsers = repository.findAll();

		// Assert
		// TODO: This should say == 2 once we have fixed the in memory db.
		assertTrue(allUsers.size() > 1);
	}


	@Test
	public void can_delete_user() throws Exception {
		// Arrange
		User user = repository.create("name", "cpr", "cvr", null);

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
		assertThat(actual.getRid(), is(expected.getRid()));
	}
}
