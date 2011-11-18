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

import com.trifork.stamdata.importer.parsers.Parser;
import com.trifork.stamdata.importer.parsers.exceptions.ParserException;
import com.trifork.stamdata.importer.parsers.Parsers;
import org.joda.time.DateTime;
import org.joda.time.Instant;

import com.trifork.stamdata.importer.config.ConnectionManager;
import com.trifork.stamdata.importer.util.Dates;

// FIXME (thb): This class serves no purpose and can be refactored into the FileParserJob class.
public final class ImportTimeManager
{
    private final static ConnectionManager connectionManager = new ConnectionManager();

    public static DateTime getLastImportTime(Class<? extends Parser> parser, Connection connection)
    {
        String parserId = Parsers.getIdentifier(parser);
        return getLastImportTime(parserId);
    }

	public static DateTime getLastImportTime(String parserId)
	{
		Connection connection = null;
		DateTime result = null;

		try
		{
            connection = connectionManager.getAutoCommitConnection();
            return getLastImportTime(parserId, connection);
		}
		finally
		{
			ConnectionManager.closeQuietly(connection);
		}
	}

    private static DateTime getLastImportTime(String parserId, Connection connection)
    {
        DateTime result = null;

        try
        {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(importtime) FROM Import WHERE spoolername = '" + parserId + "'");

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

            stmt.close();
        }
        catch (Exception e)
        {
            throw new ParserException("Could not get the last import time from the database.", e);
        }

        return result;
    }

    @Deprecated
	public static void setImportTime(String parserName, Date importTime)
	{
        // FIXME (thb): This should NOT create its own connection but use the file parser job's.
        // This will not be rolled back if an error occurs.

        Connection connection = null;

		try
		{
			connection = connectionManager.getAutoCommitConnection();
            setLastImportTime(parserName, importTime, connection);
		}
		finally
		{
			ConnectionManager.closeQuietly(connection);
		}
	}
    
    public static void setLastImportTime(Class<? extends Parser> parserClass, Instant transactionTime, Connection connection)
    {
        String parserId = Parsers.getIdentifier(parserClass);
        setLastImportTime(parserId, transactionTime.toDate(), connection);
    }

    private static void setLastImportTime(String parserId, Date transactionTime, Connection connection)
    {
        Statement stmt = null;

        try
        {
            stmt = connection.createStatement();
            stmt.executeUpdate("INSERT INTO Import VALUES ('" + Dates.toSqlDate(transactionTime) + "', '" + parserId + "')");
        }
        catch (Exception e)
        {
            throw new ParserException("Could not set the import time in the database.", e);
        }
        finally
        {

        }
    }
}
