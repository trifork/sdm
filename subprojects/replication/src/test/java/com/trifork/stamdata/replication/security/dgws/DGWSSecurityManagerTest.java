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

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import dk.sosi.seal.xml.Base64;


@RunWith(MockitoJUnitRunner.class)
public class DGWSSecurityManagerTest {

	@Mock
	HttpServletRequest request;
	@Mock
	AuthorizationDao authorizationDao;

	DGWSSecurityManager securityManager;

	String token = createRandomToken();
	String viewPath = "/foo/bar/v1";
	
	@Before
	public void setUp() {

		securityManager = new DGWSSecurityManager(authorizationDao);
		
		when(request.getHeader("Authentication")).thenReturn("STAMDATA " + token);
		when(request.getPathInfo()).thenReturn(viewPath);
	}

	@Test
	public void should_accept_valid_tokens_and_ask_the_authorization_doa_if_it_exists() throws Exception { 
		
		when(authorizationDao.isTokenValid(eq(Base64.decode(token)), eq(viewPath.substring(1)))).thenReturn(true);
		
		assertTrue(securityManager.isAuthorized(request));
	}

	@Test
	public void should_require_a_token() {
		
		when(request.getHeader("Authentication")).thenReturn(null);
		
		assertFalse(securityManager.isAuthorized(request));
	}
	
	@Test
	public void knows_client_id() {
		when(authorizationDao.findCvr(Base64.decode(token))).thenReturn("12345678");

		assertEquals("CVR:12345678", securityManager.getClientId(request));
	}

	protected String createRandomToken() {

		byte[] token = new byte[512];
		new Random().nextBytes(token);
		return Base64.encode(token);
	}
}
