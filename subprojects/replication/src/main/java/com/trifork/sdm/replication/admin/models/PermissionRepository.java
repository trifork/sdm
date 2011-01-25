package com.trifork.sdm.replication.admin.models;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.inject.Provider;
import com.trifork.sdm.replication.settings.AdminDB;


public class PermissionRepository
{

	private final Provider<Connection> provider;


	@Inject
	public PermissionRepository(@AdminDB Provider<Connection> provider)
	{
		this.provider = provider;
	}


	public List<String> findByClientId(String id)
	{
		List<String> permissions = new ArrayList<String>();

		try
		{
			PreparedStatement stm = provider.get().prepareStatement("SELECT resource_id FROM clients_permissions WHERE (client_id = ?)");
			stm.setObject(1, id);

			ResultSet resultSet = stm.executeQuery();

			while (resultSet.next())
			{

				String resourceId = resultSet.getString(1);

				permissions.add(resourceId);
			}
		}
		catch (SQLException e)
		{

			throw new RuntimeException(e);

			// TODO: log
		}

		return permissions;
	}


	public void setPermissions(String id, List<String> resources)
	{

		try
		{
			// Ugly but it works. Delete all existing permissions and set the new ones.

			PreparedStatement stm = provider.get().prepareStatement("DELETE FROM clients_permissions WHERE (client_id = ?)");
			stm.setObject(1, id);
			stm.execute();
			stm.close();

			for (String resource : resources)
			{
				stm = provider.get().prepareStatement("INSERT INTO clients_permissions SET client_id = ?, resource_id = ?");
				stm.setObject(1, id);
				stm.setString(2, resource);
				stm.execute();
				stm.close();
			}
		}
		catch (SQLException e)
		{

			// TODO: Log this error.
			throw new RuntimeException(e);
		}
	}
}
