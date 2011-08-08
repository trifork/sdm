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

package com.trifork.stamdata.importer.jobs.sks;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.jobs.FileParser;
import com.trifork.stamdata.importer.persistence.Dataset;
import com.trifork.stamdata.importer.persistence.Persister;


/**
 * Parser for the SKS register.
 * 
 * SKS is an acronym for 'Sundhedsvæsenets KlassifikationsSystem'.
 */
public class SksImporter implements FileParser
{
	private static final Logger logger = LoggerFactory.getLogger(SksImporter.class);

	@Override
	public boolean ensureRequiredFileArePresent(File[] input)
	{
		boolean present = false;

		for (File file : input)
		{
			if (file.getName().toUpperCase().endsWith(".TXT")) present = true;
		}

		return present;
	}

	@Override
	public void importFiles(File[] files, Persister persister) throws Exception
	{
		for (File file : files)
		{
			if (file.getName().toUpperCase().endsWith(".TXT"))
			{
				Dataset<Organisation> dataset = SksParser.parseOrganisationer(file);
				persister.persistDeltaDataset(dataset);
				
				logger.info("Parsing file completed. num_records={}, file={}", dataset.getEntities().size(), file.getName());
			}
			else
			{
				logger.warn("Ignoring file, which neither matches *.TXT. File: " + file.getAbsolutePath());
			}
		}
	}

	@Override
	public String getIdentifier()
	{
		return "sks";
	}

	@Override
	public String getHumanName()
	{
		return "SKS Parser";
	}
}
