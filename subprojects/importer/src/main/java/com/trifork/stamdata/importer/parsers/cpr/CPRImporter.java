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

package com.trifork.stamdata.importer.parsers.cpr;

import static com.trifork.stamdata.importer.util.DateUtils.yyyy_MM_dd;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.config.Configuration;
import com.trifork.stamdata.importer.parsers.FileParser;
import com.trifork.stamdata.importer.parsers.cpr.model.CPRDataset;
import com.trifork.stamdata.importer.parsers.exceptions.FilePersistException;
import com.trifork.stamdata.importer.persistence.AuditingPersister;
import com.trifork.stamdata.importer.persistence.Dataset;
import com.trifork.stamdata.importer.persistence.StamdataEntity;
import com.trifork.stamdata.importer.util.DateUtils;


public class CPRImporter implements FileParser
{
	private static final Logger logger = LoggerFactory.getLogger(CPRImporter.class);

	private Pattern personFilePattern;
	private Pattern personFileDeltaPattern;

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
		return true; // TODO: Ckech if the required files are there.
	}

	@Override
	public void importFiles(File[] input, Connection connection) throws Exception
	{
		logger.info("Starting to parse CPR file ");
		
		AuditingPersister dao = new AuditingPersister(connection);

		for (File personFile : input)
		{
			if (!isPersonerFile(personFile))
			{
				throw new FilePersistException("File " + personFile.getAbsolutePath() + " is not a valid CPR file. Nothing is imported from the fileset");
			}
		}

		for (File personFile : input)
		{
			logger.info("Starting parsing 'CPR person' file " + personFile.getAbsolutePath());

			CPRDataset cpr = CPRParser.parse(personFile);

			if (isDeltaFile(personFile))
			{
				// Check that the sequence is kept.

				Date latestIKraft = getLatestIkraft(connection);

				if (latestIKraft == null)
				{
					logger.warn("could not get latestIKraft from database. Asuming empty database and skipping import sequence checks.");
				}
				else if (!cpr.getPreviousFileValidFrom().equals(latestIKraft))
				{
					throw new FilePersistException("Forrige ikrafttrædelsesdato i personregisterfilen stemmer ikke overens med forrige ikrafttrædelsesdato i databasen. Dato i fil: [" + yyyy_MM_dd.format(cpr.getPreviousFileValidFrom().getTime()) + "]. Dato i database: " + yyyy_MM_dd.format(latestIKraft.getTime()));
				}
			}

			for (Dataset<? extends StamdataEntity> dataset : cpr.getDatasets())
			{
				dao.persistDeltaDataset(dataset);
			}

			// Add latest 'ikraft' date to database if we are not importing
			// a full set.

			if (isDeltaFile(personFile))
			{
				insertIkraft(cpr.getValidFrom(), connection);
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

	/**
	 * If no cpr in 12 days, fire alarm Maximum gap observed is 7 days without
	 * cpr during christmas 2008.
	 */
	public Date getNextImportExpectedBefore(Date lastImport)
	{
		Calendar cal = Calendar.getInstance();

		if (lastImport != null)
		{
			cal.setTime(lastImport);
		}

		cal.add(Calendar.DATE, 12);

		return cal.getTime();
	}

	static public Date getLatestIkraft(Connection con) throws FilePersistException
	{
		try
		{
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery("SELECT MAX(IkraftDato) AS Ikraft FROM PersonIkraft");
			if (rs.first()) return rs.getTimestamp(1);
			return null;
		}
		catch (SQLException sqle)
		{
			throw new FilePersistException("Der opstod en fejl under fremsøgning af seneste ikrafttrædelsesdato fra databasen.", sqle);
		}
	}

	void insertIkraft(Date calendar, Connection con) throws FilePersistException
	{
		try
		{
			Statement stm = con.createStatement();
			String query = "INSERT INTO PersonIkraft (IkraftDato) VALUES ('" + DateUtils.toMySQLdate(calendar) + "');";
			stm.execute(query);
		}
		catch (SQLException sqle)
		{
			throw new FilePersistException("Der opstod en fejl under indsættelse af ny ikrafttrædelsesdato til databasen.", sqle);
		}
	}
}
