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

import javax.persistence.Entity;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

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

	private SOSIFactory factory;
	private IDCard idCard;

	private final String stamdataURL;
	private Map<Class<?>, String> authorizationCache;

	public RegistryClient(String endpointURL) {

		// Determine which encryption provider to use.

		this.stamdataURL = endpointURL;
		Properties cryptoProviderSettings = SignatureUtil.setupCryptoProviderForJVM();

		// Open the key store using the path and password.

		CredentialVault vault = new ClasspathCredentialVault(cryptoProviderSettings, "Test1234");

		Federation federation = new SOSITestFederation(cryptoProviderSettings);
		factory = new SOSIFactory(federation, vault, cryptoProviderSettings);
	}

	public <T> Iterator<EntityRevision<T>> update(Class<T> entityType, String offset, int count) throws Exception {

		if (offset == null) offset = "0";

		// REQUEST STS IDCARD
		//
		// TODO: Do this whenever it expires.

		Request request = factory.createNewRequest(false, null);
		idCard = fetchIDCard();
		request.setIDCard(idCard);

		String entityName = entityType.getAnnotation(Entity.class).name();

		// REQUEST STAMDATA AUTHORIZATION TOKEN

		String viewURI = createStamdataURI(entityName).toString();
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

		Reply response = factory.deserializeReply(responseXML);

		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		AuthorizationResponseStructure authorizationResponse = unmarshaller.unmarshal(response.getBody(), AuthorizationResponseStructure.class).getValue();

		// CACHE THE AUTHORIZATION
		//
		// TODO: This cache is not actually used at the moment.

		authorizationCache = new HashMap<Class<?>, String>();
		authorizationCache.put(entityType, authorizationResponse.authorization);

		URL feedURL = new URL(stamdataURL + createPathFromURI(authorizationRequest.getViewURI()));

		return new ReplicationIterator<T>(entityType, authorizationResponse.authorization, feedURL, offset, count);
	}

	private String createPathFromURI(String entityURI) {

		return entityURI.substring(11);
	}

	private URI createStamdataURI(String entityName) throws URISyntaxException {

		return new URI("stamdata://" + entityName);
	}

	private IDCard fetchIDCard() throws Exception {

		// Create a SEAL ID card.
		//
		// TODO: Make these settings settable.

		CareProvider cvrCareProvider = new CareProvider(CVR_NUMBER, TEST_CVR, "dk");

		IDCard unsignedCard = factory.createNewSystemIDCard(TEST_IT_SYSTEM_NAME, cvrCareProvider, VOCES_TRUSTED_SYSTEM, null, // Username
				null, // Password
				factory.getCredentialVault().getSystemCredentialPair().getCertificate(), null // Alternative
																								// Name
				);

		SecurityTokenRequest stsRequest = factory.createNewSecurityTokenRequest();
		stsRequest.setIDCard(unsignedCard);

		// Send the request.

		String responseXML = SoapHelper.send(TEST_STS_URL, stsRequest.serialize2DOMDocument());
		SecurityTokenResponse response = factory.deserializeSecurityTokenResponse(responseXML);

		// Check for errors.

		if (response.isFault()) {
			throw new Exception(format("STS Error: %s %s", response.getFaultCode(), response.getFaultString()));
		}

		// If all has gone well,
		// we can store the ID Card for later use.

		return response.getIDCard();
	}
}
