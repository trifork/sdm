package com.trifork.stamdata.ssl;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provider;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
public class AuthenticatedSsnProvider implements Provider<String> {
	private static final Logger logger = LoggerFactory.getLogger(AuthenticatedSsnProvider.class);
	private final OcesHelper ocesHelper;
	private final HttpServletRequest request;

	@Inject
	public AuthenticatedSsnProvider(OcesHelper ocesHelper, HttpServletRequest request) {
		this.ocesHelper = ocesHelper;
		this.request = request;

	}

	@Override
	public String get() {
			MocesCertificateWrapper certificate = ocesHelper.extractCertificateFromRequest((HttpServletRequest) request);
			if(certificate.isValid()) {
				return certificate.getSubjectSerialNumber();
			}
			else {
				logger.info("Client used invalid certificate, client={}", certificate.getSubjectSerialNumber());
				return null;
			}
	}

}
