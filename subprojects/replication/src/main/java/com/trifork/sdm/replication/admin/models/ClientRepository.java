package com.trifork.sdm.replication.admin.models;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.inject.Provider;


public class ClientRepository
{

	private final Provider<Connection> provider;


	@Inject
	public ClientRepository(Provider<Connection> provider)
	{
		this.provider = provider;
	}


	public Client find(String id)
	{

		Client client = null;

		try
		{
			PreparedStatement stm = provider.get().prepareStatement("SELECT * FROM clients WHERE (id = ?)");
			stm.setObject(1, id);
			ResultSet result = stm.executeQuery();

			result.next();

			client = extractClient(result);
		}
		catch (SQLException e)
		{
			new RuntimeException(e);

			// TODO: Log this error.
		}

		return client;
	}


	public Client findByCertificateId(String certificateId)
	{
		Client client = null;

		try
		{
			PreparedStatement stm = provider.get().prepareStatement("SELECT * FROM clients WHERE (certificate_id = ?)");
			stm.setObject(1, certificateId);
			ResultSet result = stm.executeQuery();

			result.next();

			client = extractClient(result);
		}
		catch (SQLException e)
		{
			new RuntimeException(e);

			// TODO: Log this error.
		}

		return client;
	}


	public void destroy(String id)
	{

		try
		{
			PreparedStatement stm = provider.get().prepareStatement("DELETE FROM clients WHERE (id = ?)");
			stm.setObject(1, id);
			stm.execute();
			stm.close();
		}
		catch (SQLException e)
		{
			new RuntimeException(e);

			// TODO: Log this error.
		}
	}


	private Client extractClient(ResultSet resultSet) throws SQLException
	{

		String id = resultSet.getString("id");
		String name = resultSet.getString("name");
		String certificateId = resultSet.getString("certificate_id");

		Client client = new Client(id, name, certificateId);

		return client;
	}


	public Client create(String name, String certificateId)
	{

		assert name != null && !name.isEmpty();
		assert certificateId != null && !certificateId.isEmpty();

		Client client = null;

		try
		{
			PreparedStatement stm = provider.get().prepareStatement("INSERT INTO clients SET name = ?, certificate_id = ?", Statement.RETURN_GENERATED_KEYS);

			stm.setString(1, name);
			stm.setString(2, certificateId);

			stm.executeUpdate();
			ResultSet resultSet = stm.getGeneratedKeys();

			// connection.commit();

			if (resultSet != null && resultSet.next())
			{

				String id = resultSet.getString(1);

				client = new Client(id, name, certificateId);
			}
		}
		catch (SQLException e)
		{
			new RuntimeException(e);

			// TODO: Log this error.
		}

		return client;
	}


	public List<Client> findAll()
	{

		List<Client> clients = new ArrayList<Client>();

		try
		{
			PreparedStatement stm = provider.get().prepareStatement("SELECT * FROM clients ORDER BY name");

			ResultSet resultSet = stm.executeQuery();

			while (resultSet.next())
			{

				Client client = extractClient(resultSet);

				clients.add(client);
			}
		}
		catch (SQLException e)
		{
			new RuntimeException(e);

			// TODO: Log this error.
		}

		return clients;
	}
}
