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


package com.trifork.stamdata.importer.jobs.sor;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.*;

import com.trifork.stamdata.importer.parsers.exceptions.ParserException;
import com.trifork.stamdata.importer.persistence.Persister;
import org.slf4j.*;

import com.trifork.stamdata.importer.config.KeyValueStore;
import com.trifork.stamdata.importer.jobs.FileParser;
import org.xml.sax.SAXException;


/**
 * Parser for the SOR register.
 * 
 * SOR is an acronym for 'Sundhedsvæsenets Organisationsregister'.
 */
public class SORImporter implements FileParser
{
	private static final Logger logger = LoggerFactory.getLogger(SORImporter.class);

	@Override
	public String identifier()
	{
		return "sor";
	}

	@Override
	public String name()
	{
		return "SOR Parser";
	}

	@Override
	public boolean validateInputStructure(File[] input)
	{
		if (input.length == 0) return false;

		boolean present = false;

		for (File file : input)
		{
			if (file.getName().toLowerCase().endsWith(".xml")) present = true;
		}

		return present;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void parse(File[] files, Persister persister, KeyValueStore keyValueStore) throws Exception
	{
		for (File file : files)
		{
            MDC.put("filename", file.getName());
            
			SORDataSets dataSets = parse(file);
			persister.persistCompleteDataset(dataSets.getPraksisDS());
			persister.persistCompleteDataset(dataSets.getYderDS());
			persister.persistCompleteDataset(dataSets.getSygehusDS());
			persister.persistCompleteDataset(dataSets.getSygehusAfdelingDS());
			persister.persistCompleteDataset(dataSets.getApotekDS());

            MDC.remove("filename");
		}
	}

	public static SORDataSets parse(File file) throws SAXException, ParserConfigurationException, IOException
    {
		SORDataSets dataSets = new SORDataSets();
		SOREventHandler handler = new SOREventHandler(dataSets);
		SAXParserFactory factory = SAXParserFactory.newInstance();

        SAXParser parser = factory.newSAXParser();

        if (file.getName().toLowerCase().endsWith("xml"))
        {
            parser.parse(file, handler);
        }
        else
        {
            logger.warn("Can only parse files with extension 'xml'! The file is ignored. file={}", file.getAbsolutePath());
        }

		return dataSets;
	}
}
