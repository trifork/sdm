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

package com.trifork.stamdata.importer.jobs;

import java.sql.*;
import java.util.Date;

import org.joda.time.DateTime;


// FIXME (thb): This class serves no purpose and can be refactored into the FileParserJob class.
public class ImportTimeManager
{
	public static DateTime getLastRunTime(Connection connection, Job job)
	{
		Statement fetchLastRuntime = null;
		DateTime result = null;

		try
		{
			fetchLastRuntime = connection.createStatement();
			ResultSet rs = fetchLastRuntime.executeQuery("SELECT MAX(importtime) FROM Import WHERE spoolername = '" + job.getIdentifier() + "'");

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
		catch (SQLException e)
		{
			throw new RuntimeException("Could not get the last run time. job=" + job.getIdentifier(), e);
		}
		finally
		{
			try
			{
				if (fetchLastRuntime != null) fetchLastRuntime.close();
			}
			catch (Exception e)
			{

			}
		}

		return result;
	}

	public static void updateLastRunTime(Connection connection, Job job)
	{
		PreparedStatement updateLastRunTime = null;

		try
		{
			updateLastRunTime = connection.prepareStatement("INSERT INTO Import VALUES (?, ?)");
			updateLastRunTime.setObject(1, new Date());
			updateLastRunTime.setString(1, job.getIdentifier());
			updateLastRunTime.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new RuntimeException("Could not update the last run time for a job. job=" + job.getIdentifier(), e);
		}
		finally
		{
			try
			{
				if (updateLastRunTime != null) updateLastRunTime.close();
			}
			catch (Exception e)
			{

			}
		}
	}
}
