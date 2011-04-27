
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.util.List;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import com.trifork.stamdata.replication.gui.models.Client;
import com.trifork.stamdata.replication.gui.models.ClientDao;

@Ignore
public class ClientRepositoryTest {

	private final String clientName = "TestClient";

	private ClientDao clientDao;

	@Before
	public void setUp() {

		/*
		 * <property name="hibernate.connection.username" value="sa"/> <property
		 * name="hibernate.connection.password" value="cliente"/> <property
		 * name="hibernate.connection.url" value="jdbc:h2:~/db/offline"/>
		 * <property name="hibernate.connection.driver_class"
		 * value="org.h2.Drive"/> <property name="hibernate.hbm2dll.auto"
		 * value="create"/> <property name="hibernate.show_sql" value="true"/>
		 * <property name="hibernate.format_sql" value="true"/>
		 */
	}

	@Test
	public void cannot_find_client_with_unknown_id() throws Exception {

		Client client = clientDao.find("UnknownId");
		assertNull(client);
	}

	@Test
	public void can_find_client_by_id() throws Exception {

		Client client = clientDao.create("name", "certificateId");
		Client fetchedClient = clientDao.find(client.getId());
		assertThat(fetchedClient.getId(), equalTo(client.getId()));
	}

	@Test
	public void can_find_client_by_certificate_id() throws Exception {

		// Arrange

		String certificateId = RandomStringUtils.randomAlphabetic(10);
		clientDao.create(clientName, certificateId);

		// Act

		Client client = clientDao.findByCvr(certificateId);

		// Assert

		assertThat(client.getName(), is(clientName));
		assertThat(client.getCvrNumber(), is(certificateId));
	}

	@Test
	public void null_result_when_searching_by_certificate_id_which_does_not_exists() throws Exception {

		// Arrange

		// Notice we are not inserting the client first.

		// Act

		Client client = clientDao.findByCvr(clientName);

		// Assert

		assertNull(client);
	}

	@Test
	public void can_delete_client() throws Exception {

		Client client = clientDao.create("name", "certificateId");
		clientDao.delete(client.getId());
		Client deletedClient = clientDao.find(client.getId());

		assertNull(deletedClient);
	}

	@Test
	public void can_find_all_clients() throws Exception {

		// Arrange
		clientDao.create("name1", "certificateId");
		clientDao.create("name2", "certificateId");

		// Act
		List<Client> result = clientDao.findAll();

		// Assert
		assertTrue(result.size() > 1);
	}
}
