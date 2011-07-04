package com.trifork.stamdata.authorization;

import static com.google.common.collect.Lists.newArrayList;
import static com.trifork.stamdata.authorization.SOSITestConstants.TEST_CVR;
import static com.trifork.stamdata.authorization.SOSITestConstants.TEST_IT_SYSTEM_NAME;
import static com.trifork.stamdata.authorization.SOSITestConstants.TEST_STS_URL;
import static dk.sosi.seal.model.AuthenticationLevel.VOCES_TRUSTED_SYSTEM;
import static dk.sosi.seal.model.constants.SubjectIdentifierTypeValues.CVR_NUMBER;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.google.common.collect.ImmutableSet;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.CareProvider;
import dk.sosi.seal.model.IDCard;
import dk.sosi.seal.model.Reply;
import dk.sosi.seal.model.Request;
import dk.sosi.seal.model.SecurityTokenRequest;
import dk.sosi.seal.model.SecurityTokenResponse;
import dk.sosi.seal.model.SignatureUtil;
import dk.sosi.seal.pki.InMemoryIntermediateCertificateCache;
import dk.sosi.seal.pki.SOSITestFederation;
import dk.sosi.seal.vault.ClasspathCredentialVault;


@RunWith(MockitoJUnitRunner.class)
public class RequestProcessorIntegrationTest {

	private static IDCard idCard;
	private static SOSIFactory factory;
	private static JAXBContext context;

	@Mock
	AuthorizationDao authorizationDao;

	@BeforeClass
	public static void fetchIDCard() throws Exception {

		context = JAXBContext.newInstance(AuthorizationRequestStructure.class, AuthorizationResponseStructure.class);

		Properties sosiProps = SignatureUtil.setupCryptoProviderForJVM();
		SOSITestFederation federation = new SOSITestFederation(sosiProps, new InMemoryIntermediateCertificateCache());
		factory = new SOSIFactory(federation, new ClasspathCredentialVault(sosiProps, SOSITestConstants.KEY_STORE_PASSWORD), sosiProps);

		// Create a SEAL ID card.

		CareProvider careProvider = new CareProvider(CVR_NUMBER, TEST_CVR, "dk");
		IDCard unsignedCard = factory.createNewSystemIDCard(TEST_IT_SYSTEM_NAME, careProvider, VOCES_TRUSTED_SYSTEM, null, null, factory.getCredentialVault().getSystemCredentialPair().getCertificate(), null);

		SecurityTokenRequest stsRequest = factory.createNewSecurityTokenRequest();
		stsRequest.setIDCard(unsignedCard);

		// Send the request.

		String responseXML = send(TEST_STS_URL, stsRequest.serialize2DOMDocument());
		SecurityTokenResponse response = factory.deserializeSecurityTokenResponse(responseXML);

		// Check for errors.

		if (response.isFault()) {
			throw new Exception(format("STS Error: %s %s", response.getFaultCode(), response.getFaultString()));
		}

		// If all has gone well,
		// we can store the ID Card for later use.

		idCard = response.getIDCard();
	}

	@Test
	public void should_allow_authorized_requests_with_zero_assosiated_authorizations() throws Exception {

		Request request = factory.createNewRequest(false, null);
		request.setIDCard(idCard);

		AuthorizationRequestStructure authorizationRequest = new AuthorizationRequestStructure();
		authorizationRequest.cpr = "1234567890";

		Document replyXML = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		context.createMarshaller().marshal(authorizationRequest, replyXML);

		request.setBody(replyXML.getDocumentElement());

		RequestProcessor processor = new RequestProcessor(factory, ImmutableSet.of(TEST_CVR), context.createMarshaller(), context.createUnmarshaller(), authorizationDao);

		Reply reply = processor.process(request);

		assertFalse(reply.isFault());

		AuthorizationResponseStructure authorizationResponse = (AuthorizationResponseStructure) context.createUnmarshaller().unmarshal(reply.getBody());

		assertThat(authorizationResponse.cpr, equalTo(authorizationRequest.cpr));
		assertThat(authorizationResponse.authorizations, nullValue());
	}

	@Test
	public void should_return_multiple_assosiated_authorizations() throws Exception {

		Request request = factory.createNewRequest(false, null);
		request.setIDCard(idCard);

		AuthorizationRequestStructure authorizationRequest = new AuthorizationRequestStructure();
		authorizationRequest.cpr = "1234567890";

		when(authorizationDao.getAuthorizations("1234567890")).thenReturn(newArrayList(new Authorization("1234567890", "Anders", "Petersen", "12345", "1234"), new Authorization("1234567890", "Anders", "Petersen", "23456", "5151")));

		Document replyXML = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		context.createMarshaller().marshal(authorizationRequest, replyXML);

		request.setBody(replyXML.getDocumentElement());

		RequestProcessor processor = new RequestProcessor(factory, ImmutableSet.of(TEST_CVR), context.createMarshaller(), context.createUnmarshaller(), authorizationDao);

		Reply reply = processor.process(request);

		printXML(reply.getBody());

		AuthorizationResponseStructure authorizationResponse = (AuthorizationResponseStructure) context.createUnmarshaller().unmarshal(reply.getBody());

		assertThat(authorizationResponse.firstName, equalTo("Anders"));
		assertThat(authorizationResponse.lastName, equalTo("Petersen"));
		assertThat(authorizationResponse.cpr, equalTo("1234567890"));
		assertThat(authorizationResponse.authorizations.size(), equalTo(2));
		assertThat(authorizationResponse.authorizations.get(0).authorizationCode, equalTo("12345"));
		assertThat(authorizationResponse.authorizations.get(0).educationCode, equalTo("1234"));
		assertThat(authorizationResponse.authorizations.get(1).authorizationCode, equalTo("23456"));
		assertThat(authorizationResponse.authorizations.get(1).educationCode, equalTo("5151"));
		assertThat(authorizationResponse.authorizations.get(1).educationName, equalTo("Fysioterapeut"));
	}

	public void printXML(Element doc) throws TransformerException {

		// Set up the transformer to write the output string
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		transformer.setOutputProperty("indent", "yes");
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);

		DOMSource source = new DOMSource(doc);

		// Do the transformation and output
		transformer.transform(source, result);
		System.out.println(sw.toString());
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

		if (connection.getResponseCode() < 400) {
			return streamToString(connection.getInputStream());
		}
		else {
			throw new Exception(streamToString(connection.getErrorStream()));
		}
	}

	public static String streamToString(InputStream in) {
		return new Scanner(in).useDelimiter("\\A").next();
	}
}
