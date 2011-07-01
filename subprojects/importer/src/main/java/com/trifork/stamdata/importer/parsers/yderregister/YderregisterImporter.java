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

package com.trifork.stamdata.importer.parsers.yderregister;

import java.io.File;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.parsers.FileImporterControlledIntervals;
import com.trifork.stamdata.importer.parsers.exceptions.FileImporterException;
import com.trifork.stamdata.importer.parsers.exceptions.FilePersistException;
import com.trifork.stamdata.importer.parsers.yderregister.model.YderregisterDatasets;


public class YderregisterImporter implements FileImporterControlledIntervals
{

	private static final String[] requiredFileExt = new String[] { "K05", "K40", "K45", "K1025", "K5094" };

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public void run(List<File> files) throws FileImporterException
	{

		// get the Loebenummer from the files and verify that all files have the
		// same loebenummer
		String loebeNummerString = null;
		int loebeNummer;
		for (File f : files)
		{
			String curFileLoebe;

			if (f.getName().endsWith("XML") && f.getName().length() >= 15)
			{
				curFileLoebe = f.getName().substring(10, 15);
			}
			else
			{
				continue;
			}

			if (loebeNummerString == null)
			{
				loebeNummerString = curFileLoebe;
			}
			else
			{
				if (!loebeNummerString.equals(curFileLoebe))
				{
					throw new FileImporterException("Det blev forsøgt at importere yderregisterfiler med forskellige løbenumre. Løbenummeret fremgår af filnavnet");
				}
			}
		}

		if (loebeNummerString == null)
		{
			throw new FileImporterException("Der blev ikke fundet yderregister filer med et løbenummer");
		}

		loebeNummer = Integer.parseInt(loebeNummerString);

		Connection con = MySQLConnectionManager.getConnection();

		try
		{
			YderregisterDao dao = new YderregisterDao(con);

			// verify loebenummer
			int latestInDB = dao.getLastLoebenummer();

			if (latestInDB != 0)
			{
				if (latestInDB > loebeNummer)
				{
					throw new FilePersistException("Det blev forsøgt at indlæse et yderregister med et løbenummer, der er lavere end det seneste importerede løbenummer.");
				}
			}
			dao.setLastLoebenummer(loebeNummer);

			logger.debug("Starting to parse yderregister");
			YderregisterParser tp = new YderregisterParser();
			YderregisterDatasets yderreg = tp.parseYderregister(files);
			logger.debug("Yderregister parsed");

			logger.debug("Starting to import yderregister into database");
			dao.persistCompleteDataset(yderreg.getYderregisterDS());
			dao.persistCompleteDataset(yderreg.getYderregisterPersonDS());
			logger.debug("Done importing yderregister into database");
			con.commit();
		}
		catch (Exception e)
		{
			logger.error("An error occured while persisting the yderregister to database " + e.getMessage(), e);
			throw new FilePersistException("An error occured while persisting the yderregister to database: " + e.getMessage(), e);
		}
		finally
		{
			MySQLConnectionManager.close(con);
		}

	}

	public boolean checkRequiredFiles(List<File> files)
	{
		logger.debug("Checking yderregister file list for presence of all required files");

		Map<String, File> fileMap = new HashMap<String, File>(files.size());
		for (File f : files)
		{
			String fName = f.getName();
			if (fName.indexOf('.') != fName.lastIndexOf('.'))
			{
				fileMap.put(fName.substring(fName.indexOf('.') + 1, fName.lastIndexOf('.')), f);
			}
		}

		for (String reqFileExt : Arrays.asList(requiredFileExt))
		{
			if (!fileMap.containsKey(reqFileExt))
			{
				logger.debug("Did not find required file with extension: " + reqFileExt);
				return false;
			}
			logger.debug("Found required file: " + reqFileExt);
		}
		return true;
	}

	/**
	 * They should come at least each quarter
	 */
	@Override
	public Date getNextImportExpectedBefore(Date lastImport)
	{
		Calendar cal;
		if (lastImport == null)
		{
			cal = Calendar.getInstance();
		}
		else
		{
			cal = Calendar.getInstance();
			cal.setTime(lastImport);
		}

		cal.add(Calendar.DATE, 95);

		return cal.getTime();
	}
}
