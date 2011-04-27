package com.trifork.stamdata.replication.security.ssl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.cert.X509Certificate;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.servlet.RequestScoped;
import com.trifork.stamdata.replication.security.SecurityManager;
import com.trifork.stamdata.replication.security.dgws.AuthorizationDao;

@RequestScoped
public class SslSecurityManager implements SecurityManager {
	private final AuthorizationDao authorizationDao;
	private final OcesHelper ocesHelper;

	@Inject
	SslSecurityManager(AuthorizationDao authorizationDao, OcesHelper ocesHelper) {
		this.ocesHelper = checkNotNull(ocesHelper);
		this.authorizationDao = checkNotNull(authorizationDao);
	}

	@Override
	public boolean isAuthorized(HttpServletRequest request) {
		X509Certificate[] certificateFromHeader = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
		if (certificateFromHeader != null) {
			MocesCertificateWrapper certificate = ocesHelper.parseCertificate(certificateFromHeader);
			if (certificate.isValid()) {
				String viewName = request.getPathInfo().substring(1);
				return authorizationDao.isClientAuthorized(certificate.getCvr(), viewName);
			}
		}
		return false;
	}

	@Override
	public String getClientId(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
