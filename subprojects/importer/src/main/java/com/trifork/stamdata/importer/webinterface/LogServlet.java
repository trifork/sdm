package com.trifork.stamdata.importer.webinterface;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Singleton;
import com.trifork.stamdata.importer.config.MySQLConnectionManager;

@Singleton
public class LogServlet extends HttpServlet
{
	private static final long serialVersionUID = 2075725757449922837L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		Connection connection = null;

		PrintWriter writer = resp.getWriter();
		writer.print("<html><head><title>Stamdata Importer - Historik</title></head><body><h1>Stamdata Importer - Historik</h1>");

		try
		{
			connection = MySQLConnectionManager.getConnection();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Import ORDER BY importtime DESC");

			writer.print("<table>");

			while (rs.next())
			{
				writer.print("<tr><td>");
				writer.print(rs.getTimestamp("importtime"));
				writer.print("</td><td>");
				writer.print(rs.getString("spoolername"));
				writer.print("</td></tr>");
			}

			writer.print("</table>");
		}
		catch (Exception e)
		{
			writer.print("<p>Cannot retrieve import stats.</p><pre>");
			writer.print(e.getMessage());
			writer.print("</pre>");
		}
		finally
		{
			MySQLConnectionManager.close(connection);
		}

		writer.print("</body></html>");
	}
}
