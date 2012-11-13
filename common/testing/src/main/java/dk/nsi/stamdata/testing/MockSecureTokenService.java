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
package dk.nsi.stamdata.testing;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Properties;

import org.bouncycastle.util.encoders.Base64;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.AuthenticationLevel;
import dk.sosi.seal.model.CareProvider;
import dk.sosi.seal.model.SignatureUtil;
import dk.sosi.seal.model.SystemIDCard;
import dk.sosi.seal.model.constants.SubjectIdentifierTypeValues;
import dk.sosi.seal.vault.CredentialVault;
import dk.sosi.seal.vault.GenericCredentialVault;

/**
 * Creates ID Cards that are signed using the Test STS's certificate.
 *
 * @author Thomas Børlum <thb@trifork.com>
 */
public final class MockSecureTokenService
{
	private static final String KEYSTORE_PASSWORD = "Test1234";

	private static final String stsKeystoreAsBase64 =
			"/u3+7QAAAAIAAAABAAAAAQARc29zaTphbGlhc19zeXN0ZW0AAAE68+9IPAAAArowgg" +
			"K2MA4GCisGAQQBKgIRAQEFAASCAqIpYQ3MglljOT4+9L/AScQOMSVKBltFcjmHzkbJ" +
			"9s8a9XvjvM25py/zk+0sZ9y3cdslajyboT7H01ohgeZ04JqoxKhb8qYJOBUGV5cg1y" +
			"VNUTlhlIj/bfTTbQt3L11/o5Z0V0I+aYSjzfuxRuivzfM4uzIbd/+k8+6n5YM7iPxf" +
			"5DAmUCAW7UPWbACVxZ7l2SQBZbvLYMEQ7JQeQx/zwHxY4S9LlwssqCn1BkEeBtelS7" +
			"zIchezaZHbxzTC9vl4IEwYQUgmIGXyGn7EJ8fZUSvJGtUwLwjxl+kzzH2N6rOeE3xE" +
			"WHf2EUFOJB27hRKMigY4WfJnEmMuZWuy+5lsv5ZgKN24SJHUmb/JqrVZWKrIRc4zXn" +
			"XWGj5fmTLR9Z60wzpCisEeabNf5Yjc86fwOm6M3z2KZYj3K5I4y9eHTCcvrUopojhV" +
			"nwYHiMY6e1ICyW26HNPHKWwck51k0CCcmZev6HD8seqZ8UbpfCCTOQsDr7r0A1n6Hv" +
			"dYCJvfY4Ex0OjopeJmFaTlvs7e4vlVO1Z/rNyloBbnv1wPNSpQ/FX5FFqctOITGgPN" +
			"6+cspWbhGvKmEqSyqascgghxVqOx49MPyPW/ELPS//IJjl63ta8iREqJyY9s5/Gjyi" +
			"d0LYM1VjCCkEkzCjuH5aS4/fmosrSqe3lAolw1THosx6XROxiRyZk8SFUpYB6y3/6j" +
			"4/IBkBzjgYZubZh6Pp0J8EARRXxXHUZemajXPv7/OsiM9VcNOCjyRXzCkRCo7UjrKC" +
			"8aiGINyPXw4Q5+VvQ/6T3rjp5Fh3s0xnCFgXQvE1v/scPYEuHhBrUX89xPjed1/gdo" +
			"MibHuWX+xk4jQpROY8ZQloVt+Zd3+IOxLxMUiINmX9DrXiNyz6wcSPqdCG/spQmh1V" +
			"Md8QAAAAIABVguNTA5AAAFCjCCBQYwggRvoAMCAQICBEA4R08wDQYJKoZIhvcNAQEF" +
			"BQAwPzELMAkGA1UEBhMCREsxDDAKBgNVBAoTA1REQzEiMCAGA1UEAxMZVERDIE9DRV" +
			"MgU3lzdGVtdGVzdCBDQSBJSTAeFw0xMjExMTIwODU0MzNaFw0xNDExMTIwOTI0MzNa" +
			"MIGDMQswCQYDVQQGEwJESzEoMCYGA1UEChMfRGFuc2tlIFJlZ2lvbmVyIC8vIENWUj" +
			"o1NTgzMjIxODFKMCEGA1UEAxMaRGFuc2tlIFJlZ2lvbmVyIC0gU09TSSBTVFMwJQYD" +
			"VQQFEx5DVlI6NTU4MzIyMTgtVUlEOjExNjM0NDczNjg2MjcwgZ8wDQYJKoZIhvcNAQ" +
			"EBBQADgY0AMIGJAoGBAIGnw1vRv+BPgF+eQOQIt6RD4RF+3gtakl+HuSrEKWt8r6SZ" +
			"woLFDUcKIZZY0RKQsYjx1YAqcnUgMEVLJV4qIq+KyS+Z3yCupH2VS1wzdJ7H25Rq0H" +
			"i3I5Ago9l+LMQ+bgx5r3+4GjxQ7elY3WtjOcqk8K4n3bfShLQ8Mc+lRPSTAgMBAAGj" +
			"ggLIMIICxDAOBgNVHQ8BAf8EBAMCA7gwKwYDVR0QBCQwIoAPMjAxMjExMTIwODU0Mz" +
			"NagQ8yMDE0MTExMjA5MjQzM1owRgYIKwYBBQUHAQEEOjA4MDYGCCsGAQUFBzABhipo" +
			"dHRwOi8vdGVzdC5vY3NwLmNlcnRpZmlrYXQuZGsvb2NzcC9zdGF0dXMwggEDBgNVHS" +
			"AEgfswgfgwgfUGCSkBAQEBAQEBAzCB5zAvBggrBgEFBQcCARYjaHR0cDovL3d3dy5j" +
			"ZXJ0aWZpa2F0LmRrL3JlcG9zaXRvcnkwgbMGCCsGAQUFBwICMIGmMAoWA1REQzADAg" +
			"EBGoGXVERDIFRlc3QgQ2VydGlmaWthdGVyIGZyYSBkZW5uZSBDQSB1ZHN0ZWRlcyB1" +
			"bmRlciBPSUQgMS4xLjEuMS4xLjEuMS4xLjEuMy4gVERDIFRlc3QgQ2VydGlmaWNhdG" +
			"VzIGZyb20gdGhpcyBDQSBhcmUgaXNzdWVkIHVuZGVyIE9JRCAxLjEuMS4xLjEuMS4x" +
			"LjEuMS4zLjAXBglghkgBhvhCAQ0EChYIb3JnYW5XZWIwHQYDVR0RBBYwFIESZHJpZn" +
			"R2YWd0QGRhbmlkLmRrMIGXBgNVHR8EgY8wgYwwV6BVoFOkUTBPMQswCQYDVQQGEwJE" +
			"SzEMMAoGA1UEChMDVERDMSIwIAYDVQQDExlUREMgT0NFUyBTeXN0ZW10ZXN0IENBIE" +
			"lJMQ4wDAYDVQQDEwVDUkwzNjAxoC+gLYYraHR0cDovL3Rlc3QuY3JsLm9jZXMuY2Vy" +
			"dGlmaWthdC5kay9vY2VzLmNybDAfBgNVHSMEGDAWgBQcmAlHGkw4uRDFBClb8fROgG" +
			"rMfjAdBgNVHQ4EFgQUv9SqUWLmZYFZI2VQ0sjYz5c2r50wCQYDVR0TBAIwADAZBgkq" +
			"hkiG9n0HQQAEDDAKGwRWNy4xAwIDqDANBgkqhkiG9w0BAQUFAAOBgQAPiR5kX1usOM" +
			"U03He/2dEV4fVV5DQL3JlCpShbXFZVDUhRhODbQEs24Z84rF7nhEBFqkUrg4ts1IwF" +
			"Kb0fcjw4/lmvsHHGrCuoc8x8TT2QA7XRhZkhq8lkIpRvAVtlHgHnEZCrhKWNyn+xUw" +
			"EKLgW31ofaJ6kjwWxoXwBtuTROrgAFWC41MDkAAARhMIIEXTCCA8agAwIBAgIEQDYX" +
			"/DANBgkqhkiG9w0BAQUFADA/MQswCQYDVQQGEwJESzEMMAoGA1UEChMDVERDMSIwIA" +
			"YDVQQDExlUREMgT0NFUyBTeXN0ZW10ZXN0IENBIElJMB4XDTA0MDIyMDEzNTE0OVoX" +
			"DTM3MDYyMDE0MjE0OVowPzELMAkGA1UEBhMCREsxDDAKBgNVBAoTA1REQzEiMCAGA1" +
			"UEAxMZVERDIE9DRVMgU3lzdGVtdGVzdCBDQSBJSTCBnzANBgkqhkiG9w0BAQEFAAOB" +
			"jQAwgYkCgYEArawANI56sljDsnosDU+Mp4r+RKFys9c5qy8jWZyA+7PYFs4+IZcFxn" +
			"bNuHi8aAcbSFOUJF0PGpNgPEtNc+XAK7p16iawNTYpMkHm2VoInNfwWEj/wGmtb4rK" +
			"DT2a7auGk76q+Xdqnno4PRO8e7AKEHw7pN3kiHmZCI48PTRpRx8CAwEAAaOCAmQwgg" +
			"JgMA8GA1UdEwEB/wQFMAMBAf8wDgYDVR0PAQH/BAQDAgEGMIIBAwYDVR0gBIH7MIH4" +
			"MIH1BgkpAQEBAQEBAQEwgecwLwYIKwYBBQUHAgEWI2h0dHA6Ly93d3cuY2VydGlmaW" +
			"thdC5kay9yZXBvc2l0b3J5MIGzBggrBgEFBQcCAjCBpjAKFgNUREMwAwIBARqBl1RE" +
			"QyBUZXN0IENlcnRpZmlrYXRlciBmcmEgZGVubmUgQ0EgdWRzdGVkZXMgdW5kZXIgT0" +
			"lEIDEuMS4xLjEuMS4xLjEuMS4xLjEuIFREQyBUZXN0IENlcnRpZmljYXRlcyBmcm9t" +
			"IHRoaXMgQ0EgYXJlIGlzc3VlZCB1bmRlciBPSUQgMS4xLjEuMS4xLjEuMS4xLjEuMS" +
			"4wEQYJYIZIAYb4QgEBBAQDAgAHMIGWBgNVHR8EgY4wgYswVqBUoFKkUDBOMQswCQYD" +
			"VQQGEwJESzEMMAoGA1UEChMDVERDMSIwIAYDVQQDExlUREMgT0NFUyBTeXN0ZW10ZX" +
			"N0IENBIElJMQ0wCwYDVQQDEwRDUkwxMDGgL6AthitodHRwOi8vdGVzdC5jcmwub2Nl" +
			"cy5jZXJ0aWZpa2F0LmRrL29jZXMuY3JsMCsGA1UdEAQkMCKADzIwMDQwMjIwMTM1MT" +
			"Q5WoEPMjAzNzA2MjAxNDIxNDlaMB8GA1UdIwQYMBaAFByYCUcaTDi5EMUEKVvx9E6A" +
			"asx+MB0GA1UdDgQWBBQcmAlHGkw4uRDFBClb8fROgGrMfjAdBgkqhkiG9n0HQQAEED" +
			"AOGwhWNi4wOjQuMAMCBJAwDQYJKoZIhvcNAQEFBQADgYEApyoAjiKq6WK5XaKWUpVs" +
			"kutzohv1VcCke/3JeUVtmB+byexJMC171s4RHoqcbufcI2ASVWwu84i45MaKg/nxoq" +
			"ojMyY19/W2wbQFEdsxUCnLa9e9tlWj0xS/AaKeUhk2MBOqv+hMdc71jOqc5JN7T2Ba" +
			"6ZRIY5uXkO3IGZ3XUswtuC7llRj22bX/W8kHMMr4h6XI7Q==";

	private static final Properties properties;
	private static final CredentialVault vault;

	private static final X509Certificate certificate;

	static
	{
		try
		{
			properties = SignatureUtil.setupCryptoProviderForJVM();
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			ByteArrayInputStream byteStream = new ByteArrayInputStream(Base64.decode(stsKeystoreAsBase64));
			keystore.load(byteStream, KEYSTORE_PASSWORD.toCharArray());
			vault = new GenericCredentialVault(properties, keystore, KEYSTORE_PASSWORD);

			certificate = (X509Certificate) keystore.getCertificate((String) keystore.aliases().nextElement());
		}
		catch (Exception e)
		{
			throw new AssertionError(e);
		}
	}
	
	public static SOSIFactory createFactory()
	{
		return new SOSIFactory(vault, properties);
	}


	private MockSecureTokenService()
	{}

	public static SystemIDCard createSignedSystemIDCard(String cvr, AuthenticationLevel auth)
	{
	    String username = "Brian"; // Only used for level 2
	    String password = "Graversen"; // Only used for level 2
	    
	    CareProvider careProvider = new CareProvider(SubjectIdentifierTypeValues.CVR_NUMBER, cvr, "dk");
	    SystemIDCard idCard = createFactory().createNewSystemIDCard("ACME Pro", careProvider, auth, username, password, certificate, "123");
	    
	    return idCard;
	}

	public static SystemIDCard createSignedSystemIDCard(String cvr, AuthenticationLevel auth, String careProviderName, String itSystemName)
	{
	    String username = "Brian"; // Only used for level 2
	    String password = "Graversen"; // Only used for level 2

	    CareProvider careProvider = new CareProvider(SubjectIdentifierTypeValues.CVR_NUMBER, cvr, careProviderName);
	    SystemIDCard idCard = createFactory().createNewSystemIDCard(itSystemName, careProvider, auth, username, password, certificate, "123");

	    return idCard;
	}
}
