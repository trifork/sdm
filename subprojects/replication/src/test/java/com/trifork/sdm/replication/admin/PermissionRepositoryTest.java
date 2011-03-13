package com.trifork.sdm.replication.admin;


import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import com.trifork.sdm.replication.GuiceTest;
import com.trifork.sdm.replication.admin.models.*;


public class PermissionRepositoryTest extends GuiceTest
{
	// TODO: The client repository should not be used for this test at all.
	// This test should only be dependent on the unit under test.
	// This could for instance be fixed by having test data loaded into the
	// db before the tests are run.
	// For now the two repository classes should be merged I believe, and
	// permissions be accessed directly on the user model.
	private ClientRepository clientRepository;

	private PermissionDao permissionRepository;


	@Before
	public void setUp()
	{
		clientRepository = getInjector().getInstance(ClientRepository.class);
		permissionRepository = getInjector().getInstance(PermissionDao.class);
	}


	@Test
	public void can_find_permissions_by_client_id() throws Exception
	{
		// Arrange

		Client client1 = clientRepository.create("TestClient1", "certificateId");
		Client client2 = clientRepository.create("TestClient2", "certificateId");

		List<String> client1Permissions = new ArrayList<String>();
		client1Permissions.add("Apotek");
		client1Permissions.add("CPR");

		List<String> client2Permissions = new ArrayList<String>();
		client2Permissions.add("Apotek");

		permissionRepository.update(client1.getId(), client1Permissions);
		permissionRepository.update(client2.getId(), client2Permissions);

		// Act

		List<String> permissions = permissionRepository.findByClientId(client1.getId());

		// Assert

		assertNotNull(permissions);
		assertThat(permissions.size(), is(2));
		assertThat(permissions, contains("Apotek", "CPR"));
	}


	@Test
	public void can_set_permissions() throws Exception
	{
		// Arrange
		Client client = clientRepository.create("TestClient", "certificateId");
		ArrayList<String> permissions = new ArrayList<String>();
		permissions.add("Test");
		permissionRepository.update(client.getId(), permissions);

		// Act
		permissions.clear();
		permissions.add("Test2");
		permissions.add("Test3");
		permissionRepository.update(client.getId(), permissions);

		// Assert
		List<String> updatedPermissions = permissionRepository.findByClientId(client.getId());
		assertThat(updatedPermissions.size(), is(2));
		assertThat(updatedPermissions, contains("Test2", "Test3"));
	}


	@Test
	public void can_access_entity_when_permissions_are_correctly_setup() throws Exception
	{
		// Arrange
		Client client = clientRepository.create("name", "certIdToSearchFor");
		ArrayList<String> permissions = new ArrayList<String>();
		permissions.add("Apotek");
		permissionRepository.update(client.getId(), permissions);

		// Act
		boolean canAccessEntity = permissionRepository.canAccessEntity("certIdToSearchFor", "Apotek");

		// Assert
		assertTrue(canAccessEntity);
	}


	@Test
	public void cannot_access_entity_when_permissions_are_not_correctly_setup() throws Exception
	{
		// Arrange

		// Act
		boolean canAccessEntity = permissionRepository.canAccessEntity("certificateID", "Unkown");

		// Assert
		assertFalse(canAccessEntity);
	}
}
