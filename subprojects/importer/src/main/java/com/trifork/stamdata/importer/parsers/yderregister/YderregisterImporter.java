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
import java.util.Map;

import com.google.common.collect.Maps;
import com.trifork.stamdata.importer.parsers.FileParser;
import com.trifork.stamdata.importer.parsers.exceptions.FileImporterException;
import com.trifork.stamdata.importer.parsers.exceptions.FilePersistException;
import com.trifork.stamdata.importer.parsers.yderregister.model.YderregisterDatasets;


public class YderregisterImporter implements FileParser
{
	private static final String[] requiredFileExt = new String[] { "K05", "K40", "K45", "K1025", "K5094" };

	@Override
	public void importFiles(File[] input, Connection connection) throws Exception
	{
		String versionString = null;
		int version;

		for (File f : input)
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

			if (versionString == null)
			{
				versionString = curFileLoebe;
			}
			else
			{
				if (!versionString.equals(curFileLoebe))
				{
					throw new FileImporterException("Det blev forsøgt at importere yderregisterfiler med forskellige løbenumre. Løbenummeret fremgår af filnavnet");
				}
			}
		}

		if (versionString == null)
		{
			throw new FileImporterException("Der blev ikke fundet yderregister filer med et løbenummer");
		}

		version = Integer.parseInt(versionString);

		YderregisterDao dao = new YderregisterDao(connection);

		// Verify the version
		
		int latestInDB = dao.getLastLoebenummer();

		if (latestInDB != 0)
		{
			if (latestInDB > version)
			{
				throw new FilePersistException("Det blev forsøgt at indlæse et yderregister med et løbenummer, der er lavere end det seneste importerede løbenummer.");
			}
		}

		dao.setLastVersion(version);

		YderregisterParser tp = new YderregisterParser();
		YderregisterDatasets yderreg = tp.parseYderregister(input);

		dao.persistCompleteDataset(yderreg.getYderregisterDS());
		dao.persistCompleteDataset(yderreg.getYderregisterPersonDS());
	}

	public boolean ensureRequiredFileArePresent(File[] input)
	{
		Map<String, File> fileMap = Maps.newHashMap();
		
		for (File f : input)
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
				return false;
			}
		}
		
		return true;
	}

	/**
	 * They should come at least each quarter
	 */
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
	
	public String getIdentifier()
	{
		return "yderregister";
	}
}
