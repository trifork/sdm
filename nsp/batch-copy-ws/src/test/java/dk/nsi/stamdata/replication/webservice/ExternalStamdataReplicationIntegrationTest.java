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
package dk.nsi.stamdata.replication.webservice;

import static org.junit.Assert.assertEquals;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.Holder;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.inject.Inject;
import com.trifork.stamdata.jaxws.SealNamespaceResolver;

import dk.nsi.stamdata.jaxws.generated.Header;
import dk.nsi.stamdata.jaxws.generated.ObjectFactory;
import dk.nsi.stamdata.jaxws.generated.ReplicationFault;
import dk.nsi.stamdata.jaxws.generated.ReplicationRequestType;
import dk.nsi.stamdata.jaxws.generated.ReplicationResponseType;
import dk.nsi.stamdata.jaxws.generated.Security;
import dk.nsi.stamdata.jaxws.generated.StamdataReplication;
import dk.nsi.stamdata.jaxws.generated.StamdataReplicationService;
import dk.nsi.stamdata.replication.models.ClientDao;
import dk.nsi.stamdata.testing.TestServer;

@RunWith(GuiceTestRunner.class)
public class ExternalStamdataReplicationIntegrationTest {
	public static final String WHITELISTED_CVR = "25520041";
	public static final String NON_WHITELISTED_CVR = "87654321";
	private boolean isClientAuthority = false;

	private StamdataReplication client;

	private ReplicationRequestType request;
	private ReplicationResponseType response;
	private Element anyAsElement;

	@Inject
	private Session session;
	@Inject
	private ClientDao clientDao;

	@Before
	public void setUp() throws Exception {

		URL wsdlLocation = new URL("http://ext15-cniab01.nsp-test.netic.dk:8080/stamdata-batch-copy-ws/service/StamdataReplication?wsdl");
		QName serviceName = new QName("http://nsi.dk/2011/10/21/StamdataKrs/", "StamdataReplicationService");
		StamdataReplicationService service = new StamdataReplicationService(wsdlLocation, serviceName);

		service.setHandlerResolver(new SealNamespaceResolver());
		client = service.getStamdataReplication();
	}

	@Ignore
	@Test
	public void testCPRBarnRelationCopy() throws Exception {
		request = new ObjectFactory().createReplicationRequestType();
		request.setRegister("cpr");
		request.setDatatype("barnrelation");
		request.setVersion(1L);
		request.setOffset("00000000000000000000");
		request.setMaxRecords(500L);

		sendRequest();

		printDocument(anyAsElement.getOwnerDocument(), System.out);

		assertResponseContainsRecordAtom("cpr", "barnrelation");
	}

	// Pretty print XML document - good for debugging
	private static void printDocument(Document doc, OutputStream out) {
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.transform(new DOMSource(doc), new StreamResult(
					new OutputStreamWriter(out, "UTF-8")));
		} catch (Exception e) {
			// ignore - this is a test method
			e.printStackTrace();
		}
	}

	private void assertResponseContainsRecordAtom(String register, String datatype) {
		assertEquals(anyAsElement.getLocalName(), "feed");
		assertEquals(anyAsElement.getNamespaceURI(),"http://www.w3.org/2005/Atom");
		assertEquals(anyAsElement.getFirstChild().getFirstChild().getTextContent(), "tag:nsi.dk,2011:" + register + "/"	+ datatype + "/v1");
	}

	private void sendRequest() throws Exception, ReplicationFault {
		Holder<Security> securityHeader;
		Holder<Header> medcomHeader;

		if (isClientAuthority) {
			SecurityWrapper secutityHeadersNotWhitelisted = DGWSHeaderUtil.getVocesTrustedSecurityWrapper(WHITELISTED_CVR);
			securityHeader = secutityHeadersNotWhitelisted.getSecurity();
			medcomHeader = secutityHeadersNotWhitelisted.getMedcomHeader();
		} else {
			SecurityWrapper securityHeaders = DGWSHeaderUtil.getVocesTrustedSecurityWrapper(NON_WHITELISTED_CVR);
			securityHeader = securityHeaders.getSecurity();
			medcomHeader = securityHeaders.getMedcomHeader();
		}

		response = client.replicate(securityHeader, medcomHeader, request);
		anyAsElement = (Element) response.getAny();
	}

}
