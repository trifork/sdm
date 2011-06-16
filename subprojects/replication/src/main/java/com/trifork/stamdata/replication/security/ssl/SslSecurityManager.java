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
import com.trifork.stamdata.ssl.AuthenticatedSsnProvider.AuthenticationFailedException;
import com.trifork.stamdata.ssl.MocesCertificateWrapper;
import com.trifork.stamdata.ssl.OcesHelper;
import com.trifork.stamdata.ssl.UncheckedProvider;
import com.trifork.stamdata.ssl.annotations.AuthenticatedSSN;

@RequestScoped
public class SslSecurityManager implements SecurityManager {
	private static final Logger logger = LoggerFactory.getLogger(SslSecurityManager.class);
	private final ClientDao clientDao;
	private final UncheckedProvider<String> authenticatedSsn;

	@Inject
	SslSecurityManager(ClientDao clientDao,  @AuthenticatedSSN UncheckedProvider<String> authenticatedSsn) {
		this.authenticatedSsn = authenticatedSsn;
		this.clientDao = checkNotNull(clientDao);
	}

	@Override
	public boolean isAuthorized(HttpServletRequest request) {
		String remoteIP = request.getRemoteAddr();
		String viewName = request.getPathInfo().substring(1);
		String ssn = authenticatedSsn.get();
		Client client = clientDao.findBySubjectSerialNumber(ssn);
		if (client != null) {
			if (client.isAuthorizedFor(viewName)) {
				logger.info("Client '" + client + "' accessed view='" + viewName + "'. ip='" + remoteIP + "'");
				return true;
			}
			logger.info("Client '" + client + "' cannot access view='" + viewName + "'. ip='" + remoteIP + "'");
			return false;
		}
		logger.info("No access for subjectSerialNumber='" + ssn + "' to view='" + viewName + "'. ip='" + remoteIP + "'");
		return false;
	}

	@Override
	public String getClientId(HttpServletRequest request) {
		return authenticatedSsn.get();
	}
	
}
