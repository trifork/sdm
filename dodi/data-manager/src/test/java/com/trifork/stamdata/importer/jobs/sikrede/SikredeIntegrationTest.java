package com.trifork.stamdata.importer.jobs.sikrede;

import com.trifork.stamdata.importer.FileParserIntegrationTest;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Ignore("Ignored until we get some propper test data.")
public class SikredeIntegrationTest extends FileParserIntegrationTest
{
    public SikredeIntegrationTest()
    {
        super("sikrede");
    }

    @Test
    public void testCanImportAFileSetPlacedInTheInbox() throws Exception
    {
        File fileSet = getDirectory("data/sikrede/1");

        placeInInbox(fileSet, true);

        assertThat(isLocked(), is(false));

        assertRecordCount("Sikrede", 1);
    }
}
