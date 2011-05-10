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

package com.trifork.stamdata.client.security;

import static com.trifork.stamdata.client.security.dgws.SOSITestConstants.TEST_CVR;
import static com.trifork.stamdata.client.security.dgws.SOSITestConstants.TEST_IT_SYSTEM_NAME;
import static com.trifork.stamdata.client.security.dgws.SOSITestConstants.TEST_STS_URL;
import static dk.sosi.seal.model.AuthenticationLevel.VOCES_TRUSTED_SYSTEM;
import static dk.sosi.seal.model.constants.SubjectIdentifierTypeValues.CVR_NUMBER;
import static java.lang.String.format;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.trifork.stamdata.client.security.dgws.AuthorizationRequestStructure;
import com.trifork.stamdata.client.security.dgws.AuthorizationResponseStructure;
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

public class DgwsSecurityHandler implements SecurityHandler {
	private final String stamdataUrl;
	private final SOSIFactory sosiFactory;
	private final Map<Class<?>, String> authorizationCache = new HashMap<Class<?>, String>();
	private IDCard idCard;

	public DgwsSecurityHandler(String stamdataUrl) {
		this.stamdataUrl = stamdataUrl;
		sosiFactory = createSosiFactory();
	}
	
	@Override
	public <T> String validAuthorizationTokenFor(Class<T> entityType) throws Exception {
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

		String responseXML = send(stamdataUrl + "authenticate", request.serialize2DOMDocument());

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
		authorizationCache.put(entityType, result);
		return result;
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

		String responseXML = send(TEST_STS_URL, stsRequest.serialize2DOMDocument());
		SecurityTokenResponse response = sosiFactory.deserializeSecurityTokenResponse(responseXML);

		// Check for errors.

		if (response.isFault()) {
			throw new Exception(format("STS Error: %s %s", response.getFaultCode(), response.getFaultString()));
		}

		// If all has gone well,
		// we can store the ID Card for later use.

		return response.getIDCard();
	}

	private URI createStamdataURI(String entityName) throws URISyntaxException {
		return new URI("stamdata://" + entityName);
	}

	private <T> String entityNameFor(Class<T> entityType) {
		return entityType.getAnnotation(ViewPath.class).value();
	}
	
	private SOSIFactory createSosiFactory() {
		Properties cryptoProviderSettings = SignatureUtil.setupCryptoProviderForJVM();

		// Open the key store using the path and password.
		CredentialVault vault = new ClasspathCredentialVault(cryptoProviderSettings, "Test1234");
		Federation federation = new SOSITestFederation(cryptoProviderSettings);
		return new SOSIFactory(federation, vault, cryptoProviderSettings);
	}

	public static String send(String urlString, Node node) throws Exception {
		URL url = new URL(urlString);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// Prepare for SOAP

		connection.setRequestMethod("POST");
		connection.setRequestProperty("SOAPAction", "\"\"");
		connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8;");

		// Send the request XML.

		connection.setDoOutput(true);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new DOMSource(node), new StreamResult(connection.getOutputStream()));

		// Read the response.

		InputStream inputStream;

		if (connection.getResponseCode() < 400) {
			inputStream = connection.getInputStream();
		}
		else {
			inputStream = connection.getErrorStream();
		}

		String response = IOUtils.toString(inputStream);

		return response;
	}
}
