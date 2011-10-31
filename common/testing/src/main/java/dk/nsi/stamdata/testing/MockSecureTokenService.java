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
	
	
}
