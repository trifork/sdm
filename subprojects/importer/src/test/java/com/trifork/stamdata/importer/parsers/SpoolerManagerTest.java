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

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

import com.trifork.stamdata.importer.parsers.FileParserJob;
import com.trifork.stamdata.importer.parsers.Job;
import com.trifork.stamdata.importer.parsers.JobManager;

import java.io.File;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class SpoolerManagerTest
{
	private static final String TMP_FILE_SPOOLER_MANAGER = System.getProperty("java.io.tmpdir") + "/tmp/FileSpoolerManager";

	@After
	public void cleanUpfiles()
	{
		FileUtils.deleteQuietly(new File(TMP_FILE_SPOOLER_MANAGER));
	}

	@Test
	public void testInit()
	{
		JobManager manager = new JobManager(TMP_FILE_SPOOLER_MANAGER);
		FileParserJob spooler = manager.spoolers.get("takst");
		assertNotNull(spooler);
	}

	@Test
	public void testAreAllSpoolersRunning()
	{
		JobManager fsm = new JobManager(TMP_FILE_SPOOLER_MANAGER);
		fsm.spoolers = new HashMap<String, FileParserJob>();
		fsm.jobSpoolers = new HashMap<String, Job>();

		// Add a mocked running spooler
		FileParserJob mock1 = mock(FileParserJob.class);
		when(mock1.getState()).thenReturn(FileSpoolerImpl.FileParserJob.OK);
		fsm.spoolers.put("takst", mock1);

		assertTrue(fsm.isAllSpoolersRunning());

		Job mock2 = mock(Job.class);
		when(mock2.getState()).thenReturn(JobSpoolerImpl.Status.OK);
		fsm.jobSpoolers.put("navnebeskyttelse", mock2);

		assertTrue(fsm.isAllSpoolersRunning());

		// And a spooler that is not runnning
		when(mock1.getState()).thenReturn(FileSpoolerImpl.FileParserJob.ERROR);
		fsm.spoolers.put("test2", mock1);
		assertFalse(fsm.isAllSpoolersRunning());
	}
}
