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

package com.trifork.stamdata.importer.jobs.autorisationsregister;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.apache.commons.io.*;
import org.joda.time.*;
import org.joda.time.format.*;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.trifork.stamdata.importer.jobs.FileParserJob;
import com.trifork.stamdata.importer.persistence.Persister;
import com.trifork.stamdata.importer.util.Dates;


public class AutorisationsregisterParser implements FileParserJob
{
	private static final DateTimeFormatter FILENAME_DATE_FORMAT = Dates.CET_yyyyMMdd;
	
	private static final String FILE_ENCODING = "ISO8859-15";
	private static final String JOB_IDENTIFIER = "autorisationsregister_parser";
	
	private final Period maxTimeGap;

	@Inject
	AutorisationsregisterParser(@Named(JOB_IDENTIFIER + "." + MAX_TIME_GAP) String maxTimeGap)
	{
		this.maxTimeGap = Period.minutes(Integer.parseInt(maxTimeGap));
	}
	
	@Override
	public boolean checkFileSet(File[] input)
	{
		// TODO: It doesn't seem like we know anything about
		// what the required files are. Therefore we just
		// make sure that there are some.
		
		Preconditions.checkNotNull(input, "input");
		
		return (input.length > 0);
	}

	@Override
	public String getIdentifier()
	{
		return JOB_IDENTIFIER;
	}

	@Override
	public String getHumanName()
	{
		return "Autorisationsregisteret Parser";
	}
	
	@Override
	public Period getMaxTimeGap()
	{
		return maxTimeGap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(File[] files, Persister persister) throws Exception
	{
		// Make sure the file set has not been imported before.
		// Check what the previous highest version is (the ValidFrom column).

		Connection connection = persister.getConnection();
		ResultSet rows = connection.createStatement().executeQuery("SELECT MAX(ValidFrom) as version FROM Autorisation");

		// There will always be a next here, but it might be null.

		rows.next();
		Timestamp previousVersion = rows.getTimestamp("version");

		DateTime newVersion = getDateFromFilename(files[0].getName());

		if (previousVersion != null && !newVersion.isAfter(previousVersion.getTime()))
		{
			throw new Exception("The version of autorisationsregister that was placed for import was out of order. current_version='" + previousVersion + "', new_version='" + newVersion + "'.");
		}

		for (File file : files)
		{
			AutorisationDataset dataset = parse(file, newVersion);
			persister.persistCompleteDataset(dataset);
		}
		
		// Once the registry has been updated we want to
		// update the table that the STS and the Authorization Lookup Service
		// uses.
		
		// TODO: This could be made a bit more intelligent so we only update
		// records that have been changed.
		
		Statement dropExistingRecords = connection.createStatement();
		dropExistingRecords.execute("TRUNCATE TABLE autreg");
		dropExistingRecords.close();
		
		Statement tranferTheRegistry = connection.createStatement();
		tranferTheRegistry.execute("INSERT INTO autreg (cpr, given_name, surname, aut_id, edu_id) SELECT cpr, Fornavn, Efternavn, Autorisationsnummer, UddannelsesKode FROM Autorisation WHERE ValidFrom <= NOW() AND ValidTo > NOW();");
		tranferTheRegistry.close();
	}

	private DateTime getDateFromFilename(String filename)
	{
		return FILENAME_DATE_FORMAT.parseDateTime(filename.substring(0, 8));
	}

	public AutorisationDataset parse(File file, DateTime validFrom) throws IOException
	{
		AutorisationDataset dataset = new AutorisationDataset(validFrom.toDate());

		LineIterator lineIterator = FileUtils.lineIterator(file, FILE_ENCODING);

		while (lineIterator.hasNext())
		{
			String line = lineIterator.nextLine();
			
			StringTokenizer st = new StringTokenizer(line, ";");
			String nummer = st.nextToken();
			String cpr = st.nextToken();
			String efternavn = st.nextToken();
			String fornavn = st.nextToken();
			String educationCode = st.nextToken();
			
			dataset.addEntity(new Autorisation(nummer, cpr, fornavn, efternavn, educationCode));
		}

		return dataset;
	}
}
