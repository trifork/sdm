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

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.util.encoders.Base64;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.Security;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_utility_1_0.Timestamp;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import dk.medcom.dgws._2006._04.dgws_1_0.Header;
import dk.medcom.dgws._2006._04.dgws_1_0.Linking;
import dk.nsi.common.jaxb.convert.MarshallerFactories.SecurityMarshallerFactory;
import dk.nsi.common.ws.SecurityWrapper;
import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.AuthenticationLevel;
import dk.sosi.seal.model.CareProvider;
import dk.sosi.seal.model.Request;
import dk.sosi.seal.model.SignatureUtil;
import dk.sosi.seal.model.SystemIDCard;
import dk.sosi.seal.model.constants.SubjectIdentifierTypeValues;
import dk.sosi.seal.pki.Federation;
import dk.sosi.seal.vault.CredentialVault;
import dk.sosi.seal.vault.GenericCredentialVault;
import dk.sosi.seal.xml.XmlUtil;

public class IdCardBuilder {

	private static final String stsKeystoreAsBase64 = "/u3+7QAAAAIAAAABAAAAAQARc29zaTphbGlhc19zeXN0ZW0AAAEsNhFwqgAAArswggK3MA4GCisG"
			+ "AQQBKgIRAQEFAASCAqOu+XECVO5mg3cbXCWmHoE+hhNGmHtoGrhAn5hoOzUGhyw6rrXjN8FNB78S"
			+ "834usdVs3OurF1dSUSAMedI9UZ32iYo1EkVt5rOSgygut6EEb3tb6kaXeMiUVrScCX9Vbdg9rOat"
			+ "SCscW94vmToh9Vb4jRfz7N1SjWTuRNtRFXa1zPCaUbZYTMKSJYyukPPAgSsCgYYfIqwZgPPthllM"
			+ "US1zD0fjJDMGLlmD0zNUTMDsA2Zfih+AUdwJ7H/ubITxMfJEp/xjiBPTYaituJZWxUmuiii4Bu2S"
			+ "u+YCM06DbtzNvHe8d+HoW6tlpr9zrRpo9TwiiQYd/Id8F7JtF5B+NkALKoUlFTECt3t5yO66/t6U"
			+ "URvlon/Wk6i6KvzwnoIP/NwxLFTY1ajUcM4X3b1ufXSdtAW9KtSQMTLAS7qMxYeOKie2iVCT4mwi"
			+ "L7NpQbYgV7v9CV84lZEwiVSzj+enHHS3HTdcg/W0imriYPjSb/AG//xneWW4AI3Vk0/CkGJrEn3g"
			+ "6hVqJA+wPgM3rvZqtUZIJRdypCeRedZFFb8nuOJmC8Y42emfr2KoEDLJHJDZtpADo/XZoQchIj6q"
			+ "h/AjEhGiYtCDYaMAVPpIVomB7SuED90cBQU4RuE6P2AyxGNJ8fVUMqUVxH6B0SjL6v4QhvkUMEGm"
			+ "ZQrhNXjP0ZYF5TjxKYkBKrhbiNKH7QkbBEv/aG6h4NLtTRRnQBHSEvWKgZJFlE0XmSNQhnCdoUUu"
			+ "YP/dL26zG8POM8dSlNvn+fjdprtfBIw/6j0/KLmH0OzYzTt3reMVzMLbmhuJ4XkkGad7V+fSK8O+"
			+ "EGGCGVIXNBHVABmfY3o0qtZjLtizYJpjIMQi6Cji5fVAHqs7r05Ayd5AVNNgwFh0gqY3CjncnpZJ"
			+ "ybJ/0qcAAAACAAVYLjUwOQAABQowggUGMIIEb6ADAgECAgRAN6+JMA0GCSqGSIb3DQEBBQUAMD8x"
			+ "CzAJBgNVBAYTAkRLMQwwCgYDVQQKEwNUREMxIjAgBgNVBAMTGVREQyBPQ0VTIFN5c3RlbXRlc3Qg"
			+ "Q0EgSUkwHhcNMTAxMTEwMTMyNTAwWhcNMTIxMTEwMTM1NTAwWjCBgzELMAkGA1UEBhMCREsxKDAm"
			+ "BgNVBAoTH0RhbnNrZSBSZWdpb25lciAvLyBDVlI6NTU4MzIyMTgxSjAhBgNVBAMTGkRhbnNrZSBS"
			+ "ZWdpb25lciAtIFNPU0kgU1RTMCUGA1UEBRMeQ1ZSOjU1ODMyMjE4LVVJRDoxMTYzNDQ3MzY4NjI3"
			+ "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC7DvFIcR0G6FlkOGk+tbc5nWoRdJ/kYcL9D+Zi"
			+ "C7J36kWZTA+Jyj50s5OugqVN6bcuw2CQglFsqJ1NoRqbKu39VKDewfetWxiHcKz2OCkyzY3oEnMi"
			+ "RFyamIVaExlx6P76Zmye3GIbZDJUpeP5tg89X8SXjHN5OrDIBg6AAjCf6wIDAQABo4ICyDCCAsQw"
			+ "DgYDVR0PAQH/BAQDAgO4MCsGA1UdEAQkMCKADzIwMTAxMTEwMTMyNTAwWoEPMjAxMjExMTAxMzU1"
			+ "MDBaMEYGCCsGAQUFBwEBBDowODA2BggrBgEFBQcwAYYqaHR0cDovL3Rlc3Qub2NzcC5jZXJ0aWZp"
			+ "a2F0LmRrL29jc3Avc3RhdHVzMIIBAwYDVR0gBIH7MIH4MIH1BgkpAQEBAQEBAQMwgecwLwYIKwYB"
			+ "BQUHAgEWI2h0dHA6Ly93d3cuY2VydGlmaWthdC5kay9yZXBvc2l0b3J5MIGzBggrBgEFBQcCAjCB"
			+ "pjAKFgNUREMwAwIBARqBl1REQyBUZXN0IENlcnRpZmlrYXRlciBmcmEgZGVubmUgQ0EgdWRzdGVk"
			+ "ZXMgdW5kZXIgT0lEIDEuMS4xLjEuMS4xLjEuMS4xLjMuIFREQyBUZXN0IENlcnRpZmljYXRlcyBm"
			+ "cm9tIHRoaXMgQ0EgYXJlIGlzc3VlZCB1bmRlciBPSUQgMS4xLjEuMS4xLjEuMS4xLjEuMy4wFwYJ"
			+ "YIZIAYb4QgENBAoWCG9yZ2FuV2ViMB0GA1UdEQQWMBSBEmRyaWZ0dmFndEBkYW5pZC5kazCBlwYD"
			+ "VR0fBIGPMIGMMFegVaBTpFEwTzELMAkGA1UEBhMCREsxDDAKBgNVBAoTA1REQzEiMCAGA1UEAxMZ"
			+ "VERDIE9DRVMgU3lzdGVtdGVzdCBDQSBJSTEOMAwGA1UEAxMFQ1JMMjUwMaAvoC2GK2h0dHA6Ly90"
			+ "ZXN0LmNybC5vY2VzLmNlcnRpZmlrYXQuZGsvb2Nlcy5jcmwwHwYDVR0jBBgwFoAUHJgJRxpMOLkQ"
			+ "xQQpW/H0ToBqzH4wHQYDVR0OBBYEFI1lWjy7yErhtTGJhEVeK0hIyngYMAkGA1UdEwQCMAAwGQYJ"
			+ "KoZIhvZ9B0EABAwwChsEVjcuMQMCA6gwDQYJKoZIhvcNAQEFBQADgYEAJwd2vvgDV4OtsjKgB+5F"
			+ "0iQxnXzezGVyw43FTP2rmQ5L3u853DNYiAKpYPcvYL/4F324XDaStxXCw30hL74WJE+KzA5YFQMC"
			+ "3qBHrj2wpa2UMX2YSxwKLMGHkXmhl1UZcyIV9e5xsRTi0HgnCW1tC9rr7wV0/OC2AH7f6+BjGlwA"
			+ "BVguNTA5AAAEYTCCBF0wggPGoAMCAQICBEA2F/wwDQYJKoZIhvcNAQEFBQAwPzELMAkGA1UEBhMC"
			+ "REsxDDAKBgNVBAoTA1REQzEiMCAGA1UEAxMZVERDIE9DRVMgU3lzdGVtdGVzdCBDQSBJSTAeFw0w"
			+ "NDAyMjAxMzUxNDlaFw0zNzA2MjAxNDIxNDlaMD8xCzAJBgNVBAYTAkRLMQwwCgYDVQQKEwNUREMx"
			+ "IjAgBgNVBAMTGVREQyBPQ0VTIFN5c3RlbXRlc3QgQ0EgSUkwgZ8wDQYJKoZIhvcNAQEBBQADgY0A"
			+ "MIGJAoGBAK2sADSOerJYw7J6LA1PjKeK/kShcrPXOasvI1mcgPuz2BbOPiGXBcZ2zbh4vGgHG0hT"
			+ "lCRdDxqTYDxLTXPlwCu6deomsDU2KTJB5tlaCJzX8FhI/8BprW+Kyg09mu2rhpO+qvl3ap56OD0T"
			+ "vHuwChB8O6Td5Ih5mQiOPD00aUcfAgMBAAGjggJkMIICYDAPBgNVHRMBAf8EBTADAQH/MA4GA1Ud"
			+ "DwEB/wQEAwIBBjCCAQMGA1UdIASB+zCB+DCB9QYJKQEBAQEBAQEBMIHnMC8GCCsGAQUFBwIBFiNo"
			+ "dHRwOi8vd3d3LmNlcnRpZmlrYXQuZGsvcmVwb3NpdG9yeTCBswYIKwYBBQUHAgIwgaYwChYDVERD"
			+ "MAMCAQEagZdUREMgVGVzdCBDZXJ0aWZpa2F0ZXIgZnJhIGRlbm5lIENBIHVkc3RlZGVzIHVuZGVy"
			+ "IE9JRCAxLjEuMS4xLjEuMS4xLjEuMS4xLiBUREMgVGVzdCBDZXJ0aWZpY2F0ZXMgZnJvbSB0aGlz"
			+ "IENBIGFyZSBpc3N1ZWQgdW5kZXIgT0lEIDEuMS4xLjEuMS4xLjEuMS4xLjEuMBEGCWCGSAGG+EIB"
			+ "AQQEAwIABzCBlgYDVR0fBIGOMIGLMFagVKBSpFAwTjELMAkGA1UEBhMCREsxDDAKBgNVBAoTA1RE"
			+ "QzEiMCAGA1UEAxMZVERDIE9DRVMgU3lzdGVtdGVzdCBDQSBJSTENMAsGA1UEAxMEQ1JMMTAxoC+g"
			+ "LYYraHR0cDovL3Rlc3QuY3JsLm9jZXMuY2VydGlmaWthdC5kay9vY2VzLmNybDArBgNVHRAEJDAi"
			+ "gA8yMDA0MDIyMDEzNTE0OVqBDzIwMzcwNjIwMTQyMTQ5WjAfBgNVHSMEGDAWgBQcmAlHGkw4uRDF"
			+ "BClb8fROgGrMfjAdBgNVHQ4EFgQUHJgJRxpMOLkQxQQpW/H0ToBqzH4wHQYJKoZIhvZ9B0EABBAw"
			+ "DhsIVjYuMDo0LjADAgSQMA0GCSqGSIb3DQEBBQUAA4GBAKcqAI4iquliuV2illKVbJLrc6Ib9VXA"
			+ "pHv9yXlFbZgfm8nsSTAte9bOER6KnG7n3CNgElVsLvOIuOTGioP58aKqIzMmNff1tsG0BRHbMVAp"
			+ "y2vXvbZVo9MUvwGinlIZNjATqr/oTHXO9YzqnOSTe09gWumUSGObl5DtyBmd11LMRwr4BUdJWaEZnD/pVh2VD2taNxg=";

	private static Properties properties;
	private static CredentialVault vault;

	static {
		try {
			properties = SignatureUtil.setupCryptoProviderForJVM();
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			ByteArrayInputStream byteStream = new ByteArrayInputStream(Base64.decode(stsKeystoreAsBase64));
			keystore.load(byteStream, "Test1234".toCharArray());
			vault = new GenericCredentialVault(properties, keystore, "Test1234");
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}

	private IdCardBuilderForTestPurposes() {
	}

	public static SecurityWrapper getVocesTrustedSecurityWrapper(String careProviderId, String careProviderName,
			String itSystemName) throws Exception {
		return getSecurityWrapper(AuthenticationLevel.VOCES_TRUSTED_SYSTEM, careProviderId, careProviderName,
				itSystemName);
	}

	public static SecurityWrapper getNoAuthenticationSecurityWrapper(String careProviderId, String careProviderName,
			String itSystemName) throws Exception {
		return getSecurityWrapper(AuthenticationLevel.NO_AUTHENTICATION, careProviderId, careProviderName, itSystemName);
	}

	private static SecurityWrapper getSecurityWrapper(AuthenticationLevel auth, String careProviderId,
			String careProviderName, String itSystemName) throws Exception {

		Federation federation = null; // Not used for issuing ID cards
		SOSIFactory factory = new SOSIFactory(federation, vault, properties);

		Security security = new Security();
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = domFactory.newDocumentBuilder();

		// Marshalling security container
		SecurityMarshallerFactory marshallerFactory = new SecurityMarshallerFactory();
		Marshaller marshaller = (Marshaller) marshallerFactory.makeObject();
		JAXBContext context = JAXBContext.newInstance(Security.class);
		StringWriter writer = new StringWriter();
		marshaller.marshal(security, writer);

		// Parsing security container to generic Document
		Document doc = builder.parse(IOUtils.toInputStream(writer.toString()));

		Request request = getRequest(auth, factory, careProviderId, careProviderName, itSystemName);

		Node node = request.getIDCard().serialize2DOMDocument(factory, request.serialize2DOMDocument());

		// Adding generic "version" of idCard to the security Document
		Node importNode = doc.importNode(node, true);
		doc.getDocumentElement().appendChild(importNode);

		// Unmarshall the new security object including the idCard
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Security securityResult = (Security) unmarshaller.unmarshal(IOUtils.toInputStream(XmlUtil.node2String(doc)));
		Timestamp timeStamp = new Timestamp();
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTime(request.getCreationDate());
		cal.set(Calendar.MILLISECOND, 0);
		timeStamp.setCreated(cal);
		securityResult.setTimestamp(timeStamp);
		SecurityWrapper wrap = new SecurityWrapper(securityResult, getMedComHeader(request.getMessageID()));
		return wrap;
	}

	private static Header getMedComHeader(String messageId) {
		Header medcomHeader = new Header();
		medcomHeader.setSecurityLevel(3);
		medcomHeader.setRequireNonRepudiationReceipt("no");
		Linking linking = new Linking();
		linking.setMessageID(messageId);
		linking.setFlowID(UUID.randomUUID().toString());
		medcomHeader.setLinking(linking);
		return medcomHeader;
	}

	private static Request getRequest(AuthenticationLevel auth, SOSIFactory factory, String careProviderId,
			String careProviderName, String itSystemName) throws CertificateException {
		Request request = factory.createNewRequest(false, null);
		SystemIDCard idCard = getIdCard(auth, factory, careProviderId, careProviderName, itSystemName);
		request.setIDCard(idCard);
		return request;
	}

	private static SystemIDCard getIdCard(AuthenticationLevel auth, SOSIFactory factory, String careProviderId,
			String careProviderName, String itSystemName) {
		CareProvider careProvider = new CareProvider(SubjectIdentifierTypeValues.CVR_NUMBER, careProviderId,
				careProviderName);
		String username = null; // Only used for level 2
		String password = null; // Only used for level 2
		X509Certificate certificate = null; // Certificate not used as
											// validation is on CVR numbers only
		SystemIDCard idCard = factory.createNewSystemIDCard(itSystemName, careProvider, auth, username, password,
				certificate, "Trifork");
		return idCard;
	}
}