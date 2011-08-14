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

import java.io.File;
import java.sql.*;
import java.util.StringTokenizer;

import org.apache.commons.io.*;
import org.joda.time.*;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.trifork.stamdata.importer.jobs.FileParserJob;
import com.trifork.stamdata.importer.persistence.*;
import com.trifork.stamdata.importer.util.Dates;


public class AutorisationsregisterParser implements FileParserJob
{	
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

	@Override
	public void run(File[] files, Persister persister, Connection connection, long changeset) throws Exception
	{
		// Make sure the file set has not been imported before.
		// Check what the previous highest version is (the ValidFrom column).
		

		ResultSet rows = connection.createStatement().executeQuery("SELECT MAX(ReleaseDate) AS ReleaseDate FROM AutorisationVersion");

		rows.next();
		DateTime previousReleaseDate = (rows.getDate("ReleaseDate") != null) ? new DateTime(rows.getDate("ReleaseDate")).withZone(Dates.DK_TIMEZONE) : null;

		DateTime newVersionReleaseDate = getDateFromFilename(files[0].getName());

		if (previousReleaseDate != null && !newVersionReleaseDate.isAfter(previousReleaseDate))
		{
			throw new Exception("The version of autorisationsregister that was placed for import was out of order. previous_version=" + previousReleaseDate + ", new_version=" + newVersionReleaseDate + ".");
		}
		
		// The file in in sequence.
		
		WorkingPersister<AutorisationVersion> versionPersister = new WorkingPersister<AutorisationVersion>(changeset, true, connection, AutorisationVersion.class);
		AutorisationVersion version = new AutorisationVersion(newVersionReleaseDate.toDate());
		versionPersister.persist(version);
		versionPersister.finish();
		
		// TODO: Are there really multiple files?
		
		for (File file : files)
		{
			WorkingPersister<Autorisation> persister2 = new WorkingPersister<Autorisation>(changeset, true, connection, Autorisation.class);
			
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
				
				persister2.persist(new Autorisation(nummer, cpr, fornavn, efternavn, educationCode));
			}
			
			lineIterator.close();

			persister2.finish();
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
		tranferTheRegistry.execute(
				"INSERT INTO autreg (cpr, given_name, surname, aut_id, edu_id) " + 
				"SELECT CPR, Fornavn, Efternavn, Autorisationsnummer, Uddannelseskode " +
				"FROM (" +
				"SELECT * FROM (" +
				"SELECT MAX(EventID) AS LatestEventID, Autorisationsnummer AS TheAut " +
				"FROM VersionEvent e, Autorisation a WHERE e.EntityID = a.PID " + 
				"GROUP BY Autorisationsnummer) AS x, VersionEvent e, Autorisation a " +
				"WHERE a.Autorisationsnummer = x.TheAut AND x.LatestEventID = e.EventID AND EventType != 'DELETE') AS Y;");
		
		tranferTheRegistry.close();
	}

	private DateTime getDateFromFilename(String filename)
	{
		return Dates.DK_yyyyMMdd.parseDateTime(filename.substring(0, 8));
	}
}
