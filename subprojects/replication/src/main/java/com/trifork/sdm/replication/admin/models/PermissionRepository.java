package com.trifork.sdm.replication.admin.models;


import static com.trifork.sdm.replication.db.properties.Database.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.trifork.sdm.replication.db.properties.Transactional;


public class PermissionRepository
{
	@Inject
	@Transactional(ADMINISTRATION)
	private Provider<Connection> connectionProvider;


	@Transactional(ADMINISTRATION)
	public List<String> findByClientId(String id) throws SQLException
	{
		PreparedStatement statement = null;

		try
		{
			List<String> permissions = new ArrayList<String>();

			statement = connectionProvider.get().prepareStatement("SELECT resource_id FROM clients_permissions WHERE (client_id = ?)");
			statement.setObject(1, id);

			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next())
			{
				String entityId = resultSet.getString(1);

				permissions.add(entityId);
			}

			return permissions;
		}
		finally
		{
			if (statement != null && !statement.isClosed())
			{
				statement.close();
			}
		}
	}


	@Transactional(ADMINISTRATION)
	public void update(String id, List<String> entities) throws SQLException
	{
		PreparedStatement statement = null;

		try
		{
			Connection connection = connectionProvider.get();

			statement = connection.prepareStatement("DELETE FROM clients_permissions WHERE (client_id = ?)");
			statement.setObject(1, id);
			statement.execute();

			for (String entity : entities)
			{
				statement = connection.prepareStatement("INSERT INTO clients_permissions SET client_id = ?, resource_id = ?");
				statement.setObject(1, id);
				statement.setString(2, entity);
				statement.execute();
			}
		}
		finally
		{
			if (statement != null && !statement.isClosed())
			{
				statement.close();
			}
		}
	}


	@Transactional(ADMINISTRATION)
	public boolean canAccessEntity(String certificateID, String entityID) throws SQLException
	{
		PreparedStatement statement = null;

		try
		{
			String SQL = "SELECT COUNT(*) FROM clients_permissions JOIN clients ON clients_permissions.client_id = clients.id WHERE (resource_id = ? AND certificate_id = ?)";

			Connection connection = connectionProvider.get();

			statement = connection.prepareStatement(SQL);
			statement.setString(1, entityID);
			statement.setString(2, certificateID);
			ResultSet resultSet = statement.executeQuery();

			boolean hasAccess = false;
			if (resultSet.next())
			{
				hasAccess = resultSet.getInt(1) > 0;
			}

			return hasAccess;
		}
		finally
		{
			if (statement != null && !statement.isClosed())
			{
				statement.close();
			}
		}
	}
}
