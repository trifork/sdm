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
package dk.nsi.stamdata.performance;

import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.w3c.dom.Node;

import dk.nsi.stamdata.testing.MockSecureTokenService;
import dk.sosi.seal.model.Request;
import dk.sosi.seal.model.SystemIDCard;


/**
 * This class is a JMeter plugin that contacts the STS and gets a valid IDCard.
 * 
 * TODO: Should take the STS URL and other parameters.
 * 
 * @author Thomas Børlum (thb@trifork.com)
 */
public class IDCardSampler extends AbstractJavaSamplerClient
{
	@Override
	public SampleResult runTest(JavaSamplerContext context)
	{
		SampleResult result = new SampleResult();
		result.sampleStart();

		try
		{
			String idCard = createIdCard();

			result.setResponseData(idCard.getBytes());
			result.setResponseCode("200");
			result.setResponseMessage(idCard);
			result.setSamplerData(idCard);
			result.setSampleLabel("SOSI IDCard Header");
			result.setContentType("text/xml");
			result.setBytes(idCard.length());
			result.setSuccessful(true);
		}
		catch (Exception ignored)
		{
			result.setSuccessful(false);
			result.setResponseCode("500");
		}

		result.sampleEnd();

		return result;
	}


	private String createIdCard() throws Exception
	{
		SystemIDCard idCard = MockSecureTokenService.createSignedSystemIDCard(SOSITestConstants.TEST_CVR);
		Request createNewRequest = MockSecureTokenService.createFactory().createNewRequest(false, "flowId");
		createNewRequest.setIDCard(idCard);

		StringWriter writer = new StringWriter();
		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

		t.transform(new DOMSource(createNewRequest.serialize2DOMDocument().getFirstChild().getFirstChild()), new StreamResult(writer));

		return writer.toString();
	}


	public static String send(String urlString, Node node) throws Exception
	{
		URL url = new URL(urlString);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// Prepare for SOAP

		connection.setRequestMethod("POST");
		connection.setRequestProperty("SOAPAction", "\"\"");
		connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8;");

		// Send the request XML.

		connection.setDoOutput(true);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new DOMSource(node), new StreamResult(connection.getOutputStream()));

		// Read the response.

		if (connection.getResponseCode() > 400)
		{
			throw new RuntimeException(new Scanner(connection.getErrorStream()).useDelimiter("\\A").next());
		}

		return new Scanner(connection.getInputStream()).useDelimiter("\\A").next();
	}
}
