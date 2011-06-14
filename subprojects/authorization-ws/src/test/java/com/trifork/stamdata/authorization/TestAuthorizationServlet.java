package com.trifork.stamdata.authorization;

import static dk.sosi.seal.model.SignatureUtil.setupCryptoProviderForJVM;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provider;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.Reply;
import dk.sosi.seal.model.Request;
import dk.sosi.seal.model.constants.DGWSConstants;
import dk.sosi.seal.modelbuilders.ModelBuildException;
import dk.sosi.seal.vault.EmptyCredentialVault;

@RunWith(MockitoJUnitRunner.class)
public class TestAuthorizationServlet {

	@Mock
	HttpServletRequest in;

	@Mock
	HttpServletResponse out;
	
	@Mock
	PrintWriter writer;

	@Mock
	SOSIFactory sosiFactory;
	SOSIFactory sosiHelper;
	
	@Mock
	Request request;

	@Mock
	JAXBContext jaxbContext;

	@Mock
	Provider<AuthorizationDao> authorizationProvider;
	
	@Mock
	RequestProcessor processor;

	WebService webService;
	
	BufferedReader reader = new BufferedReader(new StringReader(""));

	@Before
	public void setUp() throws IOException
	{
		Set<String> whitelist = ImmutableSet.of("12345678");
		
		webService = new WebService(whitelist, sosiFactory, jaxbContext, authorizationProvider);
		
		sosiHelper = new SOSIFactory(new EmptyCredentialVault(), setupCryptoProviderForJVM());
	}
	
	@Test
	public void should_deserialize_the_request_and_pass_it_to_the_request_processor() throws Exception {

		when(sosiFactory.deserializeRequest(Mockito.anyString())).thenReturn(request);
		when(processor.process(request)).thenReturn(sosiHelper.createNewReply(DGWSConstants.VERSION_1_0_1, "1", "2", "OK"));
		when(out.getWriter()).thenReturn(writer);
		when(in.getReader()).thenReturn(reader);
		
		// We want to examine the contents of the response.
		// So we store it in this variable.
		
		final ThreadLocal<String> response = new ThreadLocal<String>();
		
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
			    Object[] args = invocation.getArguments();
			    response.set((String)args[0]);
			    return null;
			}})
		.when(writer).write(Mockito.anyString());
		
		// Call the service.
		
		webService.doPost(in, out);
		
		Reply reply = sosiHelper.deserializeReply(response.get());
		
		assertFalse(reply.isFault());
	}
	
	@Test
	public void should_return_soap_fault_if_could_not_deserialize_request() throws Exception {
		
		Mockito.stub(sosiFactory.deserializeRequest("")).toThrow(new ModelBuildException("Some exception."));
		when(out.getWriter()).thenReturn(writer);
		
		// We want to examine the contents of the response.
		// So we store it in this variable.
		
		final ThreadLocal<String> response = new ThreadLocal<String>();
		
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
			    Object[] args = invocation.getArguments();
			    response.set((String)args[0]);
			    return null;
			}})
		.when(writer).write(Mockito.anyString());
		
		// Call the service.
		
		webService.doPost(in, out);
		
		Reply reply = sosiFactory.deserializeReply(response.get());
		
		assertTrue(reply.isFault());
	}
}
