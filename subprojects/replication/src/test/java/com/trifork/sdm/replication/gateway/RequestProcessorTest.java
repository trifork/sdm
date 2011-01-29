package com.trifork.sdm.replication.gateway;

import static com.trifork.sdm.replication.replication.URLParameters.*;
import static com.trifork.sdm.replication.sosi.SOSITestConstants.*;
import static dk.sosi.seal.model.constants.DGWSConstants.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.number.IsCloseTo.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.StringWriter;
import java.net.URL;
import java.util.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.Matcher;
import org.junit.*;
import org.w3c.dom.Element;

import com.google.inject.*;
import com.google.inject.util.Modules;
import com.trifork.sdm.replication.ProductionModule;
import com.trifork.sdm.replication.admin.models.PermissionRepository;
import com.trifork.sdm.replication.gateway.properties.*;
import com.trifork.sdm.replication.sosi.TestSTSModule;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.*;


public class RequestProcessorTest
{
	protected static Injector injector;

	private static final int SOAP_OK_STATUS = 200;
	private static final int SOAP_FAULT_STATUS = 500;

	protected static IDCard idCard;
	protected static SOSIFactory factory;

	protected RequestProcessor processor;

	protected static Request request;
	protected GatewayRequest requestBody;

	protected String httpMethod;
	protected String clientCVR;

	protected Reply response;
	protected GatewayResponse responseBody;
	protected String requestedEntity;


	@BeforeClass
	public static void init()
	{
		// Override the production setup with the setting we need.

		Module testModule = Modules.override(new ProductionModule()).with(new AbstractModule()
		{
			@Override
			public void configure()
			{
				// Mock the permission repository so we can control permissions.

				bind(PermissionRepository.class).toInstance(mock(PermissionRepository.class));

				// Use the test STS.

				install(new TestSTSModule());
			}
		});

		// Bind the Guice modules.

		injector = Guice.createInjector(testModule);

		factory = injector.getInstance(SOSIFactory.class);
		idCard = injector.getInstance(IDCard.class);
	}


	@Before
	public void setUp() throws Exception
	{
		// Get the unit under test.

		processor = injector.getInstance(Key.get(RequestProcessor.class, SOAP.class));

		// Create a new request.

		request = factory.createNewRequest(false, null);
		request.setIDCard(idCard);

		// Authorized the user for a resource.

		requestedEntity = "Apotek";

		authorizeAccessToEntity(TEST_CVR, requestedEntity);

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

		deauthorizeAccessToEntity(clientCVR, requestedEntity);
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
		requestBody.entity = RandomStringUtils.randomAscii(10);

		sendRequest();

		assertSoapFault();
	}


	@Test
	public void should_return_the_requested_format() throws Exception
	{
		requestBody.format = "FastInfoset";

		sendRequest();

		assertParam(FORMAT, is("FastInfoset"));
	}


	@Test
	public void should_allow_xml_format_to_be_requested() throws Exception
	{
		requestBody.format = "XML";

		sendRequest();

		assertParam(FORMAT, is("XML"));
	}


	@Test
	public void should_not_allow_other_formats_to_be_requested() throws Exception
	{
		requestBody.format = "SomeFormat";

		sendRequest();

		assertSoapFault();
	}


	@Test
	public void should_return_the_expected_page_size_if_one_is_specified() throws Exception
	{
		requestBody.pageSize = 123;

		sendRequest();

		assertIntegerParam(PAGE_SIZE, is(123));
	}


	@Test
	public void should_return_default_page_size_if_non_specified() throws Exception
	{
		requestBody.pageSize = null;

		sendRequest();

		int pageSize = injector.getInstance(Key.get(int.class, DefaultPageSize.class));

		assertIntegerParam(PAGE_SIZE, is(pageSize));
	}


	@Test
	public void should_return_a_expires_date_that_matches_the_time_to_live() throws Exception
	{
		sendRequest();

		int ttl = injector.getInstance(Key.get(int.class, TTL.class));

		long expectedExpires = System.currentTimeMillis() / 1000 + ttl;
		long errorMargin = 1;

		assertDoubleParam(EXPIRES, is(closeTo(expectedExpires, errorMargin)));
	}


	@Test
	public void should_return_a_history_id_param_that_matches_the_request() throws Exception
	{
		requestBody.historyId = RandomStringUtils.randomNumeric(20);

		sendRequest();

		assertParam(HISTORY_ID, is(requestBody.historyId));
	}


	@Test
	public void should_return_a_zero_history_id_param_for_initializing_calls() throws Exception
	{
		requestBody.historyId = null;

		sendRequest();

		assertIntegerParam(HISTORY_ID, is(0));
	}


	@Test
	public void should_return_an_url_for_the_requested_resource() throws Exception
	{
		sendRequest();

		assertParam(ENTITY_TYPE, is(requestedEntity));
	}


	@Test
	public void should_return_a_signature() throws Exception
	{
		// TODO: Move to URL factory test.

		sendRequest();

		assertParamNotNull(SIGNATURE);
	}


	//
	// Helper Methods
	//

	protected void sendRequest() throws Exception
	{
		request.setBody(requestBody.serialize());

		String xml = requestToString(request);

		processor.process(xml, clientCVR, httpMethod);

		response = factory.deserializeReply(processor.getResponse());

		Element element = response.getBody();

		if (element != null)
		{
			responseBody = GatewayResponse.deserialize(element);
		}
	}


	protected URL getResponseURL() throws Exception
	{
		return new URL(responseBody.getUrl());
	}


	private void assertIntegerParam(String paramName, Matcher<Integer> matcher) throws Exception
	{
		Integer actualValue = new Integer(getResponseURLParam(paramName));
		assertThat(actualValue, matcher);
	}


	private void assertDoubleParam(String paramName, Matcher<Double> matcher) throws Exception
	{
		Double actualValue = new Double(getResponseURLParam(paramName));
		assertThat(actualValue, matcher);
	}


	private <T> void assertParam(String paramName, Matcher<T> expectation) throws Exception
	{
		@SuppressWarnings("unchecked")
		T value = (T) getResponseURLParam(paramName);
		assertThat(value, expectation);
	}


	private void assertParamNotNull(String paramName) throws Exception
	{
		assertNotNull(getResponseURLParam(paramName));
	}


	protected String getResponseURLParam(String paramName) throws Exception
	{
		return getUrlParams(getResponseURL()).get(paramName);
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


	protected String createValidRequest() throws Exception
	{
		request.setBody(requestBody.serialize());
		return requestToString(request);
	}


	protected GatewayRequest createValidRequestBody()
	{
		GatewayRequest requestBody = new GatewayRequest();

		requestBody.entity = requestedEntity;
		requestBody.version = 1;
		requestBody.format = "XML";
		requestBody.pageSize = 10;
		requestBody.historyId = "0";

		return requestBody;
	}


	protected String requestToString(Request request) throws Exception
	{
		StringWriter writer = new StringWriter();

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new DOMSource(request.serialize2DOMDocument()), new StreamResult(writer));

		return writer.toString();
	}


	private void authorizeAccessToEntity(String userCVR, String entityID) throws Exception
	{
		PermissionRepository repo = injector.getInstance(PermissionRepository.class);
		when(repo.canAccessEntity(userCVR, entityID)).thenReturn(true);
	}


	private void deauthorizeAccessToEntity(String userCVR, String entityID) throws Exception
	{
		PermissionRepository repo = injector.getInstance(PermissionRepository.class);
		when(repo.canAccessEntity(userCVR, entityID)).thenReturn(false);
	}
}
