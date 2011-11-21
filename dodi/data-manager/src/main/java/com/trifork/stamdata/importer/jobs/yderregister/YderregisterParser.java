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
import com.google.inject.Inject;
import javax.inject.Provider;
import com.trifork.stamdata.importer.config.KeyValueStore;
import com.trifork.stamdata.importer.parsers.exceptions.OutOfSequenceException;
import com.trifork.stamdata.importer.parsers.Parser;
import com.trifork.stamdata.importer.parsers.exceptions.ParserException;
import com.trifork.stamdata.importer.parsers.annotations.ParserInformation;
import com.trifork.stamdata.persistence.RecordPersister;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import javax.xml.parsers.SAXParser;
import java.io.File;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;

/**
 * @author Thomas Børlum <thb@trifork.com>
 */
@ParserInformation(id = "yderregister", name = "Yderregisteret")
public class YderregisterParser implements Parser
{
    private static final String KEY_STORE_VERSION_KEY = "version";
    private static final DateTimeFormatter VERSION_DATE_FORMAT = ISODateTimeFormat.basicDate();

	private static final List<String> REQUIRED_FILE_EXTENSIONS = ImmutableList.of("K05", "K40", "K45", "K1025", "K5094");

    private final KeyValueStore keyValueStore;
    private final SAXParser saxParser;
    private final Provider<YderregisterSaxEventHandler> saxEventHandlers;

    @Inject
    YderregisterParser(KeyValueStore keyValueStore, SAXParser saxParser, Provider<YderregisterSaxEventHandler> saxEventHandlers)
    {
        this.keyValueStore = keyValueStore;
        this.saxParser = saxParser;
        this.saxEventHandlers = saxEventHandlers;
    }

	@Override
	public void process(File input, RecordPersister persister) throws Exception
	{
        // Make sure that all the required file are there.
        //
        if (!areRequiredFilesPresent(input)) throw new ParserException("Not all required files were present.");

		String newVersion = extractVersionFromFileSet(input);

        // TODO: Ensure the import sequence, recipient etc. This cannot be done until we get more information
        // about the filename conversion.
        //
        // Currently we can ensure that we don't import an old version, by looking at the previous
        // version and ensuring that the version number is larger.
        //
		String prevVersion = keyValueStore.get(KEY_STORE_VERSION_KEY);

		if (prevVersion != null && newVersion.compareTo(prevVersion) <= 0) throw new OutOfSequenceException(prevVersion, newVersion);

		keyValueStore.put(KEY_STORE_VERSION_KEY, newVersion);

        // Do the actual importing.
        //
        for (File file : input.listFiles())
        {
            try
            {
    	        saxParser.parse(file, saxEventHandlers.get());
            }
            catch (Exception e)
            {
                throw new ParserException(e);
            }
	    }
    }

    public boolean areRequiredFilesPresent(File input)
    {
        // TODO: The implementation of this class requires
        // the presence of all the file in REQUIRED_FILE_EXTENSIONS.
        // It seems however that there only are records in the 'K05'.
        //
        // This class could be considerable easier to understand if
        // we only have to check the single file.

        Set<String> found = Sets.newHashSet();

        for (File file : input.listFiles())
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

    private String extractVersionFromFileSet(File input)
    {
        final int VERSION_START = 10;
        final int VERSION_END = 15;

        String version = null;
        
        for (File file : input.listFiles())
        {
            String versionInFilename;

            try
            {
                versionInFilename = file.getName().substring(VERSION_START, VERSION_END);
            }
            catch (StringIndexOutOfBoundsException e)
            {
                throw new ParserException(String.format("Problem in file '%s'.", file.getAbsolutePath()), e);
            }

            if (version == null)
            {
                version = versionInFilename;
            }
            else if (!version.equals(versionInFilename))
            {
                throw new ParserException(format("The data set contains files with different version numbers. (%s, %s)", version, versionInFilename));
            }
        }
        
        return version;
    }
}
