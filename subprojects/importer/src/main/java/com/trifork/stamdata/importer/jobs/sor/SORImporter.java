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

package com.trifork.stamdata.importer.jobs.sor;

import java.io.File;
import java.util.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.jobs.FileParser;
import com.trifork.stamdata.importer.persistence.AuditingPersister;

/**
 *	Parser for the SOR register.
 *
 *  SOR is an acronym for 'Sundhedsvæsenets Organisationsregister'.
 */
public class SORImporter implements FileParser
{
	private static final Logger logger = LoggerFactory.getLogger(SORImporter.class);

	@Override
	public boolean ensureRequiredFileArePresent(File[] input)
	{
		if (input.length == 0) return false;

		boolean present = false;

		for (File file : input)
		{
			if (file.getName().toLowerCase().endsWith(".xml")) present = true;
		}

		return present;
	}

	@Override
	public void importFiles(File[] files, AuditingPersister persister) throws Exception
	{
		for (File file : files)
		{
			SORDataSets dataSets = parse(file);
			persister.persistCompleteDataset(dataSets.getPraksisDS());
			persister.persistCompleteDataset(dataSets.getYderDS());
			persister.persistCompleteDataset(dataSets.getSygehusDS());
			persister.persistCompleteDataset(dataSets.getSygehusAfdelingDS());
			persister.persistCompleteDataset(dataSets.getApotekDS());
		}
	}

	public Date getNextImportExpectedBefore(Date lastImport)
	{
		return new DateTime(lastImport).plusDays(3).toDate();
	}

	public static SORDataSets parse(File file) throws Exception
	{
		SORDataSets dataSets = new SORDataSets();
		SOREventHandler handler = new SOREventHandler(dataSets);
		SAXParserFactory factory = SAXParserFactory.newInstance();

		try
		{
			SAXParser parser = factory.newSAXParser();

			if (file.getName().toUpperCase().endsWith("XML"))
			{
				parser.parse(file, handler);
			}
			else
			{
				logger.warn("Can only parse files with extension 'XML'! Ignoring: " + file.getAbsolutePath());
			}
		}
		catch (Exception e)
		{
			throw new Exception("Error parsing data from file: " + file.getAbsolutePath(), e);
		}

		return dataSets;
	}

	@Override
	public String getIdentifier()
	{
		return "sor";
	}

	@Override
	public String getHumanName()
	{
		return "SOR Parser";
	}
}
