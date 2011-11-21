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

import com.google.inject.Provider;
import com.trifork.stamdata.Fetcher;
import com.trifork.stamdata.importer.FileParserIntegrationTest;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.trifork.stamdata.importer.config.ConnectionManager;
import com.trifork.stamdata.persistence.RecordFetcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class YderregisterIntegrationTest extends FileParserIntegrationTest
{
    private Provider connections;

    public YderregisterIntegrationTest()
    {
        super("yderregister");
    }

    @Test
    public void testThatASeriesOfYderregisterDataSetsCanBeImportedResultingInTheExpectedDataInTheDatabase() throws IOException, InterruptedException, SQLException
    {
        File fileSet1 = getDirectory("data/yderregister/initial");

        placeInInbox(fileSet1, true);

        Thread.sleep(10000);

        while (isInProgress()) { Thread.sleep(500); }

        assertThat(isLocked(), is(false));

        assertRecordCount("Yderregister", 12);
        assertRecordCount("YderregisterPerson", 21);
    }
}
