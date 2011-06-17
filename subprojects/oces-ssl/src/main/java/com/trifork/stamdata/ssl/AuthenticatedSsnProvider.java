package com.trifork.stamdata.ssl;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.servlet.RequestScoped;
import com.trifork.stamdata.ssl.AuthenticatedSsnProvider.AuthenticationFailedException.Reason;

@RequestScoped
public class AuthenticatedSsnProvider implements UncheckedProvider<SubjectSerialNumber> {
    private final OcesHelper ocesHelper;
    private final HttpServletRequest request;

    @Inject
    public AuthenticatedSsnProvider(OcesHelper ocesHelper, HttpServletRequest request) {
        this.ocesHelper = ocesHelper;
        this.request = request;
    }

    @Override
	public SubjectSerialNumber get() {
		MocesCertificateWrapper certificate = ocesHelper.extractCertificateFromRequest(request);
		if (certificate == null) {
			throw new AuthenticationFailedException(Reason.NO_CERTIFICATE, null);
		} else if (!certificate.isValid()) {
			throw new AuthenticationFailedException(Reason.INVALID_CERTIFICATE, certificate.getSubjectSerialNumber().toString());
		}
		return certificate.getSubjectSerialNumber();
	}

    public static class AuthenticationFailedException extends RuntimeException {
		private final Reason reason;
			private final String ssn;
			public enum Reason {
			NO_CERTIFICATE, INVALID_CERTIFICATE
		}
		public AuthenticationFailedException(Reason reason, String ssn) {
			super("Authentication Failed. Reason=" + reason + ", client=" + ssn);
				this.reason = reason;
				this.ssn = ssn;
		}
	
		public Reason getReason() {
			return reason;
		}

		public String getSsn() {
			return ssn;
		}
    }
}
