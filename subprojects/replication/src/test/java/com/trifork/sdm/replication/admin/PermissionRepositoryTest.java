package com.trifork.sdm.replication.admin;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.trifork.sdm.replication.ConfigurationModule;
import com.trifork.sdm.replication.admin.models.Client;
import com.trifork.sdm.replication.admin.models.ClientRepository;
import com.trifork.sdm.replication.admin.models.PermissionRepository;
import com.trifork.sdm.replication.db.DatabaseModule;

public class PermissionRepositoryTest {
	private PermissionRepository permissionRepository;
	private ClientRepository clientRepository;
	private static Injector injector;
	
	@BeforeClass
	public static void init()
	{
		injector = Guice.createInjector(new ConfigurationModule(), new DatabaseModule());
	}

	@Before
	public void setUp()
	{
		permissionRepository = injector.getInstance(PermissionRepository.class);
		clientRepository = injector.getInstance(ClientRepository.class);
	}

	@Test
	public void can_find_permissions_by_client_id() throws Exception {

		// Arrange
		
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
	}
	
	@Test
	public void can_set_permissions() throws Exception {
		// Arrange
		Client client = clientRepository.create("TestClient", "certificateId");
		ArrayList<String> permissions = new ArrayList<String>();
		permissions.add("Test");
		permissionRepository.setPermissions(client.getId(), permissions);

		// Act
		permissions.clear();
		permissions.add("Test2");
		permissions.add("Test3");
		permissionRepository.setPermissions(client.getId(), permissions);
		
		// Assert
		List<String> updatedPermissions = permissionRepository.findByClientId(client.getId());
		assertThat(updatedPermissions.size(), is(2));
		assertThat(updatedPermissions, contains("Test2", "Test3"));
	}
}
