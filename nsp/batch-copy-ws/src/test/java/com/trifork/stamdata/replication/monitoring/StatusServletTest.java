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

package com.trifork.stamdata.replication.monitoring;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Provider;

@RunWith(MockitoJUnitRunner.class)
public class StatusServletTest {

	@Mock HttpServletResponse response;
	@Mock PrintWriter writer;
	@Mock Connection connection;
	@Mock Session session;
	@Mock Statement statement;
	@Mock Provider<Session> sessionProvider;
	StatusServlet servlet;

	@Before
	public void setUp() throws Exception {
		when(response.getWriter()).thenReturn(writer);
		when(connection.createStatement()).thenReturn(statement);
		when(sessionProvider.get()).thenReturn(session);

		servlet = new StatusServlet(sessionProvider);
	}

	@Test
	public void returnsStatus200IfAllIsWell() throws Exception {
		SQLQuery query = mock(SQLQuery.class);
		when(session.createSQLQuery(anyString())).thenReturn(query);
		doGet();
		assertStatus(200);
	}

	@Test
	public void returnsStatus500IfDatabaseIsDown() throws Exception {
		when(session.createSQLQuery(anyString())).thenThrow(new RuntimeException());
		doGet();
		assertStatus(500);
	}

	private void assertStatus(int statusCode) {
		verify(response).setStatus(statusCode);
	}

	private void doGet() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		servlet.doGet(request, response);
	}
}
