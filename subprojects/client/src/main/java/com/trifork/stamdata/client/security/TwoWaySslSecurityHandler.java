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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
			KeyStore truststore;
			String trustStoreFileName = System.getProperty("stamdata.client.truststore");
			if(trustStoreFileName != null) {
				String trustStorePassword = System.getProperty("stamdata.client.truststore.password");
				truststore = createKeyStoreFromFile(trustStoreFileName, trustStorePassword);
			}
			else {
				truststore = createKeyStore("/truststore.jks", "Test1234");
			}
			trustManagerFactory.init(truststore);
			TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			KeyStore keyStore;
			String keyStoreFileName = System.getProperty("stamdata.client.keystore");
			if(keyStoreFileName != null) {
				String keyStorePassword = System.getProperty("stamdata.client.keystore.password");
				keyStore = createKeyStoreFromFile(keyStoreFileName, keyStorePassword);
				keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());
			}
			else {
				keyStore = createKeyStore("/keystore.jks", "Test1234");
				keyManagerFactory.init(keyStore, "Test1234".toCharArray());
			}
			
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

	private KeyStore createKeyStoreFromFile(String path, String password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		InputStream input = new FileInputStream(path);
		return loadKeyStoreFromStream(input, password);
	}

	private KeyStore loadKeyStoreFromStream(InputStream stream, String password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		keystore.load(stream, password.toCharArray());
		return keystore;
		
	}
	
	private KeyStore createKeyStore(String path, String password) throws KeyStoreException, IOException,
			NoSuchAlgorithmException, CertificateException {
		InputStream keystoreStream = getClass().getResourceAsStream(path);
		return loadKeyStoreFromStream(keystoreStream, password);
	}
}
