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
package com.trifork.stamdata.importer.jobs.sikrede;

import static com.trifork.stamdata.Preconditions.checkNotNull;

import java.io.File;

import com.trifork.stamdata.importer.config.KeyValueStore;
import com.trifork.stamdata.importer.jobs.FileParser;
import com.trifork.stamdata.importer.persistence.Persister;

public class SikredeParserUsingNewArchitecture implements FileParser {

    @Override
    public String getIdentifier() 
    {
        return "sikrede";
    }

    @Override
    public String getHumanName() 
    {
        return "\"Sikrede\" Parser";
    }

    @Override
    public boolean ensureRequiredFileArePresent(File[] input) 
    {
        // FIXME: What are the expected names? We don't want to see unexpected filenames
        checkNotNull(input);
        return (input.length > 0);
    }

    @Override
    public void parse(File[] input, Persister persister, KeyValueStore keyValueStore) throws Exception 
    {
        // FIXME: Implement
    }

}
