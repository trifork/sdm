package com.trifork.sdm.replication.admin;


import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.*;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.trifork.sdm.replication.ProductionModule;
import com.trifork.sdm.replication.admin.models.Client;
import com.trifork.sdm.replication.admin.models.ClientRepository;


public class ClientRepositoryTest
{
	private static final String TEST_CLIENT = "TestClient";
	private static Injector injector;
	private ClientRepository clientRepository;


	@BeforeClass
	public static void init()
	{
		injector = Guice.createInjector(new ProductionModule());
	}


	@Before
	public void setUp()
	{
		clientRepository = injector.getInstance(ClientRepository.class);
	}

	
	@Test
	public void can_find_client_by_certificate_id() throws Exception
	{
		// Arrange

		String certificateId = "" + System.currentTimeMillis();
		clientRepository.create(TEST_CLIENT, certificateId);

		// Act

		Client client = clientRepository.findByCertificateId(certificateId);

		// Assert

		assertThat(client.getName(), is(equalTo(TEST_CLIENT)));
		assertThat(client.getCertificateId(), is(certificateId));
	}


	@Test
	public void null_result_when_searching_by_certificate_id_which_does_not_exists() throws Exception
	{
		// Act

		Client client = clientRepository.findByCertificateId("unknown");

		// Assert

		assertNull(client);
	}
}
