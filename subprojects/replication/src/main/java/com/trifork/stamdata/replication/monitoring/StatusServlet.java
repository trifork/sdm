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
