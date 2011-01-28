package com.trifork.sdm.replication.admin;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.trifork.sdm.replication.admin.models.Client;

public class PermissionRepositoryTest extends RepositoryTest {
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
	
	@Test
	public void can_access_entity_when_permissions_are_correctly_setup() throws Exception {
		// Arrange
		Client client = clientRepository.create("name", "certIdToSearchFor");
		ArrayList<String> permissions = new ArrayList<String>();
		permissions.add("Apotek");
		permissionRepository.setPermissions(client.getId(), permissions);
		
		// Act
		boolean canAccessEntity = permissionRepository.canAccessEntity("certIdToSearchFor", "Apotek");
		
		// Assert
		assertTrue(canAccessEntity);
	}
	
	@Test
	public void cannot_access_entity_when_permissions_are_not_correctly_setup() throws Exception {
		// Arrange
		
		// Act
		boolean canAccessEntity = permissionRepository.canAccessEntity("certificateID", "Unkown");
		
		// Assert
		assertFalse(canAccessEntity);
	}
}
