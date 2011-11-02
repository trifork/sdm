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


package com.trifork.stamdata.importer.jobs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.util.Dates;

// FIXME (thb): This class serves no purpose and can be refactored into the FileParserJob class.
public class ImportTimeManager
{
	private static Logger logger = LoggerFactory.getLogger(ImportTimeManager.class);

	public static DateTime getLastImportTime(String spoolername)
	{
		// TODO (thb): This should NOT create its own connection but use the file parser job's.
		
		Connection connection = null;
		Statement stmt = null;
		DateTime result = null;

		try
		{
			connection = MySQLConnectionManager.getAutoCommitConnection();
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT MAX(importtime) FROM Import WHERE spoolername = '" + spoolername + "'");

			if (rs.next())
			{
				// We cannot dump the timestamp directly into the
				// DateTime or we will get the current date.
				// We have to check for null first.
				
				Timestamp timestamp = rs.getTimestamp(1);
				
				if (timestamp != null)
				{
					result = new DateTime(timestamp);
				}
			}
			
			rs.close();
		}
		catch (Exception e)
		{
			// TODO (thb): Throw, don't log.
			
			logger.error("getLastImportTime(" + spoolername + ")", e);
		}
		finally
		{
			MySQLConnectionManager.close(stmt, connection);
		}
		
		return result;
	}

	public static void setImportTime(String spoolerName, Date importTime)
	{
		Connection con = null;
		Statement stmt = null;

		try
		{
			con = MySQLConnectionManager.getAutoCommitConnection();
			stmt = con.createStatement();
			stmt.executeUpdate("INSERT INTO Import VALUES ('" + Dates.toMySQLdate(importTime) + "', '" + spoolerName + "')");
		}
		catch (Exception e)
		{
			// TODO: Throw.
			logger.error("getLastImportTime(" + spoolerName + ")", e); 
		}
		finally
		{
			MySQLConnectionManager.close(stmt, con);
		}
	}
}
