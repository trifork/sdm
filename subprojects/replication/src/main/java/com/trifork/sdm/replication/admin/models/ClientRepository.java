package com.trifork.sdm.replication.admin.models;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.inject.Provider;
import com.trifork.sdm.replication.db.TransactionManager.OutOfTransactionException;
import com.trifork.sdm.replication.db.properties.AdminTransaction;


public class ClientRepository
{
	private final Provider<Connection> connectionProvider;


	@Inject
	public ClientRepository(@AdminTransaction Provider<Connection> transactionManager)
	{
		this.connectionProvider = transactionManager;
	}


	@AdminTransaction
	public Client find(String id) throws OutOfTransactionException, SQLException
	{
		Client client = null;

		PreparedStatement stm = connectionProvider.get().prepareStatement("SELECT * FROM clients WHERE (id = ?)");
		stm.setObject(1, id);
		ResultSet result = stm.executeQuery();

		result.next();

		client = extractClient(result);

		return client;
	}


	@AdminTransaction
	public Client findByCertificateId(String certificateId) throws OutOfTransactionException, SQLException
	{
		Client client = null;

		PreparedStatement stm = connectionProvider.get().prepareStatement("SELECT * FROM clients WHERE (certificate_id = ?)");
		stm.setObject(1, certificateId);
		ResultSet result = stm.executeQuery();

		result.next();

		client = extractClient(result);

		return client;
	}


	@AdminTransaction
	public void destroy(String id) throws OutOfTransactionException, SQLException
	{
		PreparedStatement stm = connectionProvider.get().prepareStatement("DELETE FROM clients WHERE (id = ?)");
		stm.setObject(1, id);
		stm.execute();
		stm.close();
	}


	private Client extractClient(ResultSet resultSet) throws SQLException
	{
		String id = resultSet.getString("id");
		String name = resultSet.getString("name");
		String certificateId = resultSet.getString("certificate_id");

		Client client = new Client(id, name, certificateId);

		return client;
	}


	@AdminTransaction
	public Client create(String name, String certificateId) throws OutOfTransactionException, SQLException
	{
		assert name != null && !name.isEmpty();
		assert certificateId != null && !certificateId.isEmpty();

		Client client = null;

		PreparedStatement stm = connectionProvider.get().prepareStatement("INSERT INTO clients SET name = ?, certificate_id = ?", Statement.RETURN_GENERATED_KEYS);

		stm.setString(1, name);
		stm.setString(2, certificateId);

		stm.executeUpdate();
		ResultSet resultSet = stm.getGeneratedKeys();

		if (resultSet != null && resultSet.next())
		{
			String id = resultSet.getString(1);

			client = new Client(id, name, certificateId);
		}

		return client;
	}


	@AdminTransaction
	public List<Client> findAll() throws OutOfTransactionException, SQLException
	{
		List<Client> clients = new ArrayList<Client>();

		PreparedStatement stm = connectionProvider.get().prepareStatement("SELECT * FROM clients ORDER BY name");

		ResultSet resultSet = stm.executeQuery();

		while (resultSet.next())
		{

			Client client = extractClient(resultSet);

			clients.add(client);
		}

		return clients;
	}
}
