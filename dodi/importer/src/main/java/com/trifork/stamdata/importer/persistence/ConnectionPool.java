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

package com.trifork.stamdata.importer.persistence;

import java.sql.*;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.trifork.stamdata.Nullable;


/**
 * @author Jan Buchholdt <jbu@trifork.com>
 */
public class ConnectionPool
{
	private final String jdbcURL;
	private final String username;
	private final String password;

	@Inject
	ConnectionPool(@Named("db.connection.jdbcUrl") String jdbcURL, @Named("db.connection.username") String username, @Nullable @Named("db.connection.password") String password)
	{
		this.jdbcURL = jdbcURL;
		this.username = username;
		this.password = password;
	}

	public Connection getConnection()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			Connection connection = DriverManager.getConnection(jdbcURL, username, password);
			connection.setAutoCommit(false);
			
			return connection;
		}
		catch (Exception e)
		{
			throw new RuntimeException("Could not load the database driver. class_name=com.mysql.jdbc.Driver", e);
		}
	}
}
