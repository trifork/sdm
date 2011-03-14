package com.trifork.stamdata.replication.monitoring;

import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;


@Singleton
public class StatusServlet extends HttpServlet {

	private static final long serialVersionUID = 1324005200396944825L;
	private final Provider<EntityManager> em;

	@Inject
	public StatusServlet(Provider<EntityManager> em) {

		this.em = em;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/plain");

		try {
			EntityManager em = this.em.get();
			Query query = em.createNativeQuery("SELECT 1");
			query.getSingleResult();
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
