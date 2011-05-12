package com.trifork.stamdata.replication.security.ssl;

import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;

public interface CertificateExtractor {
	X509Certificate[] extractCertificatesFromHttpRequest(HttpServletRequest request);
}
