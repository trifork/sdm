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


package com.trifork.stamdata.importer.jobs.yderregister;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.trifork.stamdata.importer.jobs.FileParser;
import com.trifork.stamdata.importer.jobs.yderregister.model.YderregisterDatasets;
import com.trifork.stamdata.importer.persistence.Persister;


public class YderregisterImporter implements FileParser
{
	private static final Logger logger = LoggerFactory.getLogger(YderregisterImporter.class);
	
	private static final String[] requiredFileExt = new String[] { "K05", "K40", "K45", "K1025", "K5094" };

	@SuppressWarnings("unchecked")
	@Override
	public void importFiles(File[] input, Persister persister) throws Exception
	{
		String versionString = null;
		int version;

		for (File f : input)
		{
			String currentFileVersion;

			if (f.getName().endsWith("XML") && f.getName().length() >= 15)
			{
				currentFileVersion = f.getName().substring(10, 15);
			}
			else
			{
				logger.warn("Unknown file encountered. filename='{}' parser='{}'", f.getName(), getIdentifier());
				
				continue;
			}

			if (versionString == null)
			{
				versionString = currentFileVersion;
			}
			else if (!versionString.equals(currentFileVersion))
			{
				throw new Exception("Det blev forsøgt at importere yderregisterfiler med forskellige løbenumre. Løbenummeret fremgår af filnavnet.");
			}
		}

		if (versionString == null)
		{
			throw new Exception("Der blev ikke fundet yderregister filer med et løbenummer");
		}

		version = Integer.parseInt(versionString);

		// Verify the version

		int latestInDB = getLastVersion(persister.getConnection());

		if (latestInDB != 0 && latestInDB > version)
		{
			throw new Exception("Det blev forsøgt at indlæse et yderregister med et løbenummer, der er lavere end det seneste importerede løbenummer.");
		}

		setLastVersion(version, persister.getConnection());

		YderregisterParser parser = new YderregisterParser();
		YderregisterDatasets dataset = parser.parseYderregister(input);

		persister.persistCompleteDataset(dataset.getYderregisterDS());
		persister.persistCompleteDataset(dataset.getYderregisterPersonDS());
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

	@Override
	public String getIdentifier()
	{
		return "yderregister";
	}

	@Override
	public String getHumanName()
	{
		return "Yderregisteret Parser";
	}

	public int getLastVersion(Connection connection) throws Exception
	{
		int latestInDB = 0;

		Statement stm = connection.createStatement();
		ResultSet rs = stm.executeQuery("SELECT MAX(Loebenummer) FROM YderLoebenummer");

		if (rs.next())
		{
			latestInDB = rs.getInt(1);
		}

		stm.close();

		return latestInDB;
	}

	public void setLastVersion(int version, Connection connection) throws Exception
	{
		Statement stm = connection.createStatement();
		stm.execute("INSERT INTO YderLoebenummer (Loebenummer) VALUES (" + version + "); ");
		stm.close();
	}
}
