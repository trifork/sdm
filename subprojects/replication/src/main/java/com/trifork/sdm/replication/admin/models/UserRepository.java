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
import com.trifork.sdm.replication.db.properties.Transactional;

public class UserRepository implements IUserRepository
{
	@Inject
	@Transactional(ADMINISTRATION)
	private Provider<Connection> connectionProvider;


	/* (non-Javadoc)
	 * @see com.trifork.sdm.replication.admin.models.IUserRepository#find(java.lang.String)
	 */
	@Override
	@Transactional(ADMINISTRATION)
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


	/* (non-Javadoc)
	 * @see com.trifork.sdm.replication.admin.models.IUserRepository#create(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional(ADMINISTRATION)
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


	/* (non-Javadoc)
	 * @see com.trifork.sdm.replication.admin.models.IUserRepository#findAll()
	 */
	@Override
	@Transactional(ADMINISTRATION)
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


	/* (non-Javadoc)
	 * @see com.trifork.sdm.replication.admin.models.IUserRepository#destroy(java.lang.String)
	 */
	@Override
	@Transactional(ADMINISTRATION)
	public void destroy(String id) throws SQLException
	{
		PreparedStatement stm = connectionProvider.get().prepareStatement("DELETE FROM administrators WHERE (id = ?)");
		stm.setObject(1, id);
		stm.execute();
		stm.close();
	}


	/* (non-Javadoc)
	 * @see com.trifork.sdm.replication.admin.models.IUserRepository#isAdmin(java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional(ADMINISTRATION)
	public boolean isAdmin(String userCPR, String userCVR) throws SQLException
	{
		PreparedStatement stm = connectionProvider.get().prepareStatement("SELECT COUNT(*) FROM administrators WHERE (cpr = ?)");
		stm.setObject(1, userCPR);
		ResultSet row = stm.executeQuery();
		stm.close();

		boolean isAdmin = false;

		if (row.next())
		{
			isAdmin = row.getInt(1) > 0;
		}

		return isAdmin;
	}


	// Helper Methods

	protected User extract(ResultSet row) throws SQLException
	{
		String id = row.getString("id");
		String name = row.getString("name");
		String cpr = row.getString("cpr");
		String cvr = row.getString("cvr");

		User admin = new User(id, name, cpr, cvr);

		return admin;
	}
}
