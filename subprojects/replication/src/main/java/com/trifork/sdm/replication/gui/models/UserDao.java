package com.trifork.sdm.replication.gui.models;


import static com.trifork.sdm.replication.db.properties.Database.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.trifork.sdm.replication.db.properties.Transactional;


public class UserDao {

	private final Provider<Connection> connectionProvider;


	@Inject
	public UserDao(@Transactional(ADMINISTRATION) Provider<Connection> connectionProvider) {

		this.connectionProvider = connectionProvider;
	}


	@Transactional(ADMINISTRATION)
	public User find(String id) throws SQLException {
		PreparedStatement statement = null;

		try {
			User admin = null;

			statement = connectionProvider.get().prepareStatement("SELECT * FROM administrators WHERE (id = ?)");
			statement.setObject(1, id);
			ResultSet result = statement.executeQuery();

			if (result.next()) {
				admin = extract(result);
			}

			return admin;
		}
		finally {
			if (statement != null && !statement.isClosed()) {
				statement.close();
			}
		}
	}


	@Transactional(ADMINISTRATION)
	public User find(String cvr, String cpr) throws SQLException {
		PreparedStatement statement = null;

		try {
			User admin = null;

			statement = connectionProvider.get().prepareStatement("SELECT * FROM administrators WHERE (cpr = ? AND cvr = ?)");
			statement.setObject(1, cpr);
			statement.setObject(2, cvr);
			ResultSet result = statement.executeQuery();

			if (result.next()) {
				admin = extract(result);
			}

			return admin;
		}
		finally {
			if (statement != null && !statement.isClosed()) {
				statement.close();
			}
		}
	}


	@Transactional(ADMINISTRATION)
	public User create(String name, String cpr, String cvr) throws SQLException {
		PreparedStatement statement = null;

		try {
			User admin = null;

			Connection conn = connectionProvider.get();
			String sql = "INSERT INTO administrators SET name = ?, cpr = ?, cvr = ?";
			statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			statement.setString(1, name);
			statement.setString(2, cpr);
			statement.setString(3, cvr);

			if (statement.executeUpdate() != 0) {
				ResultSet resultSet = statement.getGeneratedKeys();

				if (resultSet != null && resultSet.next()) {

					String id = resultSet.getString(1);

					admin = new User(id, name, cpr, cvr);
				}
			}

			return admin;
		}
		finally {
			if (statement != null && !statement.isClosed()) {
				statement.close();
			}
		}
	}


	@Transactional(ADMINISTRATION)
	public List<User> findAll() throws SQLException {
		PreparedStatement statement = null;

		try {
			List<User> admins = new ArrayList<User>();

			statement = connectionProvider.get().prepareStatement("SELECT * FROM administrators ORDER BY name");

			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				User client = extract(resultSet);
				admins.add(client);
			}

			return admins;
		}
		finally {
			if (statement != null && !statement.isClosed()) {
				statement.close();
			}
		}
	}


	@Transactional(ADMINISTRATION)
	public void destroy(String id) throws SQLException {
		PreparedStatement statement = null;

		try {
			statement = connectionProvider.get().prepareStatement("DELETE FROM administrators WHERE (id = ?)");
			statement.setObject(1, id);
			statement.execute();
		}
		finally {
			if (statement != null && !statement.isClosed()) {
				statement.close();
			}
		}
	}


	@Transactional(ADMINISTRATION)
	public boolean isAdmin(String userCPR, String userCVR) throws SQLException {
		PreparedStatement statement = null;

		try {
			statement = connectionProvider.get().prepareStatement("SELECT COUNT(*) FROM administrators WHERE (cpr = ?)");
			statement.setObject(1, userCPR);
			ResultSet row = statement.executeQuery();

			boolean isAdmin = false;

			if (row.next()) {
				isAdmin = row.getInt(1) > 0;
			}

			return isAdmin;
		}
		finally {
			if (statement != null && !statement.isClosed()) {
				statement.close();
			}
		}
	}


	// Helper Methods

	protected User extract(ResultSet row) throws SQLException {
		String id = row.getString("id");
		String name = row.getString("name");
		String cpr = row.getString("cpr");
		String cvr = row.getString("cvr");

		User admin = new User(id, name, cpr, cvr);

		return admin;
	}
}
