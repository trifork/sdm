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

package com.trifork.stamdata.importer.parsers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.util.DateUtils;


public class ImportTimeManager
{
	private static Logger logger = LoggerFactory.getLogger(ImportTimeManager.class);

	public static Date getLastImportTime(String spoolername)
	{
		Connection con = null;
		Statement stmt = null;

		try
		{
			con = MySQLConnectionManager.getAutoCommitConnection();
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT max(importtime) from " + MySQLConnectionManager.getHousekeepingDBName() + ".Import where spoolername = '" + spoolername + "'");

			if (rs.next())
			{
				return rs.getTimestamp(1);
			}
			else
			{
				return null;
			}
		}
		catch (Exception e)
		{
			logger.error("getLastImportTime(" + spoolername + ")", e); // TODO:
																		// Throw
																		// don't
																		// log
			return null;
		}
		finally
		{
			MySQLConnectionManager.close(stmt, con);
		}
	}

	public static void setImportTime(String spoolerName, Date importTime)
	{
		Connection con = null;
		Statement stmt = null;

		try
		{
			con = MySQLConnectionManager.getAutoCommitConnection();
			stmt = con.createStatement();
			stmt.executeUpdate("insert into " + MySQLConnectionManager.getHousekeepingDBName() + ".Import values('" + DateUtils.toMySQLdate(importTime) + "', '" + spoolerName + "')");
		}
		catch (Exception e)
		{
			logger.error("getLastImportTime(" + spoolerName + ")", e); // TODO:
																		// Throw.
		}
		finally
		{
			MySQLConnectionManager.close(stmt, con);
		}
	}
}
