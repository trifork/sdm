package dk.trifork.sdm.importer;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;


public class ImportTimeManagerTest {

	@Test
	public void test() {

		Calendar now = Calendar.getInstance();
		now.set(Calendar.MILLISECOND, 0);

		ImportTimeManager.setImportTime("testSpooler", now);
		assertEquals(now.getTimeInMillis(), ImportTimeManager.getLastImportTime("testSpooler").getTimeInMillis());
	}
}
