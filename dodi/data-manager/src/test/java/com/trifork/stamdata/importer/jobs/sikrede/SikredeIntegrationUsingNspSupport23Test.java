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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;

import org.junit.Test;

import com.trifork.stamdata.importer.FileParserIntegrationTest;

/**
 * The data for this test is taken from the jira ticket NSP-SUPPORT 23
 */
public class SikredeIntegrationUsingNspSupport23Test extends FileParserIntegrationTest
{
    public SikredeIntegrationUsingNspSupport23Test()
    {
        super("sikrede");
    }

    @Test
    public void testCanImportAFileSetPlacedInTheInbox() throws Exception
    {
        File fileSet = getDirectory("data/sikrede/NSPSUPPORT23");

        placeInInbox(fileSet, true);

        assertThat(isLocked(), is(false));

        assertRecordCount("Sikrede", 80);
    }
}
