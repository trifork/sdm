package com.trifork.stamdata.replication.security.ssl;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.RequestScoped;
import com.trifork.stamdata.replication.gui.models.Client;
import com.trifork.stamdata.replication.gui.models.ClientDao;
import com.trifork.stamdata.replication.security.SecurityManager;
import com.trifork.stamdata.ssl.MocesCertificateWrapper;
import com.trifork.stamdata.ssl.OcesHelper;

@RequestScoped
public class SslSecurityManager implements SecurityManager {
	private static final Logger logger = LoggerFactory.getLogger(SslSecurityManager.class);
	private final ClientDao clientDao;
	private final OcesHelper ocesHelper;

	@Inject
	SslSecurityManager(ClientDao clientDao, OcesHelper ocesHelper) {
		this.ocesHelper = checkNotNull(ocesHelper);
		this.clientDao = checkNotNull(clientDao);
	}

	@Override
	public boolean isAuthorized(HttpServletRequest request) {
		MocesCertificateWrapper certificate = ocesHelper.extractCertificateFromRequest(request);
		String remoteIP = request.getRemoteAddr();
		String viewName = request.getPathInfo().substring(1);
		if (certificate != null && certificate.isValid()) {
			Client client = clientDao.findBySubjectSerialNumber(certificate.getSubjectSerialNumber());
			if (client != null) {
				if (client.isAuthorizedFor(viewName)) {
					logger.info("Client '" + client + "' accessed view='" + viewName + "'. ip='" + remoteIP + "'");
					return true;
				}
				logger.info("Client '" + client + "' cannot access view='" + viewName + "'. ip='" + remoteIP + "'");
				return false;
			}
			logger.info("No access for subjectSerialNumber='" + certificate.getSubjectSerialNumber() + "' to view='" + viewName + "'. ip='" + remoteIP + "'");
			return false;
		}
		logger.info("No or invalid certificate when accessing view='" + viewName + "'. ip='" + remoteIP + "'");
		return false;
	}

	@Override
	public String getClientId(HttpServletRequest request) {
		return ocesHelper.extractCertificateFromRequest(request).getSubjectSerialNumber();
	}
	
}
