package com.trifork.sdm.replication.gui;


import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.*;

import com.trifork.sdm.replication.GuiceTest;
import com.trifork.sdm.replication.gui.models.*;


public class ClientRepositoryTest extends GuiceTest
{
	private final String clientName = "TestClient";

	private ClientDao repository;


	@Before
	public void setUp()
	{
		repository = getInjector().getInstance(ClientDao.class);
	}


	@Test
	public void cannot_find_client_with_unknown_id() throws Exception
	{
		// Act
		Client client = repository.find("IllegalId");

		// Assert
		assertNull(client);
	}


	@Test
	public void can_find_client_by_id() throws Exception
	{
		// Arrange
		Client client = repository.create("name", "certificateId");

		// Act
		Client fetchedClient = repository.find(client.getId());

		// Assert
		assertThat(fetchedClient.getId(), equalTo(client.getId()));
	}


	@Test
	public void can_find_client_by_certificate_id() throws Exception
	{
		// Arrange

		String certificateId = RandomStringUtils.randomAlphabetic(10);
		repository.create(clientName, certificateId);

		// Act

		Client client = repository.findByCertificateId(certificateId);

		// Assert

		assertThat(client.getName(), is(clientName));
		assertThat(client.getCertificateId(), is(certificateId));
	}


	@Test
	public void null_result_when_searching_by_certificate_id_which_does_not_exists() throws Exception
	{
		// Arrange

		// Notice we are not inserting the client first.

		// Act

		Client client = repository.findByCertificateId(clientName);

		// Assert

		assertNull(client);
	}


	@Test
	public void can_delete_client() throws Exception
	{
		// Arrange
		Client client = repository.create("name", "certificateId");

		// Act
		repository.destroy(client.getId());

		// Assert
		Client deletedClient = repository.find(client.getId());
		assertNull(deletedClient);
	}


	@Test
	public void can_find_all_clients() throws Exception
	{
		// Arrange
		repository.create("name1", "certificateId");
		repository.create("name2", "certificateId");

		// Act
		List<Client> result = repository.findAll();

		// Assert
		assertTrue(result.size() > 1);
	}
}
