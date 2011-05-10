// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// Contributor(s): Contributors are attributed in the source code
// where applicable.
// 
// The Original Code is "Stamdata".
// 
// The Initial Developer of the Original Code is Trifork Public A/S.
// 
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
// 
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.client.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class TwoWaySslSecurityHandler implements SecurityHandler {
	public TwoWaySslSecurityHandler() {
		setupSslCertificates();
	}

	@Override
	public <T> String validAuthorizationTokenFor(Class<T> entityType) throws Exception {
		return "";
	}

	private void setupSslCertificates() {
		try {
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(createKeyStore("/truststore.jks", "Test1234"));
			TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(createKeyStore("/keystore.jks", "Test1234"), "Test1234".toCharArray());
			KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(keyManagers, trustManagers, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
		} catch (Exception e) {
			throw new RuntimeException("Could not set up certificates", e);
		}
	}

	private KeyStore createKeyStore(String path, String password) throws KeyStoreException, IOException,
			NoSuchAlgorithmException, CertificateException {
		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		InputStream keystoreStream = getClass().getResourceAsStream(path);
		keystore.load(keystoreStream, password.toCharArray());
		return keystore;
	}
}
