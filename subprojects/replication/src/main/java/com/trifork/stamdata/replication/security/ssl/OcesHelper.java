package com.trifork.stamdata.replication.security.ssl;

import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.openoces.ooapi.certificate.OcesCertificate;
import org.openoces.ooapi.certificate.OcesCertificateFactory;
import org.openoces.ooapi.exceptions.TrustCouldNotBeVerifiedException;

public class OcesHelper {

	private final CertificateExtractor certificateExtractor;

	@Inject
	public OcesHelper(CertificateExtractor certificateExtractor) {
		this.certificateExtractor = certificateExtractor;
	}
	
	public MocesCertificateWrapper extractCertificateFromRequest(HttpServletRequest request) {
		X509Certificate[] certificateFromHeader = certificateExtractor.extractCertificatesFromHttpRequest(request);
		if (certificateFromHeader != null) {
			return parseCertificate(certificateFromHeader);
		}
		return null;
	}
	
	public MocesCertificateWrapper parseCertificate(X509Certificate[] certificateList) {
		try {
			OcesCertificate certificate = OcesCertificateFactory.getInstance().generate(Arrays.asList(certificateList));
			return new MocesCertificateWrapper(certificate);
		} catch (TrustCouldNotBeVerifiedException e) {
			throw new RuntimeException("Could not parse certificate", e);
		}
	}

}
