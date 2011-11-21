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
package com.trifork.stamdata.importer.webinterface;

import dk.nsi.stamdata.testing.TestServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.ServerSocket;

import static com.jayway.restassured.RestAssured.get;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class WebInterfaceIntegrationTest
{
    private int port = 8632;
    private TestServer server;

    @Before
    public void setUp() throws Exception
    {
        server = new TestServer().port(port).start();
    }

    @After
    public void tearDown() throws Exception
    {
        server.stop();
    }

    @Test
    public void testThatAfterStartUpTheStatusPageReturnsStatus200()
    {
        assertThat(get("http://127.0.0.1:8632/status").statusCode(), is(200));
    }

    @Test
    public void testThatAfterStartUpTheMonitoringPageReturnsStatus200()
    {
        assertThat(get("http://127.0.0.1:8632/").statusCode(), is(200));
    }
}
