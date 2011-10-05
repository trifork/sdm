/**
 * The MIT License
 *
 * Original work sponsored and donated by National Board of e-Health (NSI), Denmark
 * (http://www.nsi.dk)
 *
 * Copyright (C) 2011 National Board of e-Health (NSI), Denmark (http://www.nsi.dk)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dk.nsi.stamdata.cpr.integrationtest.dgws;

import java.io.StringWriter;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import dk.nsi.stamdata.cpr.ws.Header;
import dk.nsi.stamdata.cpr.ws.Linking;
import dk.nsi.stamdata.cpr.ws.Security;
import dk.nsi.stamdata.cpr.ws.Timestamp;
import dk.nsi.stamdata.testing.MockSecureTokenService;
import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.AuthenticationLevel;
import dk.sosi.seal.model.Request;
import dk.sosi.seal.model.SystemIDCard;
import dk.sosi.seal.xml.XmlUtil;


public final class DGWSHeaderUtil
{

	private DGWSHeaderUtil()
	{}


	public static SecurityWrapper getVocesTrustedSecurityWrapper(String careProviderId, String careProviderName, String itSystemName) throws Exception
	{
		return getSecurityWrapper(AuthenticationLevel.VOCES_TRUSTED_SYSTEM, careProviderId, careProviderName, itSystemName);
	}


	public static SecurityWrapper getNoAuthenticationSecurityWrapper(String careProviderId, String careProviderName, String itSystemName) throws Exception
	{
		return getSecurityWrapper(AuthenticationLevel.NO_AUTHENTICATION, careProviderId, careProviderName, itSystemName);
	}


	private static SecurityWrapper getSecurityWrapper(AuthenticationLevel auth, String careProviderId, String careProviderName, String itSystemName) throws Exception
	{
		Security security = new Security();

		JAXBContext context = JAXBContext.newInstance(Security.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new SealNamespacePrefixMapper());

		StringWriter writer = new StringWriter();
		marshaller.marshal(security, writer);

		// Parsing security container to generic Document.

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(IOUtils.toInputStream(writer.toString()));

		SOSIFactory factory = MockSecureTokenService.createFactory();
		
		Request request = getRequest(auth, factory, careProviderId, careProviderName, itSystemName);

		Node node = request.getIDCard().serialize2DOMDocument(factory, request.serialize2DOMDocument());

		// Adding generic "version" of idCard to the security Document.

		Node importNode = doc.importNode(node, true);
		doc.getDocumentElement().appendChild(importNode);

		// Unmarshall the new security object including the idCard.

		Unmarshaller unmarshaller = context.createUnmarshaller();
		String securityHeaderSerialized = XmlUtil.node2String(doc);
		Security securityResult = (Security) unmarshaller.unmarshal(IOUtils.toInputStream(securityHeaderSerialized));
		Timestamp timeStamp = new Timestamp();
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTime(request.getCreationDate());
		cal.set(Calendar.MILLISECOND, 0);
		timeStamp.setCreated(cal);
		securityResult.setTimestamp(timeStamp);
		SecurityWrapper wrap = new SecurityWrapper(securityResult, getMedComHeader(request.getMessageID()));

		return wrap;
	}


	private static Header getMedComHeader(String messageId)
	{
		Header medcomHeader = new Header();

		medcomHeader.setSecurityLevel(3);
		medcomHeader.setRequireNonRepudiationReceipt("no");
		Linking linking = new Linking();
		linking.setMessageID(messageId);
		linking.setFlowID(UUID.randomUUID().toString());
		medcomHeader.setLinking(linking);

		return medcomHeader;
	}


	private static Request getRequest(AuthenticationLevel auth, SOSIFactory factory, String cvr, String careProviderName, String itSystemName) throws CertificateException
	{
		Request request = factory.createNewRequest(false, null);
		SystemIDCard idCard = MockSecureTokenService.createSignedSystemIDCard(cvr);
		request.setIDCard(idCard);
		return request;
	}
}
