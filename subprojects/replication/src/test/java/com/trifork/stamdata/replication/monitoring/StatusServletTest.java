// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

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
