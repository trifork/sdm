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
package com.trifork.stamdata.replication.webservice;

import static org.junit.Assert.assertTrue;

import java.net.URL;

import javax.xml.namespace.QName;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import dk.nsi.stamdata.replication.jaxws.ObjectFactory;
import dk.nsi.stamdata.replication.jaxws.ReplicationRequestType;
import dk.nsi.stamdata.replication.jaxws.StamdataReplication;
import dk.nsi.stamdata.replication.jaxws.StamdataReplicationService;
import dk.nsi.stamdata.testing.TestServer;

public class StamdataReplicationImplIntegrationTest
{
    TestServer server;
    
    @Before
    public void setUp() throws Exception
    {
        server = new TestServer().port(8986).contextPath("/").start();
    }
    
    @After
    public void tearDown() throws Exception
    {
        server.stop();
    }
    
    @Test
    public void truth() throws Exception
    {
        URL wsdlLocation = new URL("http://localhost:8986/service/StamdataReplication?wsdl");
        QName serviceName = new QName("http://nsi.dk/2011/10/21/StamdataKrs/", "StamdataReplicationService");
        StamdataReplicationService service = new StamdataReplicationService(wsdlLocation, serviceName);

        StamdataReplication stamdataReplicationClient = service.getStamdataReplication();
        ReplicationRequestType request = new ObjectFactory().createReplicationRequestType();
        request.setRegister("cpr");
        request.setDatatype("person");
        request.setVersion(1L);
        request.setOffset("0");
        
        SecurityWrapper securityWrapper = DGWSHeaderUtil.getVocesTrustedSecurityWrapper("12345678");
        stamdataReplicationClient.replicate(securityWrapper.getSecurity(), securityWrapper.getMedcomHeader(), request);
        
        assertTrue(true);
    }
}
