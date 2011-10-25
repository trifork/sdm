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

import com.trifork.stamdata.jaxws.SealNamespacePrefixMapper;

import dk.nsi.stamdata.replication.jaxws.Header;
import dk.nsi.stamdata.replication.jaxws.Linking;
import dk.nsi.stamdata.replication.jaxws.Security;
import dk.nsi.stamdata.replication.jaxws.Timestamp;
import dk.nsi.stamdata.testing.MockSecureTokenService;
import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.AuthenticationLevel;
import dk.sosi.seal.model.Request;
import dk.sosi.seal.model.SystemIDCard;
import dk.sosi.seal.xml.XmlUtil;


public final class DGWSHeaderUtil
{

    private DGWSHeaderUtil()
    {
    }


    public static SecurityWrapper getVocesTrustedSecurityWrapper(String cvr) throws Exception
    {
        return getSecurityWrapper(AuthenticationLevel.VOCES_TRUSTED_SYSTEM, cvr, "foo", "bar");
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

        return new SecurityWrapper(securityResult, getMedComHeader(request.getMessageID()));
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
