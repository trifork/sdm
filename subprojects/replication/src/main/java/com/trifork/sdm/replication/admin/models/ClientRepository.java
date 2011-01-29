package com.trifork.sdm.replication.admin.models;

import static com.trifork.sdm.replication.db.properties.Database.*;

import java.sql.*;
import java.util.*;

import com.google.inject.*;
import com.trifork.sdm.replication.db.TransactionManager.OutOfTransactionException;
import com.trifork.sdm.replication.db.properties.Transactional;


public class ClientRepository
{
	@Inject
	@Transactional(ADMINISTRATION)
	private Provider<Connection> connectionProvider;


	@Transactional(ADMINISTRATION)
	public Client find(String id) throws OutOfTransactionException, SQLException
	{
		Client client = null;

		PreparedStatement stm = connectionProvider.get().prepareStatement("SELECT * FROM clients WHERE (id = ?)");
		stm.setObject(1, id);
		ResultSet result = stm.executeQuery();

		if (result.next())
		{
			client = serialize(result);
		}

		stm.close();

		return client;
	}


	@Transactional(ADMINISTRATION)
	public Client findByCertificateId(String certificateId) throws OutOfTransactionException, SQLException
	{
		Client client = null;

		PreparedStatement stm = connectionProvider.get().prepareStatement("SELECT * FROM clients WHERE (certificate_id = ?)");
		stm.setObject(1, certificateId);
		ResultSet result = stm.executeQuery();

		if (result.next())
		{
			client = serialize(result);
		}

		return client;
	}


	@Transactional(ADMINISTRATION)
	public void destroy(String id) throws OutOfTransactionException, SQLException
	{
		PreparedStatement stm = connectionProvider.get().prepareStatement("DELETE FROM clients WHERE (id = ?)");
		stm.setObject(1, id);
		stm.execute();
		stm.close();
	}


	@Transactional(ADMINISTRATION)
	public Client create(String name, String certificateId) throws OutOfTransactionException, SQLException
	{
		assert name != null && !name.isEmpty();
		assert certificateId != null && !certificateId.isEmpty();

		Client client = null;

		PreparedStatement stm = connectionProvider.get().prepareStatement("INSERT INTO clients SET name = ?, certificate_id = ?", Statement.RETURN_GENERATED_KEYS);

		stm.setString(1, name);
		stm.setString(2, certificateId);

		if (stm.executeUpdate() != 0)
		{
			ResultSet resultSet = stm.getGeneratedKeys();

			if (resultSet != null && resultSet.next())
			{
				String id = resultSet.getString(1);

				client = new Client(id, name, certificateId);
			}
		}

		return client;
	}


	@Transactional(ADMINISTRATION)
	public List<Client> findAll() throws OutOfTransactionException, SQLException
	{
		List<Client> clients = new ArrayList<Client>();

		PreparedStatement stm = connectionProvider.get().prepareStatement("SELECT * FROM clients ORDER BY name");

		ResultSet resultSet = stm.executeQuery();

		while (resultSet.next())
		{
			Client client = serialize(resultSet);

			clients.add(client);
		}

		return clients;
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
