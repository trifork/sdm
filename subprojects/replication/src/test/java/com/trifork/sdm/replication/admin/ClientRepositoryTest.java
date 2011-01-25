package com.trifork.sdm.replication.admin;


import org.junit.Test;


public class ClientRepositoryTest
{
	@Test
	public void can_find_client_by_certificate_id() throws Exception
	{
		/*
		// Arrange
		Injector injector = Guice.createInjector(new DatabaseModule());
		ClientRepository clientRepository = injector.getInstance(ClientRepository.class);

		String certificateId = "" + System.currentTimeMillis();
		clientRepository.create("TestClient", certificateId);

		// Act
		Client client = clientRepository.findByCertificateId(certificateId);

		// Assert
		assertNotNull(client);
		assertThat(client.getName(), is(equalTo("TestClient")));
		assertThat(client.getCertificateId(), is(equalTo(certificateId)));
		*/
	}


	@Test
	public void null_result_when_searching_by_certificate_id_which_does_not_exists() throws Exception
	{
		/*
		// Arrange
		Injector injector = Guice.createInjector(new DatabaseModule());
		ClientRepository clientRepository = injector.getInstance(ClientRepository.class);

		// Act
		Client client = clientRepository.findByCertificateId("unknown");

		// Assert
		assertNull(client);
		*/
	}
}
