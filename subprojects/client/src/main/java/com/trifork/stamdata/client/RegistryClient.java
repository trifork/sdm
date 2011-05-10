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

import java.net.URL;
import java.util.Iterator;

import org.apache.commons.lang.time.StopWatch;

import com.trifork.stamdata.client.impl.ReplicationReaderImpl;
import com.trifork.stamdata.client.security.DgwsSecurityHandler;
import com.trifork.stamdata.client.security.NoSecurityHandler;
import com.trifork.stamdata.client.security.SecurityHandler;
import com.trifork.stamdata.client.security.TwoWaySslSecurityHandler;
import com.trifork.stamdata.views.ViewPath;

public class RegistryClient {
	private final String stamdataURL;
	private final SecurityHandler securityHandler;

	public RegistryClient(String endpointURL, Security security) {
		this.stamdataURL = endpointURL;
		this.securityHandler = createSecurityHandler(endpointURL, security);
	}

	public <T> Iterator<EntityRevision<T>> update(Class<T> entityType, String offset, int count) throws Exception {
		if (offset == null) {
			offset = "0";
		}

		String authorizationToken = securityHandler.validAuthorizationTokenFor(entityType);
		URL feedURL = new URL(stamdataURL + createPathFromURI(entityNameFor(entityType)));
		ReplicationReader reader = new ReplicationReaderImpl(authorizationToken, feedURL, offset, count);
		return new ReplicationIterator<T>(entityType, reader);
	}

	public <T>void updateAndPrintStatistics(Class<T> entityType, String offset, int count) throws Exception {
		Iterator<EntityRevision<T>> revisions = update(entityType, offset, count);

		StopWatch timer = new StopWatch();
		timer.start();

		int recordCount = 0;
		while (revisions.hasNext()) {
			recordCount++;
			EntityRevision<T> revision = revisions.next();
			printRevision(revision);
		}

		timer.stop();

		printStatistics(recordCount, timer);
	}

	private static void printRevision(EntityRevision<?> revision) {
		System.out.println(revision.getId() + ": " + revision.getEntity());
	}

	private static void printStatistics(int i, StopWatch timer) {
		System.out.println();
		System.out.println("Time used: " + timer.getTime() / 1000. + " sec.");
		System.out.println("Record count: " + i);
	}

	private SecurityHandler createSecurityHandler(String endpointURL, Security security) {
		switch(security) {
		case dgws:
			return new DgwsSecurityHandler(endpointURL);
		case ssl:
			return new TwoWaySslSecurityHandler();
		case none:
			return new NoSecurityHandler();
		default:
			throw new IllegalArgumentException("Uknonwn security method: " + security);
		}
	}

	private <T> String entityNameFor(Class<T> entityType) {
		return entityType.getAnnotation(ViewPath.class).value();
	}

	private String createPathFromURI(String entityURI) {
		if (entityURI.contains(".")) {
			return entityURI.substring(11);
		}
		return entityURI;
	}
}
