
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

import static dk.sosi.seal.model.constants.SubjectIdentifierTypeValues.P_NUMBER;
import static dk.sosi.seal.model.constants.SubjectIdentifierTypeValues.SKS_CODE;
import static dk.sosi.seal.model.constants.SubjectIdentifierTypeValues.Y_NUMBER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Date;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;

import com.trifork.stamdata.replication.logging.DatabaseAuditLogger;
import com.trifork.stamdata.replication.replication.views.MockView;
import com.trifork.stamdata.replication.security.dgws.AuthorizationServlet.RequestProcessor;
import com.trifork.stamdata.views.View;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.CareProvider;
import dk.sosi.seal.model.IDCard;
import dk.sosi.seal.model.Reply;
import dk.sosi.seal.model.Request;
import dk.sosi.seal.model.SystemIDCard;
import dk.sosi.seal.model.SystemInfo;
import dk.sosi.seal.model.UserIDCard;
import dk.sosi.seal.model.constants.DGWSConstants;
import dk.sosi.seal.model.constants.SubjectIdentifierTypeValues;


public class RequestProcessorTest {

	private RequestProcessor processor;

	private Request request;
	private Reply reply;

	private String TEST_CVR = "25592123";
	
	private String TEST_VIEW_URI = "stamdata://foo/bar/v1";
	private String TEST_VIEW_NAME = "foo/bar/v1";
	private Class<? extends View> testView;

	private CareProvider careProvider;
	private SystemInfo systemInfo;
	private SystemIDCard idCard;

	private AuthorizationRequestStructure authorizationRequest;

	private Marshaller marshaller;
	private Unmarshaller unmarshaller;
	private JAXBElement<AuthorizationRequestStructure> unmarshallingResult;

	private DGWSSecurityManager securityManager;

	private DatabaseAuditLogger audit;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Before
	public void setUp() throws Exception {

		Reply faultReply = mock(Reply.class);
		when(faultReply.isFault()).thenReturn(true);

		Reply okReply = mock(Reply.class);
		when(okReply.isFault()).thenReturn(false);

		SOSIFactory factory = mock(SOSIFactory.class);
		when(factory.createNewErrorReply(any(Request.class), anyString(), anyString())).thenReturn(faultReply);
		when(factory.createNewReply(any(Request.class), anyString())).thenReturn(okReply);

		audit = mock(DatabaseAuditLogger.class);
		securityManager = mock(DGWSSecurityManager.class);
		Map registry = mock(Map.class);
		
		testView = MockView.class;
		when(registry.get(TEST_VIEW_NAME)).thenReturn(testView);

		authorizationRequest = mock(AuthorizationRequestStructure.class);
		when(authorizationRequest.getViewURI()).thenReturn(TEST_VIEW_URI);

		unmarshallingResult = mock(JAXBElement.class);
		when(unmarshallingResult.getValue()).thenReturn(authorizationRequest);

		marshaller = mock(Marshaller.class);
		unmarshaller = mock(Unmarshaller.class);
		when(unmarshaller.unmarshal(any(Node.class), eq(AuthorizationRequestStructure.class))).thenReturn(unmarshallingResult);

		processor = new RequestProcessor(factory, audit, securityManager, registry, marshaller, unmarshaller);

		// DEFAULT VALID REQUEST

		request = mock(Request.class);
		when(request.getDGWSVersion()).thenReturn(DGWSConstants.VERSION_1_0_1);

		idCard = mock(SystemIDCard.class);
		when(request.getIDCard()).thenReturn(idCard);
		when(idCard.getExpiryDate()).thenReturn(new Date(System.currentTimeMillis() + 10000));
		when(idCard.isValidInTime()).thenReturn(true);

		systemInfo = mock(SystemInfo.class);
		when(idCard.getSystemInfo()).thenReturn(systemInfo);

		careProvider = mock(CareProvider.class);
		when(systemInfo.getCareProvider()).thenReturn(careProvider);
		when(careProvider.getID()).thenReturn(TEST_CVR);
		when(careProvider.getType()).thenReturn(SubjectIdentifierTypeValues.CVR_NUMBER);
	}

	@Test
	public void Should_accept_valid_requests() {

		sendRequest();

		assertFalse(reply.isFault());
	}

	@Test
	public void Should_only_accept_dgws_1_0_1() {

		when(request.getDGWSVersion()).thenReturn(DGWSConstants.VERSION_1_0);

		sendRequest();

		assertTrue(reply.isFault());
	}

	@Test
	public void Should_only_cvr_care_provider() {

		// The other allowed care providers for System ID cards are:
		// SKS_CODE, Y_NUMBER and P_NUMBER.

		when(careProvider.getType()).thenReturn(SKS_CODE, Y_NUMBER, P_NUMBER, SubjectIdentifierTypeValues.CVR_NUMBER);

		sendRequest(); assertTrue(reply.isFault());
		sendRequest(); assertTrue(reply.isFault());
		sendRequest(); assertTrue(reply.isFault());
	}

	@Test
	public void Should_accept_only_it_system_requests() throws Exception {

		IDCard idCard = mock(UserIDCard.class);
		when(request.getIDCard()).thenReturn(idCard);

		sendRequest();

		assertFaultReturned();
	}

	@Test
	public void Should_return_fault_if_the_request_has_expirted() throws Exception {

		when(idCard.isValidInTime()).thenReturn(false);

		sendRequest();

		assertFaultReturned();
	}

	@Test
	public void Should_return_fault_if_the_request_does_not_contain_a_valid_request() throws Exception {

		when(unmarshallingResult.getValue()).thenReturn(null);

		sendRequest();
		
		assertFaultReturned();
	}
	
	public void Should_use_the_security_manager_to_issue_an_authoirzation_token() throws Exception {
		
		sendRequest();
		
		verify(securityManager).issueAuthenticationToken(TEST_CVR, testView, idCard.getExpiryDate());
	}
	
	// ASSERTIONS
	
	@SuppressWarnings("unchecked")
	public void assertFaultReturned() throws Exception {
		
		assertTrue(reply.isFault());
		verify(securityManager, never()).issueAuthenticationToken(anyString(), any(Class.class), any(Date.class));
	}

	// HELPERS

	private void sendRequest() {

		reply = processor.process(request);
	}
}
