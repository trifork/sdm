package com.trifork.sdm.replication.monitoring;

import static com.trifork.sdm.replication.db.properties.Database.*;

import java.io.IOException;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.inject.*;
import com.trifork.sdm.replication.db.properties.Transactional;


public class StatusServlet extends HttpServlet
{
	@Inject
	@Transactional(WAREHOUSE)
	private Provider<Connection> warehouseConnection;

	@Inject
	@Transactional(ADMINISTRATION)
	private Provider<Connection> adminConnection;


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		try
		{
			checkWarehouseConnection();
		}
		catch (SQLException e)
		{
			resp.getOutputStream().println("ERROR: Could not connect to the data warehouse database.");
			resp.setStatus(500);
		}

		try
		{
			checkAdminConnection();
		}
		catch (SQLException e)
		{
			resp.getOutputStream().println("ERROR: Could not connect to the administration database.");
			resp.setStatus(500);
		}
	}


	@Transactional(WAREHOUSE)
	protected boolean checkWarehouseConnection() throws SQLException
	{
		Connection conn = warehouseConnection.get();
		Statement stm = conn.createStatement();

		stm.execute("SELECT 1");

		stm.close();

		return true;
	}


	@Transactional(ADMINISTRATION)
	protected boolean checkAdminConnection() throws SQLException
	{
		Connection conn = adminConnection.get();
		Statement stm = conn.createStatement();

		stm.execute("SELECT 1");

		stm.close();

		return true;
	}

	private static final long serialVersionUID = 0;
}
