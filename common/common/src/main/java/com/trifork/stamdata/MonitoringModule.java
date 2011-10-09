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
package com.trifork.stamdata;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

public class MonitoringModule extends ServletModule
{
	@Override
	protected void configureServlets()
	{
		// We must have a binding to actually tell us if the
		// component is behaving as expected.
		
		requireBinding(ComponentMonitor.class);
		
		// The standard is to expose the servlet on '/status'
		// so let's do just that.
		
		serve("/status").with(StatusServlet.class);
	}
	
	@Singleton
	public static class StatusServlet extends HttpServlet
	{
		private final ComponentMonitor monitor;
		
		@Inject
		StatusServlet(ComponentMonitor monitor)
		{
			this.monitor = monitor;
		}
		
		protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
		{
			response.getWriter().println(monitor.isOk() ? "200 OK" : "500 ERROR");
		}
		
		private static final long serialVersionUID = 0L;
	}
}
