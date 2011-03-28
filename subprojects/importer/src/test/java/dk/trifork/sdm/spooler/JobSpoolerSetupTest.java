package dk.trifork.sdm.spooler;

import static junit.framework.Assert.assertEquals;

import java.io.InputStream;

import org.junit.Test;

import dk.trifork.sdm.config.Configuration;
import dk.trifork.sdm.jobspooler.Job;


/**
 * FileSpoolerTest. Tests that setup of files and file sets are handled correct.
 * 
 * @author Jan Buchholdt
 */
public class JobSpoolerSetupTest implements Job {

	@Test
	public void runSetupTest() throws Exception {

		InputStream configuration = getClass().getClassLoader().getResourceAsStream("testJobSpoolerconfig.properties");
		
		Configuration.setDefaultInstance(new Configuration(configuration));
		JobSpoolerSetup setup = new JobSpoolerSetup("testjobspooler");
		
		assertEquals(this.getClass().getName(), setup.getJobExecutorClass().getName());
		assertEquals("* 1 * * *", setup.getSchedule());
		
		Configuration.setDefaultInstance(new Configuration());
	}

	@Override
	public void run() {

		// Dummy
	}
}
