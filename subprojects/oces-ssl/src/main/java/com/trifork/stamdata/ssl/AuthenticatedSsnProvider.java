package com.trifork.stamdata.ssl;

import com.google.inject.Provider;
import com.google.inject.servlet.RequestScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

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
        MocesCertificateWrapper certificate = ocesHelper.extractCertificateFromRequest(request);
        if (certificate == null) {
            logger.info("No certificate in request");
            return null;
        } else if (!certificate.isValid()) {
            logger.info("Client used invalid certificate, client={}", certificate.getSubjectSerialNumber());
            return null;
        }
        return certificate.getSubjectSerialNumber();
    }

}
