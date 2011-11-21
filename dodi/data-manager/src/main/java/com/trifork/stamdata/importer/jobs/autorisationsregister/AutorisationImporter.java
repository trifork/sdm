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


package com.trifork.stamdata.importer.jobs.autorisationsregister;

import static com.trifork.stamdata.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.StringTokenizer;

import com.trifork.stamdata.importer.persistence.Persister;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.trifork.stamdata.importer.config.KeyValueStore;
import com.trifork.stamdata.importer.jobs.FileParser;
import com.trifork.stamdata.importer.util.Dates;
import com.trifork.stamdata.models.autorisationsregister.Autorisation;


public class AutorisationImporter implements FileParser
{
	private static final String FILENAME_DATE_FORMAT = "yyyyMMdd";
	private static final String FILE_ENCODING = "ISO8859-15";

	@Override
	public boolean validateInputStructure(File[] input)
	{
		// TODO: It doesn't seem like we know anything about
		// what the required files are. Therefore we just
		// make sure that there are some.

		checkNotNull(input);

		return (input.length > 0);
	}

	@Override
	public String identifier()
	{
		return "autorisationsregister";
	}

	@Override
	public String name()
	{
		return "Autorisationsregisteret Parser";
	}

	@SuppressWarnings("unchecked")
	@Override
	public void parse(File[] files, Persister persister, KeyValueStore keyValueStore) throws Exception
	{
		// Make sure the file set has not been imported before.
		// Check what the previous highest version is (the ValidFrom column).

		Connection connection = persister.getConnection();
		ResultSet rows = connection.createStatement().executeQuery("SELECT MAX(ValidFrom) as version FROM Autorisation");

		// There will always be a next here, but it might be null.

		rows.next();
		Timestamp previousVersion = rows.getTimestamp("version");

		DateTime currentVersion = getDateFromFilename(files[0].getName());

		if (previousVersion != null && !currentVersion.isAfter(previousVersion.getTime()))
		{
			throw new Exception("The version of autorisationsregister that was placed for import was out of order. current_version='" + previousVersion + "', new_version='" + currentVersion + "'.");
		}

		for (File file : files)
		{
			Autorisationsregisterudtraek dataset = parse(file, currentVersion);
			persister.persistCompleteDataset(dataset);
		}
		
		// Update the table for the STS.
		
		Statement statement = connection.createStatement();

		statement.executeUpdate("TRUNCATE TABLE autreg");
		statement.executeUpdate("INSERT INTO autreg (cpr, given_name, surname, aut_id, edu_id) SELECT cpr, Fornavn, Efternavn, Autorisationsnummer, UddannelsesKode FROM Autorisation WHERE ValidFrom <= NOW() AND ValidTo > NOW();");

		statement.close();
	}

	protected DateTime getDateFromFilename(String filename)
	{
		DateTimeFormatter formatter = DateTimeFormat.forPattern(FILENAME_DATE_FORMAT);
		return formatter.parseDateTime(filename.substring(0, 8));
	}

	public Autorisationsregisterudtraek parse(File file, DateTime validFrom) throws IOException
	{
		Autorisationsregisterudtraek dataset = new Autorisationsregisterudtraek(validFrom.toDate());

		LineIterator lineIterator = FileUtils.lineIterator(file, FILE_ENCODING);

		while (lineIterator.hasNext())
		{
			String line = lineIterator.nextLine();
			
			StringTokenizer st = new StringTokenizer(line, ";");
			
			Autorisation autorisation = new Autorisation();
			
			autorisation.setAutorisationnummer(st.nextToken());
			autorisation.setCpr(st.nextToken());
			autorisation.setEfternavn(st.nextToken());
			autorisation.setFornavn(st.nextToken());
			autorisation.setUddannelsesKode(st.nextToken());
			
			autorisation.setValidFrom(validFrom.toDate());
			autorisation.setValidTo(Dates.THE_END_OF_TIME);
			
			dataset.add(autorisation);
		}

		return dataset;
	}
}
