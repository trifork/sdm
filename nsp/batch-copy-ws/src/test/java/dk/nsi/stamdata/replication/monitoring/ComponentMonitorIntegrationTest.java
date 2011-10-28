package dk.nsi.stamdata.replication.monitoring;

import static com.jayway.restassured.RestAssured.expect;
import static org.junit.matchers.JUnitMatchers.containsString;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.RestAssured;

import dk.nsi.stamdata.replication.webservice.GuiceTestRunner;
import dk.nsi.stamdata.testing.TestServer;

@RunWith(GuiceTestRunner.class)
public class ComponentMonitorIntegrationTest {

    private TestServer server;

    @Before
    public void setUp() throws Exception {
        server = new TestServer().port(8986).contextPath("/").start();
        RestAssured.port = 8986;
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void testStatusIsOk()
    {
        expect().statusCode(200).body(containsString("200 OK")).when().get("/status");
    }
    
    @Test
    public void testStatusIsNotOkWhenClosingDatabase()
    {
        expect().statusCode(500).body(containsString("200 OK")).when().get("/status");
    }
}
