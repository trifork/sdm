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
package dk.nsi.stamdata.cpr.pvit.proxy;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Properties;

public class CprAbbsStubJettyServer {

    private Server server;
    private int desiredPort = 0;

    public CprAbbsStubJettyServer() throws IOException {
        Properties properties = new Properties();
        properties.load(new BufferedReader(new FileReader("src/test/resources/test-config.properties")));
        this.desiredPort = Integer.parseInt(properties.getProperty("cprabbs.service.endpoint.port"));

    }

    public void startServer() throws Exception
    {

        if (!available(desiredPort))
        {
            // Wait n seconds and try again.

            if (!waitSecondsForPort(3000, desiredPort))
            {
                if (!waitSecondsForPort(1000, desiredPort))
                {
                    throw new RuntimeException("Port " + desiredPort + " is not available - Jetty server cannot be started");
                }
            }
        }

        server = new Server(desiredPort);

        WebAppContext cprabbsContext = new WebAppContext();
        cprabbsContext.setWar("src/test/resources/cprabbs-stub-webapp");
        cprabbsContext.setContextPath("/cprabbs");
        server.addHandler(cprabbsContext);

        server.start();
    }
    
    public int getPort() throws Exception {
        return desiredPort;
    }

    public void stopServer() throws Exception {
        if (server != null && server.isRunning()) {
            server.stop();
        }
        server = null;
    }


    private boolean waitSecondsForPort( long milliseconds, int port) {

        long t0, t1;
        t0 = System.currentTimeMillis();

        do
        {
            t1 = System.currentTimeMillis();
        }
        while (t1 - t0 < milliseconds);

        return available(port);

    }

    private static int MIN_PORT_NUMBER = 1025;
    private static int MAX_PORT_NUMBER = 65535;

    /**
     * This is borrowed from the Apache MINA project.
     * Checks to see if a specific port is available.
     *
     * @param port the port to check for availability
     */
    public static boolean available(int port)
    {
        if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER)
        {
            throw new IllegalArgumentException("Invalid start port: " + port);
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }

    public static int getAvailableLocalPort() throws IOException {
        int actualPort;
        ServerSocket serverSocket = new ServerSocket(0);
        serverSocket.setReuseAddress(true);
        actualPort = serverSocket.getLocalPort();
        return actualPort;
    }


}
