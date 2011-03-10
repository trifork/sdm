package com.trifork.sdm.replication.gateway;


import static com.trifork.sdm.replication.gateway.SOSITestConstants.*;
import static dk.sosi.seal.model.constants.DGWSConstants.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.number.IsCloseTo.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.StringWriter;
import java.net.URL;
import java.util.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.inject.Key;
import com.trifork.sdm.replication.GuiceTest;
import com.trifork.sdm.replication.admin.models.PermissionRepository;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.*;


public class RequestProcessorTest extends GuiceTest
{
	private static final int SOAP_OK_STATUS = 200;
	private static final int SOAP_FAULT_STATUS = 500;
	
	private static JAXBContext jaxbContext;

	protected static IDCard idCard;
	protected static SOSIFactory factory;

	protected RequestProcessor processor;

	protected static Request request;
	protected AuthorizationRequestStructure requestBody;

	protected String httpMethod;
	protected String clientCVR;

	protected Reply response;
	protected AuthorizationResponseStructure responseBody;
	protected String requestedEntityURI;

	public RequestProcessorTest() throws JAXBException
	{
		super();
		
		jaxbContext = JAXBContext.newInstance(AuthorizationRequestStructure.class, AuthorizationResponseStructure.class);
	}

	@Override
	protected void configure()
	{
		bind(PermissionRepository.class).toInstance(mock(PermissionRepository.class));

		install(new TestSTSModule());
	}


	@Before
	public void setUp() throws Exception
	{
		// Get the unit under test.

		processor = getInjector().getInstance(Key.get(RequestProcessor.class));

		// Create a new request.

		factory = getInjector().getInstance(SOSIFactory.class);
		idCard = getInjector().getInstance(IDCard.class);

		request = factory.createNewRequest(false, null);
		request.setIDCard(idCard);

		// Authorized the user for a resource.

		requestedEntityURI = "Apotek";

		authorizeAccessToEntity(TEST_CVR, requestedEntityURI);

		// Set the default request parameters.

		httpMethod = "POST";
		clientCVR = TEST_CVR;

		// Construct a valid request body.

		requestBody = createValidRequestBody();
	}


	@After
	public void tearDown() throws Exception
	{
		// Reset the response.

		response = null;
		responseBody = null;

		// Deny access again.

		deauthorizeAccessToEntity(clientCVR, requestedEntityURI);
	}


	@Test
	public void should_return_a_DGWS_1_0_1_response() throws Exception
	{
		sendRequest();

		assertThat(response.getDGWSVersion(), is(VERSION_1_0_1));
	}


	@Test
	public void should_return_SOAP_OK_on_valid_requests() throws Exception
	{
		sendRequest();

		assertNotSoapFault();
	}


	@Test
	public void should_return_SOAP_content_type() throws Exception
	{
		sendRequest();

		assertThat(processor.getContentType(), is("application/soap+xml; charset=UTF-8"));
	}


	@Test
	public void should_return_SOAP_fault_for_HTTP_GET() throws Exception
	{
		httpMethod = "GET";

		sendRequest();

		assertSoapFault();
	}


	@Test
	public void should_return_SOAP_fault_for_HTTP_HEAD() throws Exception
	{
		httpMethod = "HEAD";

		sendRequest();

		assertSoapFault();
	}


	@Test
	public void should_return_SOAP_fault_for_HTTP_PUT() throws Exception
	{
		httpMethod = "PUT";

		sendRequest();

		assertSoapFault();
	}


	@Test
	public void should_return_SOAP_fault_for_unknown_resource() throws Exception
	{
		requestBody.entityURI = RandomStringUtils.randomAscii(10);

		sendRequest();

		assertSoapFault();
	}


	@Test
	public void should_return_a_expires_date_that_matches_the_time_to_live() throws Exception
	{
		sendRequest();
		
		long expectedExpires = request.getIDCard().getExpiryDate().getTime() / 1000;
		long errorMargin = 1;

		double authorizationExpiration = Long.parseLong(responseBody.authorization.split(":")[0]);
		
		assertThat(authorizationExpiration, is(closeTo(expectedExpires, errorMargin)));
	}


	//
	// Assertions
	//

	protected void assertSoapFault()
	{
		assertThat(processor.getResponseCode(), is(SOAP_FAULT_STATUS));
		assertTrue(response.isFault());
	}


	protected void assertNotSoapFault()
	{
		assertThat(processor.getResponseCode(), is(SOAP_OK_STATUS));
		assertFalse(response.isFault());
	}


	//
	// Helper Methods
	//

	protected void sendRequest() throws Exception
	{
		DocumentBuilderFactory documentBuilder = DocumentBuilderFactory.newInstance();
		documentBuilder.setNamespaceAware(true);
		Document doc = documentBuilder.newDocumentBuilder().newDocument();
		jaxbContext.createMarshaller().marshal(requestBody, doc);
		request.setBody(doc.getDocumentElement());
		String xml = requestToString(request);
		
		processor.process(xml, clientCVR, httpMethod);
		
		response = factory.deserializeReply(processor.getResponse());

		Element element = response.getBody();
		if (element != null)
		{
			responseBody = jaxbContext.createUnmarshaller().unmarshal(element, AuthorizationResponseStructure.class).getValue();
		}
	}


	protected Map<String, String> getUrlParams(URL url)
	{
		Map<String, String> params = new HashMap<String, String>();

		StringTokenizer tokenizer = new StringTokenizer(url.getQuery(), "&");
		while (tokenizer.hasMoreTokens())
		{
			String[] param = tokenizer.nextToken().split("=");

			params.put(param[0], param[1]);
		}

		return params;
	}

	private AuthorizationRequestStructure createValidRequestBody()
	{
		AuthorizationRequestStructure requestBody = new AuthorizationRequestStructure();

		requestBody.entityURI = requestedEntityURI;

		return requestBody;
	}


	private String requestToString(Request request) throws Exception
	{
		StringWriter writer = new StringWriter();

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new DOMSource(request.serialize2DOMDocument()), new StreamResult(writer));

		return writer.toString();
	}


	private void authorizeAccessToEntity(String userCVR, String entityID) throws Exception
	{
		PermissionRepository repo = getInjector().getInstance(PermissionRepository.class);
		when(repo.canAccessEntity(userCVR, entityID)).thenReturn(true);
	}


	private void deauthorizeAccessToEntity(String userCVR, String entityID) throws Exception
	{
		PermissionRepository repo = getInjector().getInstance(PermissionRepository.class);
		when(repo.canAccessEntity(userCVR, entityID)).thenReturn(false);
	}
}
