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

package com.trifork.stamdata.importer.parsers;

import org.junit.After;
import org.junit.Test;

import com.trifork.stamdata.importer.parsers.FileSpoolerImpl;
import com.trifork.stamdata.importer.parsers.JobSpoolerImpl;
import com.trifork.stamdata.importer.parsers.SpoolerManager;

import java.io.File;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpoolerManagerTest {
    private static final String TMP_FILE_SPOOLER_MANAGER = System.getProperty("java.io.tmpdir") + "/tmp/FileSpoolerManager";

    @Test
    public void testInit() {
        SpoolerManager fsm = new SpoolerManager(TMP_FILE_SPOOLER_MANAGER);
        FileSpoolerImpl spooler = fsm.spoolers.get("takst");
        assertNotNull(spooler);
    }

    @Test
    public void testUri2filepath() {
        String uri = "file:///testdir/testfile";
        String filepath = "/testdir/testfile";
        assertEquals(filepath, SpoolerManager.uri2filepath(uri));

        uri = "ftp:///testdir/testfile";
        assertNull(SpoolerManager.uri2filepath(uri));

        uri = "/testdir/testfile";
        assertNull(SpoolerManager.uri2filepath(uri));

        uri = ":¡@$£@½$";
        assertNull(SpoolerManager.uri2filepath(uri));
    }

    @Test
    public void testAreAllSpoolersRunning() {
        SpoolerManager fsm = new SpoolerManager(TMP_FILE_SPOOLER_MANAGER);
        fsm.spoolers = new HashMap<String, FileSpoolerImpl>();
        fsm.jobSpoolers = new HashMap<String, JobSpoolerImpl>();

        // Add a mocked running spooler
        FileSpoolerImpl mock1 = mock(FileSpoolerImpl.class);
        when(mock1.getStatus()).thenReturn(FileSpoolerImpl.Status.RUNNING);
        fsm.spoolers.put("takst", mock1);

        assertTrue(fsm.isAllSpoolersRunning());

        JobSpoolerImpl mock2 = mock(JobSpoolerImpl.class);
        when(mock2.getStatus()).thenReturn(JobSpoolerImpl.Status.RUNNING);
        fsm.jobSpoolers.put("navnebeskyttelse", mock2);

        assertTrue(fsm.isAllSpoolersRunning());

        // And a spooler that is not runnning
        when(mock1.getStatus()).thenReturn(FileSpoolerImpl.Status.ERROR);
        fsm.spoolers.put("test2", mock1);
        assertFalse(fsm.isAllSpoolersRunning());
    }

    @After
    public void cleanUpfiles() {
        FileSpoolerImplTest.deleteFile(new File(TMP_FILE_SPOOLER_MANAGER));
    }

}
