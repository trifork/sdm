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

package com.trifork.stamdata.importer.jobs.cpr;

import static com.trifork.stamdata.Preconditions.checkNotNull;
import static com.trifork.stamdata.importer.util.DateUtils.yyyy_MM_dd;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.config.Configuration;
import com.trifork.stamdata.importer.jobs.FileParser;
import com.trifork.stamdata.importer.jobs.cpr.model.CPRDataset;
import com.trifork.stamdata.importer.persistence.Dataset;
import com.trifork.stamdata.importer.persistence.Persister;
import com.trifork.stamdata.importer.persistence.StamdataEntity;
import com.trifork.stamdata.importer.util.DateUtils;


public class CPRImporter implements FileParser
{
	private static final Logger logger = LoggerFactory.getLogger(CPRImporter.class);

	private final Pattern personFilePattern;
	private final Pattern personFileDeltaPattern;

	public CPRImporter()
	{
		personFilePattern = Pattern.compile(Configuration.getString("spooler.cpr.file.pattern.person"));
		personFileDeltaPattern = Pattern.compile(Configuration.getString("spooler.cpr.file.pattern.person.delta"));
	}

	@Override
	public String getIdentifier()
	{
		return "cpr";
	}

	@Override
	public boolean ensureRequiredFileArePresent(File[] input)
	{
		return true; // TODO: Check if the required files are there.
	}

	@Override
	public void importFiles(File[] input, Persister persister) throws Exception
	{
		checkNotNull(input);
		checkNotNull(persister);
		
		logger.info("Starting to parse CPR file ");

		for (File personFile : input)
		{
			if (!isPersonerFile(personFile))
			{
				throw new Exception("File " + personFile.getAbsolutePath() + " is not a valid CPR file. Nothing will be imported from the fileset.");
			}
		}

		// Check that the sequence is kept.

		Connection connection = persister.getConnection();

		for (File personFile : input)
		{
			logger.info("Starting parsing 'CPR person' file " + personFile.getAbsolutePath());

			CPRDataset cpr = CPRParser.parse(personFile);

			if (isDeltaFile(personFile))
			{
				// HACK: Don't use the connection this way. @see
				// Persister#getConnection()

				Date previousVersion = getLatestVersion(connection);

				if (previousVersion == null)
				{
					logger.debug("Find any previous versions of CPR. Asuming an initial import and skipping sequence checks.");
				}
				else if (!cpr.getPreviousFileValidFrom().equals(previousVersion))
				{
					throw new Exception("Forrige ikrafttrædelsesdato i personregisterfilen stemmer ikke overens med forrige ikrafttrædelsesdato i databasen. Dato i fil: [" + yyyy_MM_dd.format(cpr.getPreviousFileValidFrom().getTime()) + "]. Dato i database: " + yyyy_MM_dd.format(previousVersion.getTime()));
				}
			}

			for (Dataset<? extends StamdataEntity> dataset : cpr.getDatasets())
			{
				persister.persistDeltaDataset(dataset);
			}

			// Add latest 'version' date to database if we are not importing
			// a full set.

			if (isDeltaFile(personFile))
			{
				insertVersion(cpr.getValidFrom(), connection);
			}
		}
	}

	private boolean isPersonerFile(File f)
	{
		return personFilePattern.matcher(f.getName()).matches();
	}

	private boolean isDeltaFile(File f)
	{
		return personFileDeltaPattern.matcher(f.getName()).matches();
	}

	static public Date getLatestVersion(Connection con) throws SQLException
	{
		Statement stm = con.createStatement();
		ResultSet rs = stm.executeQuery("SELECT MAX(IkraftDato) AS Ikraft FROM PersonIkraft");
		if (rs.first()) return rs.getTimestamp(1);

		// Returns null if no previous version of CPR has been imported.
		
		return null; 
	}

	void insertVersion(Date calendar, Connection con) throws SQLException
	{
		Statement stm = con.createStatement();
		String query = "INSERT INTO PersonIkraft (IkraftDato) VALUES ('" + DateUtils.toMySQLdate(calendar) + "');";
		stm.execute(query);
	}

	@Override
	public String getHumanName()
	{
		return "CPR Parser";
	}
}
