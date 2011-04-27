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

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.trifork.stamdata.replication.replication.views.cpr.Person;

@Ignore
public class RegistryClientTest {

	private RegistryClient client;

	@Before
	public void setUp() {
		client = new RegistryClient("http://localhost:8080/replication/stamdata/", Security.ssl);
	}

	@Test
	public void should_extract_complete_dataset() throws Exception {
		client.updateAndPrintStatistics(Person.class, null, 5000);
	}

	@Test
	public void should_extract_delta_dataset() throws Exception {
		client.updateAndPrintStatistics(Person.class, "13026992710000000010", 5000);
	}
}
