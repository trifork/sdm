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

import static com.trifork.stamdata.client.SOSITestConstants.TEST_CVR;
import static com.trifork.stamdata.client.SOSITestConstants.TEST_IT_SYSTEM_NAME;
import static com.trifork.stamdata.client.SOSITestConstants.TEST_STS_URL;
import static dk.sosi.seal.model.AuthenticationLevel.VOCES_TRUSTED_SYSTEM;
import static dk.sosi.seal.model.constants.SubjectIdentifierTypeValues.CVR_NUMBER;
import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.time.StopWatch;
import org.w3c.dom.Document;

import com.trifork.stamdata.client.impl.ReplicationReaderImpl;
import com.trifork.stamdata.views.ViewPath;

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
	private final Security security;
	private SOSIFactory sosiFactory;
	private IDCard idCard;
	private Map<Class<?>, String> authorizationCache;

	public RegistryClient(String endpointURL, Security security) {
		this.stamdataURL = endpointURL;
		this.security = security;

		if (security == Security.dgws) {
			createSosiFactory();
		} else if (security == Security.ssl) {
			setupSslCertificates();
		}
	}

	private void createSosiFactory() {
		Properties cryptoProviderSettings = SignatureUtil.setupCryptoProviderForJVM();

		// Open the key store using the path and password.
		CredentialVault vault = new ClasspathCredentialVault(cryptoProviderSettings, "Test1234");
		Federation federation = new SOSITestFederation(cryptoProviderSettings);
		sosiFactory = new SOSIFactory(federation, vault, cryptoProviderSettings);
	}

	private void setupSslCertificates() {
		try {
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(createKeyStore("/truststore.jks", "Test1234"));
			TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(createKeyStore("/keystore.jks", "Test1234"), "Test1234".toCharArray());
			KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(keyManagers, trustManagers, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
		} catch (Exception e) {
			throw new RuntimeException("Could not set up certificates", e);
		}
	}

	private KeyStore createKeyStore(String path, String password) throws KeyStoreException, IOException,
			NoSuchAlgorithmException, CertificateException {
		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		InputStream keystoreStream = getClass().getResourceAsStream(path);
		keystore.load(keystoreStream, password.toCharArray());
		return keystore;
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
		if (security != Security.dgws) {
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
