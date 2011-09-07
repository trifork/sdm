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
			response.getWriter().println(monitor.isOK() ? "200 OK" : "500 ERROR");
		}
		
		private static final long serialVersionUID = 0L;
	}
}
