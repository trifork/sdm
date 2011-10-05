/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */

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

/**
 * Status Servlet that returns 200 if the db is up and 500 otherwise.
 * 
 * @author Thomas Børlum <thb@trifork.com>
 */
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
