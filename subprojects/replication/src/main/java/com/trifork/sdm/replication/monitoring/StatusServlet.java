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
	@Transactional(WAREHOUSE)
	private Provider<Connection> warehouseConnection;

	@Transactional(ADMINISTRATION)
	private Provider<Connection> adminConnection;


	@Inject
	public StatusServlet(@Transactional(ADMINISTRATION) Provider<Connection> adminConnection, @Transactional(WAREHOUSE) Provider<Connection> warehouseConnection)
	{
		this.adminConnection = adminConnection;
		this.warehouseConnection = warehouseConnection;
	}


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		int statusCode = 200;
		
		try
		{
			checkWarehouseConnection();
		}
		catch (SQLException e)
		{
			resp.getOutputStream().println("ERROR: Could not connect to the data warehouse database.");
			statusCode = 500;
		}

		try
		{
			checkAdminConnection();
		}
		catch (SQLException e)
		{
			resp.getOutputStream().println("ERROR: Could not connect to the administration database.");
			statusCode = 500;
		}
		
		resp.setStatus(statusCode);
	}


	@Transactional(WAREHOUSE)
	protected boolean checkWarehouseConnection() throws SQLException
	{
		Connection conn = warehouseConnection.get();
		Statement stm = conn.createStatement();

		stm.executeQuery("SELECT 1");

		stm.close();

		return true;
	}


	@Transactional(ADMINISTRATION)
	protected boolean checkAdminConnection() throws SQLException
	{
		Connection conn = adminConnection.get();
		Statement stm = conn.createStatement();

		stm.executeQuery("SELECT 1");

		stm.close();

		return true;
	}

	private static final long serialVersionUID = 0;
}
