// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package com.trifork.stamdata.client;

import java.util.Iterator;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.trifork.stamdata.replication.replication.views.cpr.Person;

@Ignore
public class RegistryClientTest {

	private RegistryClient client;

	@Before
	public void setUp() {
		client = new RegistryClient("http://localhost:8080/replication/stamdata/", false);
	}

	@Test
	public void should_extract_complete_dataset() throws Exception {

		Iterator<EntityRevision<Person>> revisions = client.update(Person.class, null, 5000);

		int recordCount = 0;

		StopWatch timer = new StopWatch();
		timer.start();

		while (revisions.hasNext()) {
			recordCount++;
			EntityRevision<Person> revision = revisions.next();
			printRevision(revision);
		}

		timer.stop();

		printStatistics(recordCount, timer);
	}

	@Test
	public void should_extract_delta_dataset() throws Exception {

		Iterator<EntityRevision<Person>> revisions = client.update(Person.class, "13002373210000092000225", 5000);

		int recordCount = 0;

		StopWatch timer = new StopWatch();
		timer.start();

		while (revisions.hasNext()) {
			recordCount++;
			EntityRevision<Person> revision = revisions.next();
			printRevision(revision);
		}

		timer.stop();

		printStatistics(recordCount, timer);
	}

	protected void printRevision(EntityRevision<?> revision) {
		System.out.println(revision.getId() + ": " + revision.getEntity());
	}

	protected void printStatistics(int i, StopWatch timer) {
		System.out.println();
		System.out.println("Time used: " + timer.getTime() / 1000. + " sec.");
		System.out.println("Record count: " + i);
	}
}
