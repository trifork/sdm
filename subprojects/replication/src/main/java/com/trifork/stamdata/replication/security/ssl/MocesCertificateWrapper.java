package com.trifork.stamdata.replication.security.ssl;

import org.openoces.ooapi.certificate.CertificateStatus;
import org.openoces.ooapi.certificate.MocesCertificate;

public class MocesCertificateWrapper {

	private final MocesCertificate certificate;

	public MocesCertificateWrapper(MocesCertificate certificate) {
		this.certificate = certificate;
	}

	public String getCvr() {
		return certificate.getCvr();
	}
	
	public boolean isValid() {
		return certificate.validityStatus() == CertificateStatus.VALID;
	}

}
