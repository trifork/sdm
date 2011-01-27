package com.trifork.sdm.replication.admin;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.*;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.trifork.sdm.replication.ProductionModule;
import com.trifork.sdm.replication.admin.models.Client;
import com.trifork.sdm.replication.admin.models.ClientRepository;

public class ClientRepositoryTest
{
	private static Injector injector;
	private ClientRepository clientRepository;

	// Test data.

	private String clientName;


	@BeforeClass
	public static void init()
	{
		injector = Guice.createInjector(new ProductionModule());
	}


	@Before
	public void setUp()
	{
		clientRepository = injector.getInstance(ClientRepository.class);

		// Generate some test random data.

		clientName = RandomStringUtils.random(50);
	}


	@Test
	public void can_find_client_by_certificate_id() throws Exception
	{
		// Arrange

		String certificateId = "" + System.currentTimeMillis();
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
}
