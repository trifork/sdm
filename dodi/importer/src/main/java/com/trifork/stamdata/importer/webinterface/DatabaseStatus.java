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

package com.trifork.stamdata.importer.webinterface;

import java.sql.*;

import org.slf4j.*;

import com.google.inject.Inject;
import com.trifork.stamdata.importer.persistence.ConnectionPool;


/**
 * @author Jan Buchholdt <jbu@trifork.com>
 */
public class DatabaseStatus
{
	private final Logger logger = LoggerFactory.getLogger(DatabaseStatus.class);

	private final ConnectionPool connectionPool;

	@Inject
	DatabaseStatus(ConnectionPool connectionPool)
	{
		this.connectionPool = connectionPool;
	}

	public boolean isAlive()
	{
		boolean isUp = false;
		Connection connection = null;
		Statement simpleStatement = null;

		try
		{
			connection = connectionPool.getConnection();
			simpleStatement = connection.createStatement();

			ResultSet rs = simpleStatement.executeQuery("SELECT 1");

			rs.next();

			isUp = (1 == rs.getInt(1));

			rs.close();
		}
		catch (Exception e)
		{
			logger.error("The database connection is down.", e);
		}
		finally
		{
			try
			{
				if (simpleStatement != null) simpleStatement.close();
				if (connection != null) connection.close();
			}
			catch (Exception e)
			{
			}
		}

		return isUp;
	}
}
