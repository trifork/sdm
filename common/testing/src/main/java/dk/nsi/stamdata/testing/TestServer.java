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
package dk.nsi.stamdata.testing;

import java.io.File;
import java.net.URL;

import org.junit.Assert;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

import static java.lang.String.format;
import static org.junit.Assert.assertTrue;


public class TestServer
{
    private Server server;
    private String contextPath = "/";
    private int port = 8972;
    private String warPath = "src/main/webapp/";

    public TestServer()
    {
        server = new Server();
    }


    public TestServer start() throws Exception
    {
        WebAppContext context = new WebAppContext();

        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(port);

        File war = new File("." , warPath);
        assertTrue(new File(war, "WEB-INF/web.xml").getAbsolutePath(), new File(war, "WEB-INF/web.xml").exists());
        System.out.println("webapp: " + war.getAbsolutePath());
        
        context.setWar(war.getAbsolutePath());

        context.setContextPath(contextPath);
        server.setHandler(context);
        server.addConnector(connector);

        server.start();

        if (server.isFailed())
        {
            throw new IllegalStateException("Failed to start the test server. Check the log.");
        }

        return this;
    }


    public TestServer port(int port)
    {
        this.port = port;

        return this;
    }


    public TestServer contextPath(String contextPath)
    {
        this.contextPath = contextPath;

        return this;
    }
    
    public TestServer warPath(String warPath)
    {
        this.warPath = warPath;
        
        return this;
    }

    public String warPath()
    {
        return warPath;
    }


    public void stop() throws Exception
    {
        if (server != null && server.isRunning())
        {
            server.stop();
        }

        server.destroy();
        server = null;
    }

    public void join() throws InterruptedException
    {
        server.join();
    }
}
