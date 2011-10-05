/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.google.inject.Provider;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.Reply;
import dk.sosi.seal.model.Request;
import dk.sosi.seal.model.constants.DGWSConstants;
import dk.sosi.seal.model.constants.FlowStatusValues;
import dk.sosi.seal.modelbuilders.ModelBuildException;
import dk.sosi.seal.vault.EmptyCredentialVault;
import dk.sosi.seal.xml.XmlUtilException;


@RunWith(MockitoJUnitRunner.class)
public class TestAuthorizationServlet
{
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
	RequestProcessor processor;

	@Mock
	Provider<RequestProcessor> processorProvider;

	WebService webService;

	BufferedReader reader = new BufferedReader(new StringReader(" "));

	@Before
	public void setUp() throws IOException
	{
		when(processorProvider.get()).thenReturn(processor);

		webService = new WebService(sosiFactory, processorProvider);

		sosiHelper = new SOSIFactory(new EmptyCredentialVault(), setupCryptoProviderForJVM());
	}

	@Test
	public void should_deserialize_the_request_and_pass_it_to_the_request_processor() throws Exception
	{
		when(sosiFactory.deserializeRequest(Mockito.anyString())).thenReturn(request);
		when(processor.process(request)).thenReturn(sosiHelper.createNewReply(DGWSConstants.VERSION_1_0_1, "1", "2", FlowStatusValues.FLOW_FINALIZED_SUCCESFULLY));
		when(out.getWriter()).thenReturn(writer);
		when(in.getReader()).thenReturn(reader);

		// We want to examine the contents of the response.
		// So we store it in this variable.

		final ThreadLocal<String> response = new ThreadLocal<String>();

		doAnswer(new Answer<Object>()
		{
			public Object answer(InvocationOnMock invocation)
			{
				Object[] args = invocation.getArguments();
				response.set((String) args[0]);
				return null;
			}
		}).when(writer).write(Mockito.anyString());

		// Call the service.

		webService.doPost(in, out);

		Reply reply = sosiHelper.deserializeReply(response.get());

		assertFalse(reply.isFault());
	}

	@Test
	public void should_return_soap_fault_if_could_not_deserialize_request() throws Exception
	{
		sosiFactory = new SOSIFactory(new EmptyCredentialVault(), setupCryptoProviderForJVM())
		{
			@Override
			public Request deserializeRequest(String xml) throws XmlUtilException, ModelBuildException
			{
				throw new ModelBuildException("Some exception.");
			}
		};

		webService = new WebService(sosiFactory, processorProvider);
		
		when(out.getWriter()).thenReturn(writer);
		when(in.getReader()).thenReturn(reader);

		// We want to examine the contents of the response.
		// So we store it in this variable.

		final ThreadLocal<String> response = new ThreadLocal<String>();

		doAnswer(new Answer<Object>()
		{
			public Object answer(InvocationOnMock invocation)
			{
				Object[] args = invocation.getArguments();
				response.set((String) args[0]);
				return null;
			}
		}).when(writer).write(Mockito.anyString());

		// Call the service.

		webService.doPost(in, out);

		Reply reply = sosiFactory.deserializeReply(response.get());

		assertTrue(reply.isFault());
	}
}
