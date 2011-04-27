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
