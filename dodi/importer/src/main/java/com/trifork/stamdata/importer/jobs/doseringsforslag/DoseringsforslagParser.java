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

package com.trifork.stamdata.importer.jobs.doseringsforslag;

import static org.slf4j.LoggerFactory.*;

import java.io.*;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

import org.joda.time.Period;
import org.slf4j.Logger;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.trifork.stamdata.importer.jobs.FileParserJob;
import com.trifork.stamdata.importer.persistence.*;


/**
 * Importer for drug dosage suggestions data.
 */
public class DoseringsforslagParser implements FileParserJob
{
	private static final Logger logger = getLogger(DoseringsforslagParser.class);

	private static final String JOB_IDENTIFIER = "doseringsforslag_parser";

	private final Period maxTimeGap;

	@Inject
	DoseringsforslagParser(@Named(JOB_IDENTIFIER + "." + MAX_TIME_GAP) String maxTimeGap)
	{
		this.maxTimeGap = Period.minutes(Integer.parseInt(maxTimeGap));
	}

	@Override
	public String getIdentifier()
	{
		return JOB_IDENTIFIER;
	}

	@Override
	public String getHumanName()
	{
		return "Doseringsforslag Parser";
	}

	@Override
	public Period getMaxTimeGap()
	{
		return maxTimeGap;
	}

	@Override
	public void run(File[] files, Persister persister, Connection connection, long changeset) throws Exception
	{
		// METADATA FILE
		//
		// The file contains information about the validity period
		// of the data.

		DosageVersion version = parseVersionFile(getFile(files, "DosageVersion.json"));

		// CHECK PREVIOUS VERSION
		//
		// Check that the version in imported in
		// sequence and that we haven't missed one.

		Statement versionStatement = connection.createStatement();
		ResultSet queryResults = versionStatement.executeQuery("SELECT MAX(releaseNumber) FROM DosageVersion");

		if (!queryResults.next())
		{
			throw new Exception("SQL statement returned no rows, expected NULL or int value.");
		}

		int maxVersion = queryResults.getInt(1);

		if (queryResults.wasNull())
		{
			logger.warn("No previous version of Dosage Suggestion registry found, assuming initial import.");
		}
		else if (version.getReleaseNumber() != maxVersion + 1)
		{
			throw new Exception("The Dosage Suggestion files are out of sequence! Expected " + (maxVersion + 1) + ", but was " + version.getReleaseNumber() + ".");
		}

		// The version is in sequence we can persist it.

		WorkingPersister<DosageVersion> persister2 = new WorkingPersister<DosageVersion>(changeset, true, connection, DosageVersion.class);
		persister2.persist(version);
		persister2.finish();

		// OTHER FILES
		//
		// There are data files and relation file.
		// Relation files act as one-to-one etc. relations.
		//
		// This data source represents the 'whole truth' for
		// the validity period. That means that complete
		// datasets will be used for persisting, and no existing
		// records will be valid in the period.
		//
		// We have to declare the <T> types explicitly since GSon
		// (Java is stupid) can't get the runtime types otherwise.

		Type type;

		type = new TypeToken<Map<String, Collection<Drug>>>()
		{
		}.getType();
		parseDataFile(changeset, connection, getFile(files, "Drugs.json"), "drugs", version, Drug.class, type);

		type = new TypeToken<Map<String, Collection<DosageUnit>>>()
		{
		}.getType();
		parseDataFile(changeset, connection, getFile(files, "DosageUnits.json"), "dosageUnits", version, DosageUnit.class, type);

		type = new TypeToken<Map<String, Collection<DosageStructure>>>()
		{
		}.getType();
		parseDataFile(changeset, connection, getFile(files, "DosageStructures.json"), "dosageStructures", version, DosageStructure.class, type);

		type = new TypeToken<Map<String, Collection<DrugDosageStructureRelation>>>()
		{
		}.getType();
		parseDataFile(changeset, connection, getFile(files, "DrugsDosageStructures.json"), "drugsDosageStructures", version, DrugDosageStructureRelation.class, type);

		logger.info("Dosage Suggestion Registry v" + version.getReleaseNumber() + " was successfully imported.");
	}

	/**
	 * Parses Version.json files.
	 */
	private DosageVersion parseVersionFile(File file) throws FileNotFoundException
	{
		Reader reader = new InputStreamReader(new FileInputStream(file));

		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		Type type = new TypeToken<Map<String, DosageVersion>>()
		{
		}.getType();

		Map<String, DosageVersion> versions = gson.fromJson(reader, type);

		return versions.get("version");
	}

	/**
	 * Parses other data files.
	 * 
	 * @throws Exception
	 */
	private <T extends Record> void parseDataFile(long changeset, Connection connection, File file, String root, DosageVersion version, Class<T> type, Type collectionType) throws Exception
	{
		Reader reader = new InputStreamReader(new FileInputStream(file));

		Map<String, List<T>> parsedData = new Gson().fromJson(reader, collectionType);
		WorkingPersister<T> persister = new WorkingPersister<T>(changeset, true, connection, type);

		for (T structure : parsedData.get(root))
		{
			persister.persist(structure);
		}

		persister.finish();
	}

	@Override
	public boolean checkFileSet(File[] input)
	{
		// ALL THE FOLLOWING FILES MUST BE PRESENT
		//
		// The import will fail if not all of the files can be found.

		boolean present = true;

		present &= getFile(input, "DosageStructures.json") != null;
		present &= getFile(input, "DosageUnits.json") != null;
		present &= getFile(input, "Drugs.json") != null;
		present &= getFile(input, "DrugsDosageStructures.json") != null;
		present &= getFile(input, "DosageVersion.json") != null;

		return present;
	}

	/**
	 * Searches the provided file array for a specific file name.
	 * 
	 * @param files
	 *            the list of files to search.
	 * @param name
	 *            the file name of the file to return.
	 * 
	 * @return the file with the specified name or null if no file is found.
	 */
	private File getFile(File[] files, String name)
	{
		File result = null;

		for (File file : files)
		{

			if (file.getName().equals(name))
			{
				result = file;
				break;
			}
		}

		return result;
	}
}
