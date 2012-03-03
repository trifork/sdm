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

import com.google.inject.Inject;
import com.trifork.stamdata.importer.config.KeyValueStore;
import com.trifork.stamdata.importer.parsers.Parser;
import com.trifork.stamdata.importer.parsers.annotations.ParserInformation;
import com.trifork.stamdata.importer.parsers.exceptions.OutOfSequenceException;
import com.trifork.stamdata.importer.parsers.exceptions.ParserException;
import com.trifork.stamdata.persistence.RecordPersister;
import dk.sdsd.nsp.slalog.api.SLALogItem;

import javax.inject.Provider;
import javax.xml.parsers.SAXParser;
import java.io.File;

import static com.trifork.stamdata.importer.tools.SLALoggerHolder.getSLALogger;

/**
 * @author Jan Buchholdt <jbu@trifork.com>
 */
@ParserInformation(id = "yderregister", name = "Yderregisteret")
public class YderregisterParser implements Parser {
    private static final String KEY_STORE_VERSION_KEY = "version";

    private final KeyValueStore keyValueStore;
    private final SAXParser saxParser;
    private final Provider<YderregisterSaxEventHandler> saxEventHandlers;

    @Inject
    YderregisterParser(KeyValueStore keyValueStore, SAXParser saxParser, Provider<YderregisterSaxEventHandler> saxEventHandlers) {
        this.keyValueStore = keyValueStore;
        this.saxParser = saxParser;
        this.saxEventHandlers = saxEventHandlers;
    }

    @Override
    public void process(File input, RecordPersister persister) throws Exception {
        SLALogItem slaLogItem = getSLALogger().createLogItem("YderregisterParser", input != null ? input.getName() : "no input file");
        try {
            // Make sure that all the required file are there.
            //
            if (!areRequiredFilesPresent(input)) throw new ParserException("Not all required files were present.");

            // Do the actual importing (there is only one file).
            //
            File file = input.listFiles()[0];

            YderregisterSaxEventHandler eventHandler = saxEventHandlers.get();
            saxParser.parse(file, eventHandler);

            String newVersion = eventHandler.GetVersionFromFileSet();

            if (newVersion == null) throw new ParserException("No version string was extracted from the file set.");

            // Ensure the import sequence.
            //
            // Currently we can ensure that we don't import an old version, by looking at the previous
            // version and ensuring that the version number is larger.
            //
            String prevVersion = keyValueStore.get(KEY_STORE_VERSION_KEY);

            // TODO: Ensure the new version is also the expected version (e.i. we have not skipped anything).

            if (prevVersion != null && newVersion.compareTo(prevVersion) <= 0) {
                throw new OutOfSequenceException(prevVersion, newVersion);
            }

            keyValueStore.put(KEY_STORE_VERSION_KEY, newVersion);

            slaLogItem.setCallResultOk();
            slaLogItem.store();
        } catch (Exception e) {
            slaLogItem.setCallResultError("YderregisterParser failed - Cause: " + e.getMessage());
            slaLogItem.store();

            throw e;
        }
    }

    public boolean areRequiredFilesPresent(File input) {
        return input.listFiles().length == 1;
    }
}
