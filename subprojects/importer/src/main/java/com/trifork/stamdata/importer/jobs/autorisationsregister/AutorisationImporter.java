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

package com.trifork.stamdata.importer.jobs.autorisationsregister;

import static com.trifork.stamdata.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;

import com.trifork.stamdata.importer.jobs.FileParser;
import com.trifork.stamdata.importer.jobs.autorisationsregister.model.Autorisation;
import com.trifork.stamdata.importer.jobs.autorisationsregister.model.Autorisationsregisterudtraek;
import com.trifork.stamdata.importer.persistence.AuditingPersister;


public class AutorisationImporter implements FileParser
{
	private static final String FILE_ENCODING = "ISO8859-15";

	@Override
	public boolean ensureRequiredFileArePresent(File[] input)
	{
		// TODO: It doesn't seem like we know anything about
		// what the required files are. Therefore we just
		// make sure that there are some.

		checkNotNull(input);
		
		return (input.length > 0);
	}

	@Override
	public String getIdentifier()
	{
		return "autorisationsregister";
	}

	@Override
	public void importFiles(File[] files, AuditingPersister persister) throws Exception
	{
		for (File file : files)
		{
			Date date = getDateFromFilename(file.getName());

			Autorisationsregisterudtraek dataset = parse(file, date);
			persister.persistCompleteDataset(dataset);
		}
	}

	protected Date getDateFromFilename(String filename) throws ParseException
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		return formatter.parse(filename.substring(0, 8));
	}

	public Date getNextImportExpectedBefore(Date lastImport)
	{
		// Largest gap observed was 15 days from 2008-10-18 to 2008-11-01.

		return new DateTime(lastImport).plusMonths(1).toDate();
	}
	
	public Autorisationsregisterudtraek parse(File file, Date validFrom) throws IOException
	{
		Autorisationsregisterudtraek dataset = new Autorisationsregisterudtraek(validFrom);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), FILE_ENCODING));

		while (reader.ready())
		{
			dataset.addEntity(new Autorisation(reader.readLine()));
		}

		return dataset;
	}

	@Override
	public String getHumanName()
	{
		return "Autorisationsregisteret Parser";
	}
}
