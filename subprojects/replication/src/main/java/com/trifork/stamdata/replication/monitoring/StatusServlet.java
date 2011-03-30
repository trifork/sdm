package com.trifork.stamdata.replication.monitoring;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;


@Singleton
public class StatusServlet extends HttpServlet {

	private static final long serialVersionUID = 1324005200396944825L;
	private final Provider<Session> session;

	@Inject
	public StatusServlet(Provider<Session> session) {

		this.session = session;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/plain");

		try {
			Session session = this.session.get();
			SQLQuery query = session.createSQLQuery("SELECT 1");
			query.uniqueResult();
			response.setStatus(200);
			response.getWriter().println("200 OK");
		}
		catch (Exception e) {
			response.setStatus(500);
			response.getWriter().println("500 Internal Server Error");
			e.printStackTrace(response.getWriter());
		}
	}
}
