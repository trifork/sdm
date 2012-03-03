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
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import org.apache.log4j.Logger;

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
	    private final Logger logger;
	    
	    private boolean failure = false;
		private final Provider<ComponentMonitor> monitor;
		
		private static final String OK = "200 OK";
		private static final String ERROR = "500 ERROR";
		
		@Inject
		StatusServlet(Provider<ComponentMonitor> monitor)
		{
			this.monitor = monitor;
			this.logger = Logger.getLogger(monitor.getClass());
		}
		
		protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
		{
			writeStatus(response);
			response.getWriter().println();
			writeManifestInfo(response);
		}
		
		private void writeManifestInfo(HttpServletResponse response) throws IOException
		{
		    PrintWriter writer = response.getWriter();
		    
		    ServletContext application = getServletConfig().getServletContext();
		    InputStream inputStream = application.getResourceAsStream("/META-INF/MANIFEST.MF");
		    
		    if (inputStream == null)
		    {
		        writer.println("Mode: Development (No MANIFEST.MF file found)");
		        return;
		    }
		    
		    Manifest manifest = new Manifest(inputStream);
		    
		    writer.println("Mode: Production");
		    
		    // Get the main attributes in the manifest
		    //
		    Attributes attrs = (Attributes)manifest.getMainAttributes();

		    // Enumerate each attribute
		    //
		    for (Iterator<?> it = attrs.keySet().iterator(); it.hasNext();)
		    {
    		    // Get attribute name
		        //
    		    Attributes.Name attrName = (Attributes.Name)it.next();
                String attrValue = attrs.getValue(attrName);
    		    writer.println(attrName + ": " + attrValue);
		    }
        }

        protected void writeStatus(HttpServletResponse response) throws IOException
		{
		    boolean isOk = false;
		    
		    try
		    {
		        isOk = monitor.get().isOk();
		    }
		    catch (Exception e)
		    {
		        // Only print errors once, until restart.
		        
		        if (failure != true)
		        {
		            failure = true;
		            logger.error("The component is in an error state. The error must be fixed and the component restarted. The error was caused by the following exception:", e);
		        }
		    }
		    
		    if (isOk)
		    {
		        response.setStatus(200);
		        response.getWriter().println(OK);
		    }
		    else
		    {
		        response.setStatus(500);
		        response.getWriter().println(ERROR);
		    }
		}
		
		private static final long serialVersionUID = 0L;
	}
}
