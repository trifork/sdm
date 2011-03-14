package com.trifork.sdm.replication.monitoring;


import static com.trifork.sdm.replication.db.properties.Database.*;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Provider;
import com.trifork.sdm.replication.db.properties.Database;


public class StatusServletTest {

	private StatusServlet servlet;

	private HttpServletResponse response;
	private HttpServletRequest request;
	private PrintWriter outputWriter;

	private List<Database> upDatabases;

	private Statement warehouseStatement;
	private Statement adminStatement;


	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Before
	public void setUp() throws Exception {

		// The request and response.

		request = mock(HttpServletRequest.class);

		response = mock(HttpServletResponse.class);
		outputWriter = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(outputWriter);

		// A list containing the databases we simulate as being up
		// and running.

		upDatabases = new ArrayList<Database>();
		upDatabases.add(ADMINISTRATION);
		upDatabases.add(WAREHOUSE);

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

		// Servlet

		servlet = new StatusServlet(warehouseProvider, adminProvider);
	}


	@Test
	public void should_return_status_500_if_warehouse_db_is_down() throws Exception {

		bringUp(ADMINISTRATION);
		bringDown(WAREHOUSE);
		doGet();
		assertStatus(500);
	}


	@Test
	public void should_return_status_500_if_admin_db_is_down() throws Exception {

		bringUp(WAREHOUSE);
		bringDown(ADMINISTRATION);
		doGet();
		assertStatus(500);
	}


	@Test
	public void should_return_500_if_both_are_down() throws Exception {

		bringDown(ADMINISTRATION);
		bringDown(WAREHOUSE);

		doGet();

		assertStatus(500);
	}


	@Test
	public void should_return_status_200_if_all_is_well() throws Exception {

		bringUp(WAREHOUSE);
		bringUp(ADMINISTRATION);
		doGet();
		assertStatus(200);
	}


	// Assertions
	//

	private void assertStatus(int statusCode) {
		verify(response).setStatus(statusCode);
	}


	// Helper Methods
	//

	private void doGet() throws Exception {
		servlet.doGet(request, response);
	}


	private void bringUp(Database db) throws SQLException {
		if (db == WAREHOUSE) {
			when(warehouseStatement.executeQuery(anyString())).thenReturn(null);
		}
		else {
			when(adminStatement.executeQuery(anyString())).thenReturn(null);
		}
	}


	private void bringDown(Database db) throws SQLException {
		if (db == WAREHOUSE) {
			when(warehouseStatement.executeQuery(anyString())).thenThrow(new SQLException());
		}
		else {
			when(adminStatement.executeQuery(anyString())).thenThrow(new SQLException());
		}
	}
}
