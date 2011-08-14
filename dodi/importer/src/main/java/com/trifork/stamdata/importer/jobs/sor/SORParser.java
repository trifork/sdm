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

import static com.google.common.base.Preconditions.*;

import java.io.File;

import javax.xml.parsers.*;

import org.joda.time.Period;
import org.slf4j.*;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.trifork.stamdata.importer.jobs.FileParserJob;
import com.trifork.stamdata.importer.persistence.Persister;


/**
 * Parser for the SOR register.
 * 
 * SOR is an acronym for 'Sundhedsvæsenets Organisationsregister'.
 */
public class SORParser implements FileParserJob
{
	private static final Logger logger = LoggerFactory.getLogger(SORParser.class);

	private static final String JOB_IDENTIFIER = "sor_parser";

	private final Period maxTimeGap;

	@Inject
	SORParser(@Named(JOB_IDENTIFIER + "." + MAX_TIME_GAP) String maxTimeGap)
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
		return "SOR Parser";
	}

	@Override
	public Period getMaxTimeGap()
	{
		return maxTimeGap;
	}

	@Override
	public boolean checkFileSet(File[] input)
	{
		checkNotNull(input, "input");
		
		if (input.length == 0)
		{
			return false;
		}

		boolean present = false;

		for (File file : input)
		{
			if (file.getName().toLowerCase().endsWith(".xml"))
			{
				present = true;
			}
		}

		return present;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void run(File[] files, Persister persister) throws Exception
	{
		checkNotNull(files, "file");
		checkNotNull(persister, "persister");
		
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

	public static SORDataSets parse(File file) throws Exception
	{
		checkNotNull(file, "file");
		
		SORDataSets dataSets = new SORDataSets();
		SOREventHandler handler = new SOREventHandler(dataSets);
		SAXParserFactory factory = SAXParserFactory.newInstance();

		try
		{
			SAXParser parser = factory.newSAXParser();

			if (file.getName().toLowerCase().endsWith("xml"))
			{
				parser.parse(file, handler);
			}
			else
			{
				logger.warn("Can only parse files with extension 'xml'! The file is ignored. file={}", file.getAbsolutePath());
			}
		}
		catch (Exception e)
		{
			throw new Exception("Error parsing data from file: " + file.getAbsolutePath(), e);
		}

		return dataSets;
	}
}
