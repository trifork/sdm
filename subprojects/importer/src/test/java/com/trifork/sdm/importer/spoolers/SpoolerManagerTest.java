package com.trifork.sdm.importer.spoolers;


import org.junit.After;
import org.junit.Test;


public class SpoolerManagerTest
{
	private static final String TMP_FILE_SPOOLER_MANAGER = "/tmp/FileSpoolerManager";


	@Test
	public void testInit()
	{
		/*
		SpoolerManager fsm = new SpoolerManager(TMP_FILE_SPOOLER_MANAGER);
		FileSpooler spooler = fsm.spoolers.get("takst");

		assertNotNull(spooler);
		*/
	}


	@Test
	public void testUri2filepath()
	{
		/*
		String uri = "file:///testdir/testfile";
		String filepath = "/testdir/testfile";
		assertEquals(filepath, SpoolerManager.uri2filepath(uri));

		uri = "ftp:///testdir/testfile";
		assertNull(SpoolerManager.uri2filepath(uri));

		uri = "/testdir/testfile";
		assertNull(SpoolerManager.uri2filepath(uri));

		uri = ":¡@$£@½$";
		assertNull(SpoolerManager.uri2filepath(uri));
		*/
	}


	@Test
	public void testAreAllSpoolersRunning()
	{
		/*
		SpoolerManager fsm = new SpoolerManager(TMP_FILE_SPOOLER_MANAGER);
		fsm.spoolers = new HashMap<String, FileSpooler>();

		// Add a mocked running spooler
		FileSpooler mock1 = mock(FileSpooler.class);
		when(mock1.getStatus()).thenReturn(FileSpooler.Status.RUNNING);
		fsm.spoolers.put("takst", mock1);

		assertTrue(fsm.isAllSpoolersRunning());

		JobSpooler mock2 = mock(JobSpooler.class);
		when(mock2.getStatus()).thenReturn(JobSpooler.Status.RUNNING);
		fsm.jobSpoolers.put("navnebeskyttelse", mock2);

		assertTrue(fsm.isAllSpoolersRunning());

		// And a spooler that is not running.

		when(mock1.getStatus()).thenReturn(FileSpooler.Status.ERROR);
		fsm.spoolers.put("test2", mock1);
		assertFalse(fsm.isAllSpoolersRunning());
		*/
	}


	@After
	public void cleanUpfiles()
	{
		//FileSpoolerImplTest.deleteFile(new File(TMP_FILE_SPOOLER_MANAGER));
	}

}
