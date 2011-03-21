package dk.trifork.sdm.spooler;


import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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



