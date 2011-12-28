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

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class CprAbbsResponseParserTest {

    private static final String exampleSoapResponse = "<?xml version='1.0' encoding='UTF-8'?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Header><ns4:Security xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:ns3=\"urn:oasis:names:tc:SAML:2.0:assertion\" xmlns:ns4=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:ns5=\"http://nsi.dk/cprabbs/2011/10\" xmlns:ns6=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" xmlns:ns7=\"http://rep.oio.dk/medcom.sundcom.dk/xml/schemas/2007/02/01/\" xmlns:ns8=\"http://rep.oio.dk/ebxml/xml/schemas/dkcc/2003/02/13/\" xmlns:ns9=\"http://rep.oio.dk/itst.dk/xml/schemas/2006/01/17/\" xmlns:ns10=\"http://rep.oio.dk/cpr.dk/xml/schemas/core/2005/03/18/\" xmlns:ns11=\"http://rep.oio.dk/cpr.dk/xml/schemas/core/2006/01/17/\" xmlns:ns12=\"http://rep.oio.dk/itst.dk/xml/schemas/2005/02/22/\" xmlns:ns13=\"http://rep.oio.dk/cpr.dk/xml/schemas/core/2005/11/24/\" xmlns:ns14=\"http://rep.oio.dk/ebxml/xml/schemas/dkcc/2005/03/15/\" xmlns:ns15=\"http://rep.oio.dk/itst.dk/xml/schemas/2005/06/24/\" xmlns:ns16=\"http://rep.oio.dk/xkom.dk/xml/schemas/2005/03/15/\" xmlns:ns17=\"http://rep.oio.dk/ebxml/xml/schemas/dkcc/2005/05/13/\" xmlns:ns18=\"http://rep.oio.dk/xkom.dk/xml/schemas/2006/01/06/\" xmlns:ns19=\"http://rep.oio.dk/cpr.dk/xml/schemas/core/2006/09/01/\" xmlns:ns20=\"http://rep.oio.dk/ois.dk/xml/schemas/2006/04/25/\" xmlns:ns21=\"http://rep.oio.dk/ebxml/xml/schemas/dkcc/2005/09/01/\" xmlns:ns22=\"http://www.medcom.dk/dgws/2006/04/dgws-1.0.xsd\" xmlns:ns23=\"http://nsi.dk/2011/09/23/StamdataCpr/\"><ns6:Timestamp><ns6:Created>2011-12-28T08:21:08Z</ns6:Created></ns6:Timestamp><ns3:Assertion IssueInstant=\"2011-12-28T08:16:08Z\" Version=\"2.0\" id=\"IDCard\"><ns3:Issuer>TheSOSILibrary</ns3:Issuer><ns3:Subject><ns3:NameID Format=\"medcom:other\">123</ns3:NameID><ns3:SubjectConfirmation><ns3:ConfirmationMethod>urn:oasis:names:tc:SAML:2.0:cm:holder-of-key</ns3:ConfirmationMethod><ns3:SubjectConfirmationData><ns2:KeyInfo><ns2:KeyName>OCESSignature</ns2:KeyName></ns2:KeyInfo></ns3:SubjectConfirmationData></ns3:SubjectConfirmation></ns3:Subject><ns3:Conditions NotBefore=\"2011-12-28T08:16:08Z\" NotOnOrAfter=\"2011-12-29T08:16:08Z\"/><ns3:AttributeStatement id=\"IDCardData\"><ns3:Attribute Name=\"sosi:IDCardID\"><ns3:AttributeValue>kO/x0t+cwPa0sMQoGBxF/w==</ns3:AttributeValue></ns3:Attribute><ns3:Attribute Name=\"sosi:IDCardVersion\"><ns3:AttributeValue>1.0.1</ns3:AttributeValue></ns3:Attribute><ns3:Attribute Name=\"sosi:IDCardType\"><ns3:AttributeValue>system</ns3:AttributeValue></ns3:Attribute><ns3:Attribute Name=\"sosi:AuthenticationLevel\"><ns3:AttributeValue>3</ns3:AttributeValue></ns3:Attribute><ns3:Attribute Name=\"sosi:OCESCertHash\"><ns3:AttributeValue>pFQXTvhBFAsCwPy2VkYI6GDTnFQ=</ns3:AttributeValue></ns3:Attribute></ns3:AttributeStatement><ns3:AttributeStatement id=\"SystemLog\"><ns3:Attribute Name=\"medcom:ITSystemName\"><ns3:AttributeValue>ACME Pro</ns3:AttributeValue></ns3:Attribute><ns3:Attribute Name=\"medcom:CareProviderID\" NameFormat=\"medcom:cvrnumber\"><ns3:AttributeValue>12345678</ns3:AttributeValue></ns3:Attribute><ns3:Attribute Name=\"medcom:CareProviderName\"><ns3:AttributeValue>dk</ns3:AttributeValue></ns3:Attribute></ns3:AttributeStatement><ns2:Signature id=\"OCESSignature\"><ns2:SignedInfo><ns2:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><ns2:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/><ns2:Reference URI=\"#IDCard\"><ns2:Transforms><ns2:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/><ns2:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/></ns2:Transforms><ns2:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/><ns2:DigestValue>FtLdOHukO/MXEBOvM9kvGNDRNh4=</ns2:DigestValue></ns2:Reference></ns2:SignedInfo><ns2:SignatureValue>kKBaH6VqYvoz34K4zvXz/ri2H4KuDrtZXkKa/o4fVjMoOXVTQBVSliszY7xKCeObjHanj0DUH0iUvHvLgnhZAF2J9KWFUSp9KuBrRU031/cM1u/GDihqrrLG7OkVuYOiTt2ubr8m1FlIfneIavvp2fOHPK4hWpUsiuA2vb/VjZY=</ns2:SignatureValue><ns2:KeyInfo><ns2:X509Data><ns2:X509Certificate>MIIFBjCCBG+gAwIBAgIEQDeviTANBgkqhkiG9w0BAQUFADA/MQswCQYDVQQGEwJESzEMMAoGA1UEChMDVERDMSIwIAYDVQQDExlUREMgT0NFUyBTeXN0ZW10ZXN0IENBIElJMB4XDTEwMTExMDEzMjUwMFoXDTEyMTExMDEzNTUwMFowgYMxCzAJBgNVBAYTAkRLMSgwJgYDVQQKEx9EYW5za2UgUmVnaW9uZXIgLy8gQ1ZSOjU1ODMyMjE4MUowIQYDVQQDExpEYW5za2UgUmVnaW9uZXIgLSBTT1NJIFNUUzAlBgNVBAUTHkNWUjo1NTgzMjIxOC1VSUQ6MTE2MzQ0NzM2ODYyNzCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAuw7xSHEdBuhZZDhpPrW3OZ1qEXSf5GHC/Q/mYguyd+pFmUwPico+dLOTroKlTem3LsNgkIJRbKidTaEamyrt/VSg3sH3rVsYh3Cs9jgpMs2N6BJzIkRcmpiFWhMZcej++mZsntxiG2QyVKXj+bYPPV/El4xzeTqwyAYOgAIwn+sCAwEAAaOCAsgwggLEMA4GA1UdDwEB/wQEAwIDuDArBgNVHRAEJDAigA8yMDEwMTExMDEzMjUwMFqBDzIwMTIxMTEwMTM1NTAwWjBGBggrBgEFBQcBAQQ6MDgwNgYIKwYBBQUHMAGGKmh0dHA6Ly90ZXN0Lm9jc3AuY2VydGlmaWthdC5kay9vY3NwL3N0YXR1czCCAQMGA1UdIASB+zCB+DCB9QYJKQEBAQEBAQEDMIHnMC8GCCsGAQUFBwIBFiNodHRwOi8vd3d3LmNlcnRpZmlrYXQuZGsvcmVwb3NpdG9yeTCBswYIKwYBBQUHAgIwgaYwChYDVERDMAMCAQEagZdUREMgVGVzdCBDZXJ0aWZpa2F0ZXIgZnJhIGRlbm5lIENBIHVkc3RlZGVzIHVuZGVyIE9JRCAxLjEuMS4xLjEuMS4xLjEuMS4zLiBUREMgVGVzdCBDZXJ0aWZpY2F0ZXMgZnJvbSB0aGlzIENBIGFyZSBpc3N1ZWQgdW5kZXIgT0lEIDEuMS4xLjEuMS4xLjEuMS4xLjMuMBcGCWCGSAGG+EIBDQQKFghvcmdhbldlYjAdBgNVHREEFjAUgRJkcmlmdHZhZ3RAZGFuaWQuZGswgZcGA1UdHwSBjzCBjDBXoFWgU6RRME8xCzAJBgNVBAYTAkRLMQwwCgYDVQQKEwNUREMxIjAgBgNVBAMTGVREQyBPQ0VTIFN5c3RlbXRlc3QgQ0EgSUkxDjAMBgNVBAMTBUNSTDI1MDGgL6AthitodHRwOi8vdGVzdC5jcmwub2Nlcy5jZXJ0aWZpa2F0LmRrL29jZXMuY3JsMB8GA1UdIwQYMBaAFByYCUcaTDi5EMUEKVvx9E6Aasx+MB0GA1UdDgQWBBSNZVo8u8hK4bUxiYRFXitISMp4GDAJBgNVHRMEAjAAMBkGCSqGSIb2fQdBAAQMMAobBFY3LjEDAgOoMA0GCSqGSIb3DQEBBQUAA4GBACcHdr74A1eDrbIyoAfuRdIkMZ183sxlcsONxUz9q5kOS97vOdwzWIgCqWD3L2C/+Bd9uFw2krcVwsN9IS++FiRPiswOWBUDAt6gR649sKWtlDF9mEscCizBh5F5oZdVGXMiFfXucbEU4tB4JwltbQva6+8FdPzgtgB+3+vgYxpc</ns2:X509Certificate></ns2:X509Data></ns2:KeyInfo></ns2:Signature></ns3:Assertion></ns4:Security><ns22:Header xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:ns3=\"urn:oasis:names:tc:SAML:2.0:assertion\" xmlns:ns4=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:ns5=\"http://nsi.dk/cprabbs/2011/10\" xmlns:ns6=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" xmlns:ns7=\"http://rep.oio.dk/medcom.sundcom.dk/xml/schemas/2007/02/01/\" xmlns:ns8=\"http://rep.oio.dk/ebxml/xml/schemas/dkcc/2003/02/13/\" xmlns:ns9=\"http://rep.oio.dk/itst.dk/xml/schemas/2006/01/17/\" xmlns:ns10=\"http://rep.oio.dk/cpr.dk/xml/schemas/core/2005/03/18/\" xmlns:ns11=\"http://rep.oio.dk/cpr.dk/xml/schemas/core/2006/01/17/\" xmlns:ns12=\"http://rep.oio.dk/itst.dk/xml/schemas/2005/02/22/\" xmlns:ns13=\"http://rep.oio.dk/cpr.dk/xml/schemas/core/2005/11/24/\" xmlns:ns14=\"http://rep.oio.dk/ebxml/xml/schemas/dkcc/2005/03/15/\" xmlns:ns15=\"http://rep.oio.dk/itst.dk/xml/schemas/2005/06/24/\" xmlns:ns16=\"http://rep.oio.dk/xkom.dk/xml/schemas/2005/03/15/\" xmlns:ns17=\"http://rep.oio.dk/ebxml/xml/schemas/dkcc/2005/05/13/\" xmlns:ns18=\"http://rep.oio.dk/xkom.dk/xml/schemas/2006/01/06/\" xmlns:ns19=\"http://rep.oio.dk/cpr.dk/xml/schemas/core/2006/09/01/\" xmlns:ns20=\"http://rep.oio.dk/ois.dk/xml/schemas/2006/04/25/\" xmlns:ns21=\"http://rep.oio.dk/ebxml/xml/schemas/dkcc/2005/09/01/\" xmlns:ns22=\"http://www.medcom.dk/dgws/2006/04/dgws-1.0.xsd\" xmlns:ns23=\"http://nsi.dk/2011/09/23/StamdataCpr/\"><ns22:SecurityLevel>3</ns22:SecurityLevel><ns22:Linking><ns22:FlowID>92a0468e-27da-482e-9b9c-1f651df2440e</ns22:FlowID><ns22:MessageID>42foobar</ns22:MessageID></ns22:Linking><ns22:RequireNonRepudiationReceipt>no</ns22:RequireNonRepudiationReceipt></ns22:Header></S:Header><S:Body><ns5:CprAbbsResponse xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:ns3=\"urn:oasis:names:tc:SAML:2.0:assertion\" xmlns:ns4=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:ns5=\"http://nsi.dk/cprabbs/2011/10\" xmlns:ns6=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" xmlns:ns7=\"http://rep.oio.dk/medcom.sundcom.dk/xml/schemas/2007/02/01/\" xmlns:ns8=\"http://rep.oio.dk/ebxml/xml/schemas/dkcc/2003/02/13/\" xmlns:ns9=\"http://rep.oio.dk/itst.dk/xml/schemas/2006/01/17/\" xmlns:ns10=\"http://rep.oio.dk/cpr.dk/xml/schemas/core/2005/03/18/\" xmlns:ns11=\"http://rep.oio.dk/cpr.dk/xml/schemas/core/2006/01/17/\" xmlns:ns12=\"http://rep.oio.dk/itst.dk/xml/schemas/2005/02/22/\" xmlns:ns13=\"http://rep.oio.dk/cpr.dk/xml/schemas/core/2005/11/24/\" xmlns:ns14=\"http://rep.oio.dk/ebxml/xml/schemas/dkcc/2005/03/15/\" xmlns:ns15=\"http://rep.oio.dk/itst.dk/xml/schemas/2005/06/24/\" xmlns:ns16=\"http://rep.oio.dk/xkom.dk/xml/schemas/2005/03/15/\" xmlns:ns17=\"http://rep.oio.dk/ebxml/xml/schemas/dkcc/2005/05/13/\" xmlns:ns18=\"http://rep.oio.dk/xkom.dk/xml/schemas/2006/01/06/\" xmlns:ns19=\"http://rep.oio.dk/cpr.dk/xml/schemas/core/2006/09/01/\" xmlns:ns20=\"http://rep.oio.dk/ois.dk/xml/schemas/2006/04/25/\" xmlns:ns21=\"http://rep.oio.dk/ebxml/xml/schemas/dkcc/2005/09/01/\" xmlns:ns22=\"http://www.medcom.dk/dgws/2006/04/dgws-1.0.xsd\" xmlns:ns23=\"http://nsi.dk/2011/09/23/StamdataCpr/\"><ns5:changedCprs>0101822231</ns5:changedCprs><ns5:changedCprs>0101821234</ns5:changedCprs></ns5:CprAbbsResponse></S:Body></S:Envelope>";
    private static final String exampleResponseWithHeaders = "HTTP/1.1 200 OK\r\n"
            + "Content-Type: text/xml; charset=utf-8\r\n"
            + "Content-Length: 9152\r\n"
            + "Server: Jetty(6.1.22)\r\n"
            + exampleSoapResponse;

    @Test
    public void testBasicParserWithoutHeaders() throws CprAbbsException {
        CprAbbsResponseParser parser = new CprAbbsResponseParser();
        List<String> extractCprNumbers = parser.extractCprNumbersWithoutHeaders(exampleSoapResponse);
        assertEquals(2, extractCprNumbers.size());
        assertEquals("0101822231", extractCprNumbers.get(0));
        assertEquals("0101821234", extractCprNumbers.get(1));
    }

    @Test
    public void testBasicParser() throws CprAbbsException {
        CprAbbsResponseParser parser = new CprAbbsResponseParser();
        List<String> extractCprNumbers = parser.extractCprNumbers(exampleResponseWithHeaders);
        assertEquals(2, extractCprNumbers.size());
        assertEquals("0101822231", extractCprNumbers.get(0));
        assertEquals("0101821234", extractCprNumbers.get(1));
    }
    
}
