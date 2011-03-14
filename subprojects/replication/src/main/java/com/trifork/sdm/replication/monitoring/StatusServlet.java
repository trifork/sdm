package com.trifork.sdm.replication.monitoring;


import static com.trifork.sdm.replication.db.properties.Database.*;

import java.io.IOException;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.inject.*;
import com.trifork.sdm.replication.db.properties.Transactional;


@Singleton
public class StatusServlet extends HttpServlet {

	private static final long serialVersionUID = 1324005200396944825L;

	private Provider<Connection> warehouseConnection;
	private Provider<Connection> adminConnection;


	@Inject
	public StatusServlet(@Transactional(WAREHOUSE) Provider<Connection> warehouseConnection, @Transactional(ADMINISTRATION) Provider<Connection> adminConnection) {

		this.warehouseConnection = warehouseConnection;
		this.adminConnection = adminConnection;
	}


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/plain");

		int statusCode = 200;

		try {
			checkWarehouseConnection();
		}
		catch (SQLException e) {
			resp.getWriter().println("ERROR: Could not connect to the data warehouse database.");
			statusCode = 500;
		}

		try {
			checkAdminConnection();
		}
		catch (SQLException e) {
			resp.getWriter().println("ERROR: Could not connect to the administration database.");
			statusCode = 500;
		}

		if (statusCode == 200) {
			resp.getWriter().println("200 OK");
		}

		resp.setStatus(statusCode);
	}


	@Transactional(WAREHOUSE)
	protected boolean checkWarehouseConnection() throws SQLException {
		Connection conn = warehouseConnection.get();
		Statement stm = conn.createStatement();

		stm.executeQuery("SELECT 1");

		stm.close();

		return true;
	}


	@Transactional(ADMINISTRATION)
	protected boolean checkAdminConnection() throws SQLException {
		Connection conn = adminConnection.get();
		Statement stm = conn.createStatement();

		stm.executeQuery("SELECT 1");

		stm.close();

		return true;
	}
}
