package com.trifork.stamdata.replication.security.ssl;

import org.openoces.ooapi.certificate.CertificateStatus;
import org.openoces.ooapi.certificate.FocesCertificate;
import org.openoces.ooapi.certificate.MocesCertificate;
import org.openoces.ooapi.certificate.OcesCertificate;
import org.openoces.ooapi.certificate.VocesCertificate;

public class MocesCertificateWrapper {

	private final OcesCertificate certificate;

	public MocesCertificateWrapper(OcesCertificate certificate) {
		this.certificate = certificate;
	}

	public String getCvr() {
		if (certificate instanceof FocesCertificate) {
			return ((FocesCertificate) certificate).getCvr();
		} else if (certificate instanceof VocesCertificate) {
			return ((VocesCertificate) certificate).getCvr();
		} else if (certificate instanceof MocesCertificate) {
			return ((MocesCertificate) certificate).getCvr();
		}
		throw new IllegalStateException("Not a certificate with CVR number: " + certificate);
	}
	
	public boolean isValid() {
		return certificate.validityStatus() == CertificateStatus.VALID;
	}

}
