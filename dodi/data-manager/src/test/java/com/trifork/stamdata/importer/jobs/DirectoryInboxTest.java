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
package com.trifork.stamdata.importer.jobs;

import com.google.common.base.Strings;
import com.trifork.stamdata.importer.parsers.Parser;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class DirectoryInboxTest
{
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    private final int stabilizationPeriod = 500;

    private final String parserId = "foo";

    private File parserInboxDir;
    private DirectoryInbox inbox;

    @Before
    public void setUp() throws Exception
    {
        Parser parser = Mockito.mock(Parser.class);
        when(parser.identifier()).thenReturn(parserId);

        parserInboxDir = folder.newFolder(parserId);
        inbox = new DirectoryInbox(folder.getRoot().getAbsolutePath(), parser, stabilizationPeriod);
    }

    @Test
    public void testUpdateShouldNotIncludeADirectoryWhileUnstable() throws Exception
    {
        placeInboxEntry("123");
        inbox.update();
        assertThat(inbox.peek(), is(nullValue(File.class)));
    }

    @Test
    public void testPeekShouldNotRemoveTheEntryFromTheInbox() throws Exception
    {
        File entry = placeInboxEntry("123");
        inbox.update();
        waitForStabilizationPeriodToPass();
        inbox.update();

        assertThat(inbox.peek(), is(entry));
        assertThat(inbox.peek(), is(entry));
    }

    @Test
    public void testProgressShouldProgressToTheNextEntry() throws Exception
    {
        File entry1 = placeInboxEntry("123");
        File entry2 = placeInboxEntry("234");
        inbox.update();
        waitForStabilizationPeriodToPass();
        inbox.update();

        assertThat(inbox.peek(), is(entry1));
        inbox.progress();

        assertThat(inbox.peek(), is(entry2));
        inbox.progress();

        assertThat(inbox.peek(), is(nullValue()));
    }

    @Test
    public void testInboxEntriesAreDeletedOnceAfterProgressIsCalled() throws Exception
    {
        placeInboxEntry("123");
        inbox.update();
        waitForStabilizationPeriodToPass();
        inbox.update();

        inbox.progress();
        assertThat(anEntryNamed("123").exists(), is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalStateExceptionIsThrownIfProgressIsCalledOnAnEmptyInbox() throws IOException
    {
        inbox.progress();
    }

    @Test
    public void testOnlyStableEntriesShouldBeReturnedReturnedByPeek() throws Exception
    {
        placeInboxEntry("123");
        inbox.update();
        assertThat(inbox.peek(), is(nullValue()));

        waitForStabilizationPeriodToPass();
        inbox.update();
        assertThat(inbox.peek(), is(notNullValue(File.class)));
    }

    @Test
    public void testSizeShouldOnlyReturnTheNumberOfItemsReadyForImportFromTheTopMostItemAndDownUntilTheFirstUnstable() throws Exception
    {
        placeInboxEntry("123");
        placeInboxEntry("234");

        // Make the inbox notice the two new entries.
        // Since it is the first time the inbox sees
        // them, it cannot know it they are stable.
        // They therefore do not count in the size
        // calculation.
        //
        inbox.update();
        assertThat(inbox.size(), is(0));

        // Add a new entry. This will be unstable
        // since update() has not yet been called
        // while the entry is present.
        //
        placeInboxEntry("456");

        // Allow the stabilization period to pass.
        // This will only stabilize the '123' and '234'.
        //
        waitForStabilizationPeriodToPass();

        // Now we add another element in-between the
        // others (lexicographically). Even now that
        // '456' is stable it will not count since,
        // only 'ready' (top-most stable) entries count.
        //
        placeInboxEntry("345");

        // Now entries '123' and '234' are stable.
        // Entry '456' on the other hand is not
        // since the inbox has not seen it before.
        //
        inbox.update();
        assertThat(inbox.size(), is(2));

        // Allow the '456' entry to stabilize.
        //
        waitForStabilizationPeriodToPass();

        // Now all entries are stable.
        //
        inbox.update();
        assertThat(inbox.size(), is(4));
    }

    @Test
    public void testAnEntryIsNotConsideredStableIfItsContentChanges() throws Exception
    {
        placeInboxEntry("345");

        createFile("345/1.txt", 10);
        inbox.update();

        createFile("345/1.txt", 11);
        waitForStabilizationPeriodToPass();
        inbox.update();

        assertThat(inbox.size(), is(0));

        createFile("345/1.txt", 10);
        waitForStabilizationPeriodToPass();
        inbox.update();

        assertThat(inbox.size(), is(0));
    }

    //
    // Helpers
    //

    private File createSubDirectory(String name)
    {
        File file = new File(parserInboxDir.getPath() + File.separatorChar + name);
        if (!file.exists()) folder.newFolder(parserInboxDir.getName() + File.separatorChar + name);
        return file;
    }

    private File createFile(String name, int size) throws Exception
    {
        File file = folder.newFile(parserInboxDir.getName()+ "/" + name);
        FileUtils.write(file, Strings.repeat("X", size));
        return file;
    }

    private File placeInboxEntry(String entry) throws Exception
    {
        String someFilename = entry + "/1.txt";
        File dir = createSubDirectory(entry);
        createFile(someFilename, 10);

        return dir;
    }

    private void waitForStabilizationPeriodToPass() throws InterruptedException
    {
        // We wait twice as long, so the test is less likely to fail.
        //
        Thread.sleep(stabilizationPeriod * 2);
    }

    private File anEntryNamed(String name)
    {
        return new File(parserInboxDir, name);
    }
}
