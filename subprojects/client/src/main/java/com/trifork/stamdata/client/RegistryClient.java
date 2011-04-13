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

import static com.trifork.stamdata.client.SOSITestConstants.TEST_CVR;
import static com.trifork.stamdata.client.SOSITestConstants.TEST_IT_SYSTEM_NAME;
import static com.trifork.stamdata.client.SOSITestConstants.TEST_STS_URL;
import static dk.sosi.seal.model.AuthenticationLevel.VOCES_TRUSTED_SYSTEM;
import static dk.sosi.seal.model.constants.SubjectIdentifierTypeValues.CVR_NUMBER;
import static java.lang.String.format;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.time.StopWatch;
import org.w3c.dom.Document;

import com.trifork.stamdata.client.impl.ReplicationReaderImpl;
import com.trifork.stamdata.replication.replication.annotations.ViewPath;
import com.trifork.stamdata.replication.security.dgws.AuthorizationRequestStructure;
import com.trifork.stamdata.replication.security.dgws.AuthorizationResponseStructure;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.CareProvider;
import dk.sosi.seal.model.IDCard;
import dk.sosi.seal.model.Reply;
import dk.sosi.seal.model.Request;
import dk.sosi.seal.model.SecurityTokenRequest;
import dk.sosi.seal.model.SecurityTokenResponse;
import dk.sosi.seal.model.SignatureUtil;
import dk.sosi.seal.pki.Federation;
import dk.sosi.seal.pki.SOSITestFederation;
import dk.sosi.seal.vault.ClasspathCredentialVault;
import dk.sosi.seal.vault.CredentialVault;


public class RegistryClient {

	private final String stamdataURL;
	private final boolean securityEnabled;
	private SOSIFactory sosiFactory;
	private IDCard idCard;
	private Map<Class<?>, String> authorizationCache;

	public RegistryClient(String endpointURL, boolean sosiSecurityEnabled) {
		this.stamdataURL = endpointURL;
		this.securityEnabled = sosiSecurityEnabled;

		if (sosiSecurityEnabled) {
			createSosiFactory();
		}
	}

	private void createSosiFactory() {
		Properties cryptoProviderSettings = SignatureUtil.setupCryptoProviderForJVM();

		// Open the key store using the path and password.
		CredentialVault vault = new ClasspathCredentialVault(cryptoProviderSettings, "Test1234");
		Federation federation = new SOSITestFederation(cryptoProviderSettings);
		sosiFactory = new SOSIFactory(federation, vault, cryptoProviderSettings);
	}

	public <T> Iterator<EntityRevision<T>> update(Class<T> entityType, String offset, int count) throws Exception {
		if (offset == null) {
			offset = "0";
		}

		String authorizationToken = validAuthorizationTokenFor(entityType);
		URL feedURL = new URL(stamdataURL + createPathFromURI(entityNameFor(entityType)));
		ReplicationReader reader = new ReplicationReaderImpl(authorizationToken, feedURL, offset, count);
		return new ReplicationIterator<T>(entityType, reader);
	}

	public <T>void updateAndPrintStatistics(Class<T> entityType, String offset, int count) throws Exception {
		Iterator<EntityRevision<T>> revisions = update(entityType, offset, count);

		int recordCount = 0;

		StopWatch timer = new StopWatch();
		timer.start();

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

	private <T> String validAuthorizationTokenFor(Class<T> entityType) throws Exception {
		if (!securityEnabled) {
			return "";
		}

		// REQUEST STS IDCARD
		//
		// TODO: Do this whenever it expires.

		Request request = sosiFactory.createNewRequest(false, null);
		idCard = fetchIDCard();
		request.setIDCard(idCard);

		// REQUEST STAMDATA AUTHORIZATION TOKEN

		String viewURI = createStamdataURI(entityNameFor(entityType)).toString();
		AuthorizationRequestStructure authorizationRequest = new AuthorizationRequestStructure(viewURI);

		// CONVERT THE AUTHORIZATION TO XML

		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		JAXBContext jaxbContext = JAXBContext.newInstance(AuthorizationRequestStructure.class, AuthorizationResponseStructure.class);
		Marshaller marshaller = jaxbContext.createMarshaller();

		marshaller.marshal(authorizationRequest, doc);
		request.setBody(doc.getDocumentElement());

		String responseXML = SoapHelper.send(stamdataURL + "authenticate", request.serialize2DOMDocument());

		// PARSE THE RESPONSE
		//
		// TODO: We need some error handling here.
		// For instance we need to throw an exception if
		// the client is not authorized.

		Reply response = sosiFactory.deserializeReply(responseXML);

		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		AuthorizationResponseStructure authorizationResponse = unmarshaller.unmarshal(response.getBody(), AuthorizationResponseStructure.class).getValue();

		// CACHE THE AUTHORIZATION
		//
		// TODO: This cache is not actually used at the moment.

		String result = authorizationResponse.authorization;
		authorizationCache = new HashMap<Class<?>, String>();
		authorizationCache.put(entityType, result);
		return result;
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

	private URI createStamdataURI(String entityName) throws URISyntaxException {

		return new URI("stamdata://" + entityName);
	}

	private IDCard fetchIDCard() throws Exception {

		// Create a SEAL ID card.
		//
		// TODO: Make these settings settable.

		CareProvider cvrCareProvider = new CareProvider(CVR_NUMBER, TEST_CVR, "dk");

		IDCard unsignedCard = sosiFactory.createNewSystemIDCard(TEST_IT_SYSTEM_NAME, cvrCareProvider, VOCES_TRUSTED_SYSTEM, null, // Username
				null, // Password
				sosiFactory.getCredentialVault().getSystemCredentialPair().getCertificate(), null // Alternative
																								// Name
				);

		SecurityTokenRequest stsRequest = sosiFactory.createNewSecurityTokenRequest();
		stsRequest.setIDCard(unsignedCard);

		// Send the request.

		String responseXML = SoapHelper.send(TEST_STS_URL, stsRequest.serialize2DOMDocument());
		SecurityTokenResponse response = sosiFactory.deserializeSecurityTokenResponse(responseXML);

		// Check for errors.

		if (response.isFault()) {
			throw new Exception(format("STS Error: %s %s", response.getFaultCode(), response.getFaultString()));
		}

		// If all has gone well,
		// we can store the ID Card for later use.

		return response.getIDCard();
	}
}
