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

package com.trifork.stamdata.spooler;


import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.spooler.FileSpoolerSetup;

/**
 * FileSpoolerTest. Tests that setup of files and filesets are handled correct.
 *
 * @author Jan Buchholdt
 */

public class FileSpoolerSetupTest {


    @Before
    public void setupTest() throws Exception {
    }

    @After
    public void cleanUpTest() throws Exception {
    }

    @Test
    public void runSetupTest() throws Exception {
        String spoolerDir = "file://" + System.getProperty("java.io.tmpdir") + "/FileSpoolerTest";

        FileSpoolerSetup setup = new FileSpoolerSetup("TestSpooler", spoolerDir, TestFileImporter.class);
        Assert.assertEquals(setup.rootDir, spoolerDir + "/TestSpooler");
        Assert.assertEquals(setup.getInputPath(), spoolerDir + "/TestSpooler/" + FileSpoolerSetup.INPUT_DIR);
        Assert.assertEquals(setup.getRejectPath(), spoolerDir + "/TestSpooler/" + FileSpoolerSetup.REJECT_DIR);
        Assert.assertEquals(setup.getProcessingPath(), spoolerDir + "/TestSpooler/" + FileSpoolerSetup.PROCESSING_DIR);
        Assert.assertEquals(setup.getStableSeconds(), FileSpoolerSetup.DEFAULT_STABLE_SECONDS);
        Assert.assertEquals(setup.getImporterClass(), TestFileImporter.class);
        setup.stableSeconds = 20;
        Assert.assertEquals(setup.getStableSeconds(), 20);

    }

}
