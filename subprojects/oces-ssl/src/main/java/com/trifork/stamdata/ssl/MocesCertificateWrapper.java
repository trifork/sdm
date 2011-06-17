package com.trifork.stamdata.ssl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openoces.ooapi.certificate.CertificateStatus;
import org.openoces.ooapi.certificate.FocesCertificate;
import org.openoces.ooapi.certificate.MocesCertificate;
import org.openoces.ooapi.certificate.OcesCertificate;
import org.openoces.ooapi.certificate.PocesCertificate;
import org.openoces.ooapi.certificate.VocesCertificate;
import org.openoces.serviceprovider.ServiceProviderSetup;

import com.trifork.stamdata.ssl.SubjectSerialNumber.Kind;

public class MocesCertificateWrapper {
	private final OcesCertificate certificate;

	public MocesCertificateWrapper(OcesCertificate certificate) {
		this.certificate = certificate;
	}

	public boolean isValid() {
		return certificate.validityStatus() == CertificateStatus.VALID && !ServiceProviderSetup.getCurrentChecker().isRevoked(certificate);
	}
	
	public SubjectSerialNumber getSubjectSerialNumber() {
		Kind kind = getKind();
		String cvr = getCvr();
		String subjectId = getSubjectId();
		return new SubjectSerialNumber(kind, cvr, subjectId);
	}
	
	private Kind getKind() {
		if (certificate instanceof FocesCertificate) {
			return Kind.FOCES;
		}
		else if (certificate instanceof VocesCertificate) {
			return Kind.VOCES;
		}
		else if(certificate instanceof MocesCertificate) {
			return Kind.MOCES;
		}
		else if(certificate instanceof PocesCertificate) {
			return Kind.POCES;
		}
		throw new RuntimeException("Could not determine kind of certificate");
	}

	private String getCvr() {
		if(getKind() == Kind.FOCES) {
			return ((FocesCertificate) certificate).getCvr();
		}
		else if (getKind() == Kind.VOCES) {
			return ((VocesCertificate) certificate).getCvr();
		}
		else if (getKind() == Kind.MOCES) {
			return ((MocesCertificate) certificate).getCvr();
		}
		throw new IllegalStateException("Not a certificate with CVR number: " + certificate);
	}
	
	private static Pattern ssnPattern = Pattern.compile("CVR:([\\d]{8})-[RFU]ID:(.+)");
	
	private String getSubjectId() {
		String subjectSerialNumber = certificate.getSubjectSerialNumber();
		Matcher matcher = ssnPattern.matcher(subjectSerialNumber);
		if(!matcher.matches()) {
			throw new RuntimeException("Could not extract subjectId from SubjectSerialNumber");
		}
		return matcher.group(2);
	}
}
