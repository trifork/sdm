package com.trifork.stamdata.ssl;

import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;

public class ContainerSslTerminatedCertificateExtractor implements CertificateExtractor {

	@Override
	public X509Certificate[] extractCertificatesFromHttpRequest(
			HttpServletRequest request) {
		return (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
	}

}
