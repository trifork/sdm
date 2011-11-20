/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */


package com.trifork.stamdata.importer.config;

import java.sql.*;

import com.trifork.stamdata.Nullable;
import com.trifork.stamdata.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Buchholdt <jbu@trifork.com>
 * @author Thomas Børlum <thb@trifork.com>
 */
public class ConnectionManager
{
	private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
	
	public Connection getConnection() throws SQLException
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		}
		catch (Exception e)
		{
			throw new RuntimeException("Could not load the database driver: com.mysql.jdbc.Driver", e);
		}
		
		Connection connection = DriverManager.getConnection(Configuration.getString("db.url") + getDBName(), Configuration.getString("db.user"), Configuration.getString("db.pwd"));
		connection.setAutoCommit(false);

		return connection;
	}

    public boolean isAvailable()
    {
        Connection connection = null;

        try
        {
            connection = getAutoCommitConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1");
            rs.next();
            return 1 == rs.getInt(1);
        }
        catch (Exception e)
        {
            return false;
        }
        finally
        {
            closeQuietly(connection);
        }
    }

	public Connection getAutoCommitConnection()
	{
		try
		{
			Connection connection = getConnection();
			connection.setAutoCommit(true);

			return connection;
		}
		catch (Exception e)
		{
			throw new RuntimeException("Could not create database connection.", e);
		}
	}

	public String getDBName()
	{
		return Configuration.getString("db.database");
	}

	public static void close(Connection connection)
	{
        Preconditions.checkNotNull(connection, "connection");

		try
		{
            connection.close();
		}
		catch (Exception e)
		{
			throw new RuntimeException("Could not close connection.", e);
		}
	}

	public static void closeQuietly(@Nullable Connection connection)
	{
		try
		{
            if (connection != null) close(connection);
		}
		catch (Exception e)
		{
            // Ignore
		}
	}

    public static void rollbackQuietly(@Nullable Connection connection)
    {
        try
        {
            if (connection != null) connection.rollback();
        }
        catch (SQLException e)
        {
            // Ignore
        }
    }
}
