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
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import com.trifork.stamdata.client.Main;

public class TwoWaySslSecurityHandler implements SecurityHandler {
	public TwoWaySslSecurityHandler() {
		setupSslCertificates();
	}

	@Override
	public <T> String validAuthorizationTokenFor(Class<T> entityType) throws Exception {
		return "";
	}
	
	private KeyStore createKeyStoreFromParams(String storePath, String password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		if(storePath.startsWith("classpath:")) {
			return createKeyStore(storePath.substring("classpath:".length()), password);
		}
		return createKeyStoreFromFile(storePath, password);
	}
	
	private TrustManager[] createTrustManagers() throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		String trustStorePath = Main.getParameter("stamdata.client.truststore");
		String trustStorePassword = Main.getParameter("stamdata.client.truststore.password");
		KeyStore truststore = createKeyStoreFromParams(trustStorePath, trustStorePassword );
		trustManagerFactory.init(truststore);
		return trustManagerFactory.getTrustManagers();
		
	}
	
	private KeyManager[] createKeyManagers() throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, UnrecoverableKeyException {
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		String keyStorePath = Main.getParameter("stamdata.client.keystore");
		String keyStorePassword = Main.getParameter("stamdata.client.keystore.password");
		KeyStore keyStore = createKeyStoreFromParams(keyStorePath, keyStorePassword );
		keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());
		return keyManagerFactory.getKeyManagers();
		
	}

	private void setupSslCertificates() {
		try {
			TrustManager[] trustManagers = createTrustManagers();
			KeyManager[] keyManagers = createKeyManagers();

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
