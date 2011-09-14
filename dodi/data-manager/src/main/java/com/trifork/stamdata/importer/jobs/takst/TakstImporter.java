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

package com.trifork.stamdata.importer.jobs.takst;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.jobs.FileParser;
import com.trifork.stamdata.importer.persistence.CompleteDataset;
import com.trifork.stamdata.importer.persistence.Persister;

/**
 * Parser for the DKMA register. Also known as 'Taksten'.
 * 
 * DKMA is an acroynm for 'Danish Medicines Agency'.
 */
public class TakstImporter implements FileParser
{
	private static final Logger logger = LoggerFactory.getLogger(TakstImporter.class);
	
	private static final DateTimeFormatter weekFormatter = DateTimeFormat.forPattern("xxxxww").withLocale(new Locale("da", "DK"));
	
	public boolean ensureRequiredFileArePresent(File[] input)
	{
		final String[] requiredFileNames = new String[] { "system.txt", "lms01.txt", "lms02.txt", "lms03.txt", "lms04.txt", "lms05.txt", "lms07.txt", "lms09.txt", "lms10.txt", "lms11.txt", "lms12.txt", "lms13.txt", "lms14.txt", "lms15.txt", "lms16.txt", "lms17.txt", "lms18.txt", "lms19.txt", "lms20.txt", "lms23.txt", "lms24.txt", "lms25.txt", "lms26.txt", "lms27.txt", "lms28.txt" };

		Map<String, File> fileMap = Maps.newHashMap();
		
		for (File f : input)
		{
			fileMap.put(f.getName(), f);
		}

		for (String reqFile : requiredFileNames)
		{
			if (!fileMap.containsKey(reqFile)) return false;
		}
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void importFiles(File[] input, Persister persister) throws Exception
	{
		Takst takst = new TakstParser().parseFiles(input);

		persister.persistCompleteDataset(takst.getDatasets().toArray(new CompleteDataset[] {}));
	}

	/**
	 * Der findes to typer takster: Ordinære takster og "indimellem" takster.
	 * Ordinære takster skal komme hver 14. dag. "Indimellem" takster kommer ad
	 * hoc, og vi kan ikke sætte forventning op til dem.
	 */
	public Date getNextImportExpectedBefore(Date lastImport)
	{
		Connection con = null;
		Statement stmt = null;
		DateTime ordinaryTakst = null;

		try
		{
			con = MySQLConnectionManager.getAutoCommitConnection();
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT TakstUge FROM TakstVersion WHERE validFrom IN (SELECT MAX(validFrom) FROM TakstVersion)");

			if (rs.next())
			{
				String lastWeek = rs.getString(1);

				// Next ordinary 'takst' expected 14 days after.

				// We want the ordinary 'takst' to be imported 36 hours
				// before it is suppose to be in effect.

				ordinaryTakst = weekFormatter.parseDateTime(lastWeek).plusDays(14).minusHours(36);
			}
		}
		catch (Exception e)
		{
			logger.error("Cannot get last TakstVersion from database. Could be that no 'takst' have been imported", e);
		}
		finally
		{
			MySQLConnectionManager.close(stmt, con);
		}

		if (ordinaryTakst == null)
		{
			// Something failed. Raise an alarm by setting the expected next
			// import to past time.

			ordinaryTakst = new DateTime().minusHours(1);
		}

		return ordinaryTakst.toDate();
	}
	
	public String getIdentifier()
	{
		return "dkma";
	}

	@Override
	public String getHumanName()
	{
		return "DKMA";
	}
}
