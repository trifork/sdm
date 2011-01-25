package com.trifork.sdm.replication.admin.models;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.inject.Provider;
import com.trifork.sdm.replication.settings.AdminDB;


public class AdminRepository
{
	private final Provider<Connection> provider;


	@Inject
	AdminRepository(@AdminDB Provider<Connection> provider)
	{
		this.provider = provider;
	}


	public Admin find(String id)
	{

		Admin admin = null;

		try
		{
			PreparedStatement stm = provider.get().prepareStatement("SELECT * FROM administrators WHERE (id = ?)");
			stm.setObject(1, id);
			ResultSet result = stm.executeQuery();

			result.next();

			admin = extract(result);
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);

			// TODO: Log this.
		}

		return admin;
	}


	private Admin extract(ResultSet resultSet) throws SQLException
	{

		String id = resultSet.getString("id");
		String name = resultSet.getString("name");
		String cpr = resultSet.getString("cpr");
		String cvr = resultSet.getString("cvr");

		Admin admin = new Admin(id, name, cpr, cvr);

		return admin;
	}


	public Admin create(String name, String cpr, String cvr)
	{

		assert name != null && !name.isEmpty();
		assert cpr != null && !cpr.isEmpty();
		assert cvr != null && !cvr.isEmpty();

		Admin admin = null;

		try
		{
			PreparedStatement stm = provider.get().prepareStatement("INSERT INTO administrators SET name = ?, cpr = ?, cvr = ?", Statement.RETURN_GENERATED_KEYS);

			stm.setString(1, name);
			stm.setString(2, cpr);
			stm.setString(3, cvr);

			stm.executeUpdate();
			ResultSet resultSet = stm.getGeneratedKeys();

			// connection.commit();

			if (resultSet != null && resultSet.next())
			{

				String id = resultSet.getString(1);

				admin = new Admin(id, name, cpr, cvr);
			}
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);

			// TODO: Log this.
		}

		return admin;
	}


	public List<Admin> findAll()
	{

		List<Admin> admins = new ArrayList<Admin>();

		try
		{
			PreparedStatement stm = provider.get().prepareStatement("SELECT * FROM administrators ORDER BY name");

			ResultSet resultSet = stm.executeQuery();

			while (resultSet.next())
			{

				Admin client = extract(resultSet);

				admins.add(client);
			}
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);

			// TODO: Log this.
		}

		return admins;
	}


	public void destroy(String id)
	{

		try
		{
			PreparedStatement stm = provider.get().prepareStatement("DELETE FROM administrators WHERE (id = ?)");
			stm.setObject(1, id);
			stm.execute();
			stm.close();
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);

			// TODO: Log this.
		}
	}
}
