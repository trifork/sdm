package com.trifork.stamdata.importer;

import dk.nsi.stamdata.testing.TestServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.nsi.stamdata.*;

import static org.junit.Assert.assertFalse;

public class ComponentIntegrationTest
{
    TestServer server;

    @Before
    public void setUp() throws Exception
    {
        server = new TestServer().start();
    }

    @After
    public void tearDown() throws Exception
    {
        while (true) {}
    }

    @Test
    public void canStartTheComponent()
    {
    }
}
