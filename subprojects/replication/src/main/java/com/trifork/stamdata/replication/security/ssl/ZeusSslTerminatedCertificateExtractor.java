package com.trifork.stamdata.replication.security.ssl;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Zeus load balancer used by Netic puts the SSL client certificate into a HTTP header.
 *
 */
public class ZeusSslTerminatedCertificateExtractor implements CertificateExtractor {
	private static final Logger logger = LoggerFactory.getLogger(ZeusSslTerminatedCertificateExtractor.class);
	@Override
	public X509Certificate[] extractCertificatesFromHttpRequest(
			HttpServletRequest request) {
		String pemText = request.getHeader("SSLClientCert");
		try {
			byte[] base64Decoded = Base64.decodeBase64(pemText); 
			X509Certificate certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(base64Decoded));
			return new X509Certificate[] { certificate  };
		} catch (CertificateException e) {
			logger.error("Could not parse certificate from Zeus", e);
			return null;
		}
	}
}