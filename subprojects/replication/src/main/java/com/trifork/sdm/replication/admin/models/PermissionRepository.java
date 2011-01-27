package com.trifork.sdm.replication.admin.models;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.trifork.sdm.replication.db.TransactionManager;
import com.trifork.sdm.replication.db.TransactionManager.OutOfTransactionException;
import com.trifork.sdm.replication.db.TransactionManager.Transactional;


public class PermissionRepository
{
	private final TransactionManager connectionProvider;


	@Inject
	public PermissionRepository(TransactionManager connectionProvider)
	{
		this.connectionProvider = connectionProvider;
	}

	@Transactional
	public List<String> findByClientId(String id) throws OutOfTransactionException, SQLException
	{
		List<String> permissions = new ArrayList<String>();

		PreparedStatement stm = connectionProvider.get().prepareStatement("SELECT resource_id FROM clients_permissions WHERE (client_id = ?)");
		stm.setObject(1, id);

		ResultSet resultSet = stm.executeQuery();

		while (resultSet.next())
		{
			String entityId = resultSet.getString(1);

			permissions.add(entityId);
		}

		return permissions;
	}


	@Transactional
	public void setPermissions(String id, List<String> entities) throws SQLException
	{
		Connection connection = connectionProvider.get();
		
		PreparedStatement statement = connection.prepareStatement("DELETE FROM clients_permissions WHERE (client_id = ?)");
		statement.setObject(1, id);
		statement.execute();
		statement.close();

		for (String entity : entities)
		{
			statement = connection.prepareStatement("INSERT INTO clients_permissions SET client_id = ?, resource_id = ?");
			statement.setObject(1, id);
			statement.setString(2, entity);
			statement.execute();
			statement.close();
		}
	}


	@Transactional
	public boolean canAccessEntity(String certificateID, String entityID) throws SQLException
	{
		String SQL = "SELECT COUNT(*) FROM clients_permissions JOIN ON (client_id) WHERE (resource_id = ? AND certificate_id = ?)";
		
		Connection connection = connectionProvider.get();
		
		PreparedStatement statement = connection.prepareStatement(SQL);
		statement.setString(1, entityID);
		statement.setString(2, certificateID);
		statement.execute();
		statement.close();
		
		return false;
	}
}
