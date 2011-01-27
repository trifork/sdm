package com.trifork.sdm.replication.admin.models;

import static com.trifork.sdm.replication.db.properties.Database.ADMINISTRATION;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.inject.Provider;
import com.trifork.sdm.replication.db.properties.Transaction;

public class UserRepository
{
	@Inject
	@Transaction(ADMINISTRATION)
	private Provider<Connection> connectionProvider;


	@Transaction(ADMINISTRATION)
	public User find(String id)
	{
		User admin = null;

		try
		{
			PreparedStatement statement = connectionProvider.get().prepareStatement("SELECT * FROM administrators WHERE (id = ?)");
			statement.setObject(1, id);
			ResultSet result = statement.executeQuery();

			result.next();

			admin = extract(result);

			statement.close();
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);

			// TODO: Log this.
		}

		return admin;
	}


	@Transaction(ADMINISTRATION)
	public User create(String name, String cpr, String cvr)
	{
		User admin = null;

		try
		{
			PreparedStatement stm = connectionProvider.get().prepareStatement("INSERT INTO administrators SET name = ?, cpr = ?, cvr = ?");

			stm.setString(1, name);
			stm.setString(2, cpr);
			stm.setString(3, cvr);

			stm.executeUpdate();
			ResultSet resultSet = stm.getGeneratedKeys();

			if (resultSet != null && resultSet.next())
			{
				String id = resultSet.getString(1);
				admin = new User(id, name, cpr, cvr);
			}
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);

			// TODO: Log this.
		}

		return admin;
	}


	@Transaction(ADMINISTRATION)
	public List<User> findAll()
	{
		List<User> admins = new ArrayList<User>();

		try
		{
			PreparedStatement stm = connectionProvider.get().prepareStatement("SELECT * FROM administrators ORDER BY name");

			ResultSet resultSet = stm.executeQuery();

			while (resultSet.next())
			{
				User client = extract(resultSet);
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


	@Transaction(ADMINISTRATION)
	public void destroy(String id)
	{
		try
		{
			PreparedStatement stm = connectionProvider.get().prepareStatement("DELETE FROM administrators WHERE (id = ?)");
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


	protected User extract(ResultSet resultSet) throws SQLException
	{
		String id = resultSet.getString("id");
		String name = resultSet.getString("name");
		String cpr = resultSet.getString("cpr");
		String cvr = resultSet.getString("cvr");

		User admin = new User(id, name, cpr, cvr);

		return admin;
	}
}
