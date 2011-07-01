// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
//
// Contributor(s): Contributors are attributed in the source code
// where applicable.
//
// The Original Code is "Stamdata".
//
// The Initial Developer of the Original Code is Trifork Public A/S.
//
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
//
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.importer.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MySQLConnectionManager
{
	private static Logger logger = LoggerFactory.getLogger(MySQLConnectionManager.class);

	public static Connection getConnection()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection con = DriverManager.getConnection(Configuration.getString("db.url") + getDBName(), Configuration.getString("db.user"), Configuration.getString("db.pwd"));
			con.setAutoCommit(false);
			return con;
		}
		catch (Exception e)
		{
			logger.error("Error creating MySQL database connection", e);
		}
		return null;
	}

	public static Connection getAutoCommitConnection()
	{
		try
		{
			Connection con = getConnection();
			con.setAutoCommit(true);
			return con;
		}
		catch (Exception e)
		{
			logger.error("Error creating MySQL database connection", e);
		}
		return null;
	}

	public static String getDBName()
	{
		return Configuration.getString("db.database");
	}

	public static String getHousekeepingDBName()
	{

		String value = Configuration.getString("db.housekeepingdatabase");
		return value != null ? value : Configuration.getString("db.database");
	}

	public static void close(Connection connection)
	{
		try
		{
			if (connection != null)
			{
				connection.close();
			}
			else
				logger.warn("Cannot commit and close connection, because connection == null");
		}
		catch (Exception e)
		{
			logger.error("Could not close connection", e);
		}
	}

	public static void close(Statement stmt, Connection connection)
	{
		try
		{
			if (stmt != null)
			{
				stmt.close();
			}
			else
				logger.warn("Cannot close stmt, because stmt == null");
		}
		catch (Exception e)
		{
			logger.error("Could not close stmt", e);
		}
		finally
		{
			close(connection);
		}
	}

}
