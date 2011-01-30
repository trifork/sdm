package com.trifork.sdm.replication.admin;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import com.trifork.sdm.replication.admin.models.Client;


public class ClientRepositoryTest extends RepositoryTest
{
	// Test data.

	private final String clientName = "TestClient";


	@Test
	public void cannot_find_client_with_unknown_id() throws Exception
	{
		// Act
		Client client = clientRepository.find("IllegalId");

		// Assert
		assertNull(client);
	}


	@Test
	public void can_find_client_by_id() throws Exception
	{
		// Arrange
		Client client = clientRepository.create("name", "certificateId");

		// Act
		Client fetchedClient = clientRepository.find(client.getId());

		// Assert
		assertThat(fetchedClient.getId(), equalTo(client.getId()));
	}


	@Test
	public void can_find_client_by_certificate_id() throws Exception
	{
		// Arrange

		String certificateId = RandomStringUtils.randomAlphabetic(10);
		clientRepository.create(clientName, certificateId);

		// Act

		Client client = clientRepository.findByCertificateId(certificateId);

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

		Client client = clientRepository.findByCertificateId(clientName);

		// Assert

		assertNull(client);
	}


	@Test
	public void can_delete_client() throws Exception
	{
		// Arrange
		Client client = clientRepository.create("name", "certificateId");

		// Act
		clientRepository.destroy(client.getId());

		// Assert
		Client deletedClient = clientRepository.find(client.getId());
		assertNull(deletedClient);
	}


	@Test
	public void can_find_all_clients() throws Exception
	{
		// Arrange
		clientRepository.create("name1", "certificateId");
		clientRepository.create("name2", "certificateId");

		// Act
		List<Client> result = clientRepository.findAll();

		// Assert
		assertTrue(result.size() > 1);
	}
}
