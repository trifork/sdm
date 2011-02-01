package com.trifork.sdm.replication.monitoring;


import static com.trifork.sdm.replication.db.properties.Database.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

import javax.servlet.http.*;

import org.junit.*;

import com.google.inject.Provides;
import com.trifork.sdm.replication.GuiceTest;
import com.trifork.sdm.replication.db.properties.*;


public class StatusServletTest extends GuiceTest
{
	private StatusServlet servlet;

	private HttpServletResponse response;
	private HttpServletRequest request;
	private PrintWriter outputWriter;

	private List<Database> upDatabases;


	@Before
	public void setUp() throws Exception
	{
		servlet = getInjector().getInstance(StatusServlet.class);

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
	}


	@Test
	public void should_return_status_500_if_warehouse_db_is_down() throws Exception
	{
		bringDown(WAREHOUSE);

		doGet();

		assertStatus(500);
	}


	@Test
	public void should_return_status_500_if_admin_db_is_down() throws Exception
	{
		bringDown(ADMINISTRATION);

		doGet();

		assertStatus(500);
	}


	@Test
	public void should_return_500_if_both_are_down() throws Exception
	{
		bringDown(ADMINISTRATION);
		bringDown(WAREHOUSE);

		doGet();

		assertStatus(500);
	}


	@Test
	public void should_return_status_200_if_all_is_well() throws Exception
	{
		doGet();

		assertStatus(200);
	}


	//
	// Assertions
	//

	private void assertStatus(int statusCode)
	{
		verify(response).setStatus(statusCode);
	}


	//
	// Helper Methods
	//

	private void doGet() throws Exception
	{
		servlet.doGet(request, response);
	}


	private void bringDown(Database db)
	{
		upDatabases.remove(db);
	}


	private Connection createConnection(boolean isDown) throws Exception
	{
		Statement statement = mock(Statement.class);

		// Whether or not the db is up depends on if it is
		// in the upDatabases set.

		if (isDown)
		{
			when(statement.executeQuery(anyString())).thenThrow(new SQLException());
		}
		else
		{
			ResultSet result = mock(ResultSet.class);
			when(result.next()).thenReturn(true);
			when(statement.executeQuery(anyString())).thenReturn(result);
		}

		Connection connection = mock(Connection.class);
		when(connection.createStatement()).thenReturn(statement);

		return connection;
	}


	//
	// Providers
	//

	@Provides
	@Transactional(ADMINISTRATION)
	public Connection provideAdminConnection() throws Exception
	{
		boolean isDown = !upDatabases.contains(ADMINISTRATION);
		return createConnection(isDown);
	}


	@Provides
	@Transactional(WAREHOUSE)
	public Connection provideWarehouseConnection() throws Exception
	{
		boolean isDown = !upDatabases.contains(WAREHOUSE);
		return createConnection(isDown);
	}
}
