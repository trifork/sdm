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
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import com.google.inject.Provider;


public class StatusServletTest {

	private StatusServlet servlet;

	private HttpServletResponse response;

	private Statement warehouseStatement;
	private Statement adminStatement;

	private EntityManager em;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Before
	public void setUp() throws Exception {

		// The request and response.
		response = mock(HttpServletResponse.class);
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);

		// Connection Providers

		warehouseStatement = mock(Statement.class);

		Connection connection = mock(Connection.class);
		when(connection.createStatement()).thenReturn(warehouseStatement);

		Provider warehouseProvider = mock(Provider.class);
		when(warehouseProvider.get()).thenReturn(connection);

		adminStatement = mock(Statement.class);

		connection = mock(Connection.class);
		when(connection.createStatement()).thenReturn(adminStatement);

		Provider adminProvider = mock(Provider.class);
		when(adminProvider.get()).thenReturn(connection);

		em = mock(EntityManager.class);

		Provider emProbvider = mock(Provider.class);
		when(emProbvider.get()).thenReturn(em);

		// Servlet

		servlet = new StatusServlet(emProbvider);
	}

	@Test
	public void should_return_status_500_if_db_is_down() throws Exception {

		when(em.createNativeQuery(anyString())).thenThrow(new RuntimeException());
		doGet();
		assertStatus(500);
	}

	@Test
	public void should_return_status_200_if_all_is_well() throws Exception {

		Query query = mock(Query.class);
		when(em.createNativeQuery(anyString())).thenReturn(query);
		doGet();
		assertStatus(200);
	}

	// Assertions

	private void assertStatus(int statusCode) {

		verify(response).setStatus(statusCode);
	}

	// Helper Methods

	private void doGet() throws Exception {

		HttpServletRequest request = mock(HttpServletRequest.class);
		servlet.doGet(request, response);
	}
}
