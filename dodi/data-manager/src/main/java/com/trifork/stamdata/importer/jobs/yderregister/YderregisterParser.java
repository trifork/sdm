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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.trifork.stamdata.importer.config.KeyValueStore;
import com.trifork.stamdata.importer.jobs.FileParser;
import com.trifork.stamdata.importer.jobs.sikrede.RecordPersister;
import com.trifork.stamdata.importer.parsers.dkma.ParserException;
import com.trifork.stamdata.importer.persistence.Persister;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;


public class YderregisterParser implements FileParser
{
    private static final String KEY_STORE_VERSION_KEY = "version";
    private static final DateTimeFormatter VERSION_DATE_FORMAT = ISODateTimeFormat.basicDate();

	private static final List<String> REQUIRED_FILE_EXTENSIONS = ImmutableList.of("K05", "K40", "K45", "K1025", "K5094");

    @Override
	public String getIdentifier()
	{
        // WARNING: Be careful not to change this after the first run.
        // If you do this, you will have to change any external reference
        // to it too. Such as mentions in the database.
        //
		return "yderregister";
	}

	@Override
	public String getHumanName()
	{
		return "Yderregister Parser";
	}

    @Override
    public boolean validateInputStructure(File[] input)
	{
        // FIXME: The original implementation of this class requires
        // the presence of all the file in REQUIRED_FILE_EXTENSIONS.
        // It seems however that there only are records in the 'K05'.
        //
        // This class could be considerable easier to understand if
        // we only have to check the single file.

		Set<String> found = Sets.newHashSet();

		for (File file : input)
		{
			String filename = file.getName();
            
			if (filename.indexOf('.') != filename.lastIndexOf('.'))
			{
                int extensionStart = filename.indexOf('.') + 1;
                int extensionEnd = filename.lastIndexOf('.');
                String extension = filename.substring(extensionStart, extensionEnd);

                boolean added = found.add(extension);

                if (!added) throw new ParserException(format("Multiple files with extension '%s'.", extension));
			}
		}

		return found.containsAll(REQUIRED_FILE_EXTENSIONS);
	}

	@Override
	public void parse(File[] input, Persister persister, KeyValueStore keyValueStore) throws Exception
	{
        Instant transactionTime = Instant.now();

		String newVersion = extractVersionFromFileSet(input);

        // FIXME: Ensure the import sequence. This cannot be done until we get more information
        // about the filename conversion.
        //
        // Currently we can ensure that we don't import an old version, by looking at the previous
        // version and ensuring that the version number is larger.
        //
		String prevVersion = keyValueStore.get(KEY_STORE_VERSION_KEY);

        // TODO: Ensure the new version has the correct format: yyyyMMdd
        //
		if (newVersion.compareTo(prevVersion) > 0)
		{
			throw new ParserException(format("The received version '%s' of the register is not in sequence. Current version is '%s'.", newVersion, prevVersion));
		}

		keyValueStore.put(KEY_STORE_VERSION_KEY, prevVersion);

        // Do the actual import.
        //
        RecordPersister newPersister = new RecordPersister(null, persister);
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

        for (File file : input)
        {
	        parser.parse(file, new YderRegisterSaxEventHandler(newPersister, transactionTime));
	    }
    }

    private String extractVersionFromFileSet(File[] input)
    {
        String version = null;
        
        for (File file : input)
        {
            String versionInFilename = file.getName().substring(10, 15);

            if (version == null)
            {
                version = versionInFilename;
            }
            else if (!version.equals(versionInFilename))
            {
                throw new ParserException(format("The data set contains files with different version numbers. (%s, %s)", version, versionInFilename));
            }
        }
        
        if (version == null) throw new ParserException(format("Malformed file set. No version number found."));
        
        return version;
    }
}
