
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
