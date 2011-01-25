package com.trifork.sdm.replication.admin;


import org.junit.Test;


public class PermissionRepositoryTest
{
	@Test
	public void can_find_permissions_by_client_id() throws Exception
	{
		/*
		// Arrange

		Injector injector = Guice.createInjector(new DatabaseModule());
		PermissionRepository permissionRepository = injector.getInstance(PermissionRepository.class);
		ClientRepository clientRepository = injector.getInstance(ClientRepository.class);

		Client client1 = clientRepository.create("TestClient1", "certificateId");
		Client client2 = clientRepository.create("TestClient2", "certificateId");

		List<String> client1Permissions = new ArrayList<String>();
		client1Permissions.add("Apotek");
		client1Permissions.add("CPR");

		List<String> client2Permissions = new ArrayList<String>();
		client2Permissions.add("Apotek");

		permissionRepository.setPermissions(client1.getId(), client1Permissions);
		permissionRepository.setPermissions(client2.getId(), client2Permissions);

		// Act

		List<String> permissions = permissionRepository.findByClientId(client1.getId());

		// Assert

		assertNotNull(permissions);
		assertThat(permissions.size(), is(2));
		assertThat(permissions, contains("Apotek", "CPR"));
		
		*/
	}
}
