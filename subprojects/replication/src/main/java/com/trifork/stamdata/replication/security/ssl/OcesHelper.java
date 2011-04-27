package com.trifork.stamdata.replication.security.ssl;

import java.security.cert.X509Certificate;
import java.util.Arrays;

import org.openoces.ooapi.certificate.MocesCertificate;
import org.openoces.ooapi.certificate.OcesCertificate;
import org.openoces.ooapi.certificate.OcesCertificateFactory;
import org.openoces.ooapi.exceptions.TrustCouldNotBeVerifiedException;

public class OcesHelper {

	public MocesCertificateWrapper parseCertificate(X509Certificate[] certificateList) {
		try {
			OcesCertificate certificate = OcesCertificateFactory.getInstance().generate(Arrays.asList(certificateList));
			return new MocesCertificateWrapper((MocesCertificate) certificate);
		} catch (TrustCouldNotBeVerifiedException e) {
			throw new RuntimeException("Could not parse certificate", e);
		}
	}

}
