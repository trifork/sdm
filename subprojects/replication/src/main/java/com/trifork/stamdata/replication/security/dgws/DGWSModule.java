
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

package com.trifork.stamdata.replication.security.dgws;

import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.replication.security.SecurityManager;

import dk.sdsd.nsp.slalog.ws.SLALoggingServletFilter;
import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.SignatureUtil;
import dk.sosi.seal.pki.InMemoryIntermediateCertificateCache;
import dk.sosi.seal.pki.SOSIFederation;
import dk.sosi.seal.vault.EmptyCredentialVault;


public class DGWSModule extends ServletModule {

	private JAXBContext jaxbContext;

	@Override
	protected void configureServlets() {

		// BIND THE SECURITY MANAGER
		//
		// The binding is required by the replication module.

		bind(SecurityManager.class).to(DGWSSecurityManager.class);

		// SERVE THE SOAP AUTHENTICATION SERVICE
		//
		// This servlet takes DGWS requests, authenticates and authorizes
		// them, and returns a replication token if authorized.

		serve("/stamdata/authenticate").with(AuthorizationServlet.class);
		
		// SLA LOGGING
		//
		// Enabled SLA Logging using the NSP Util API.
		// 
		// The SLALoggingServletFilter is made for SOAP services.
		// 
		// This filter is only used for the authentication service SOAP since,
		// the other parts of the service is REST.
		
		bind(SLALoggingServletFilter.class).in(Scopes.SINGLETON);
		filter("/stamdata/authenticate").through(SLALoggingServletFilter.class);

		// XML MARSHALLING
		//
		// The marshallers are used to marshal the SOAP
		// bodies to and from XML.
		//
		// @see AuthorizationRequestStructure
		// @see AuthorizationResponseStructure

		try {
			jaxbContext = JAXBContext.newInstance(AuthorizationRequestStructure.class, AuthorizationResponseStructure.class);
		}
		catch (JAXBException e) {
			addError(e);
		}
	}

	@Provides
	@Singleton
	protected SOSIFactory provideSOSIFactory() {

		// SETUP THE ENCRYPTION SETTINGS FOR SEAL
		//
		// Use SEAL's handy crypto provider selection algorithm
		// to figure out what crypto provider is best suited for
		// the current runtime environment.

		Properties encryption = SignatureUtil.setupCryptoProviderForJVM();

		SOSIFederation federation = new SOSIFederation(encryption, new InMemoryIntermediateCertificateCache());

		return new SOSIFactory(federation, new EmptyCredentialVault(), encryption);
	}

	@Provides
	protected Marshaller provideMarshaller() throws JAXBException {

		return jaxbContext.createMarshaller();
	}

	@Provides
	protected Unmarshaller provideUnmarshaller() throws JAXBException {

		return jaxbContext.createUnmarshaller();
	}
}
