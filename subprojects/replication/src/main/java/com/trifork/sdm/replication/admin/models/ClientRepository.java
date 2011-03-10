package com.trifork.sdm.replication.admin.models;


import static com.trifork.sdm.replication.db.properties.Database.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.trifork.sdm.replication.db.properties.Transactional;


public class ClientRepository
{
	@Inject
	@Transactional(ADMINISTRATION)
	private Provider<Connection> connectionProvider;


	@Transactional(ADMINISTRATION)
	public Client find(String id) throws SQLException
	{
		PreparedStatement statement = null;

		try
		{
			Client client = null;

			statement = connectionProvider.get().prepareStatement("SELECT * FROM clients WHERE (id = ?)");
			statement.setObject(1, id);
			ResultSet result = statement.executeQuery();

			if (result.next())
			{
				client = serialize(result);
			}

			return client;
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
	public Client findByCertificateId(String certificateId) throws SQLException
	{
		PreparedStatement statement = null;

		try
		{
			Client client = null;

			statement = connectionProvider.get().prepareStatement("SELECT * FROM clients WHERE (certificate_id = ?)");
			statement.setObject(1, certificateId);
			ResultSet result = statement.executeQuery();

			if (result.next())
			{
				client = serialize(result);
			}

			return client;
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
	public void destroy(String id) throws SQLException
	{
		PreparedStatement statement = null;

		try
		{
			statement = connectionProvider.get().prepareStatement("DELETE FROM clients WHERE (id = ?)");
			statement.setObject(1, id);
			statement.execute();
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
	public Client create(String name, String certificateId) throws SQLException
	{
		assert name != null && !name.isEmpty();
		assert certificateId != null && !certificateId.isEmpty();

		PreparedStatement statement = null;

		try
		{
			Client client = null;

			statement = connectionProvider.get().prepareStatement("INSERT INTO clients SET name = ?, certificate_id = ?", Statement.RETURN_GENERATED_KEYS);

			statement.setString(1, name);
			statement.setString(2, certificateId);

			if (statement.executeUpdate() != 0)
			{
				ResultSet resultSet = statement.getGeneratedKeys();

				if (resultSet != null && resultSet.next())
				{
					String id = resultSet.getString(1);

					client = new Client(id, name, certificateId);
				}
			}

			return client;
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
	public List<Client> findAll() throws SQLException
	{
		PreparedStatement statement = null;

		try
		{
			List<Client> clients = new ArrayList<Client>();

			statement = connectionProvider.get().prepareStatement("SELECT * FROM clients ORDER BY name");

			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next())
			{
				Client client = serialize(resultSet);

				clients.add(client);
			}

			return clients;
		}
		finally
		{
			if (statement != null && !statement.isClosed())
			{
				statement.close();
			}
		}
	}


	protected Client serialize(ResultSet resultSet) throws SQLException
	{
		String id = resultSet.getString("id");
		String name = resultSet.getString("name");
		String certificateId = resultSet.getString("certificate_id");

		Client client = new Client(id, name, certificateId);

		return client;
	}
}
