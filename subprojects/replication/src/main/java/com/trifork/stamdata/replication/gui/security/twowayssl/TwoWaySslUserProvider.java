package com.trifork.stamdata.replication.gui.security.twowayssl;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provider;
import com.trifork.stamdata.replication.gui.models.User;
import com.trifork.stamdata.replication.gui.models.UserDao;
import com.trifork.stamdata.replication.security.ssl.MocesCertificateWrapper;
import com.trifork.stamdata.replication.security.ssl.MocesCertificateWrapper.Kind;
import com.trifork.stamdata.replication.security.ssl.OcesHelper;

public class TwoWaySslUserProvider implements Provider<User> {
	private static final Logger logger = LoggerFactory.getLogger(TwoWaySslUserProvider.class);
	private final OcesHelper ocesHelper;
	private final Provider<HttpServletRequest> request;
	private final UserDao userDao;
	
	@Inject
	public TwoWaySslUserProvider(OcesHelper ocesHelper, UserDao userDao, Provider<HttpServletRequest> request) {
		this.ocesHelper = ocesHelper;
		this.userDao = userDao;
		this.request = request;
		
	}
	@Override
	public User get() {
		MocesCertificateWrapper certificate = ocesHelper.extractCertificateFromRequest(request.get());
		if(certificate != null) {
			if(!certificate.isValid()) {
				logger.info("Attempted to access with INVALID certificate, SubjectSerialNumber=" + certificate.getSubjectSerialNumber());
				return null;
			}
			if(certificate.getKind() != Kind.MOCES) {
				logger.info("Attempted to access with non-MOCES: " + certificate.getKind());
				return null;
			}
			String cvr = certificate.getCvr();
			String subjectId = certificate.getSubjectId();
			User user = userDao.findByCvrAndRid(cvr, subjectId);
			String page = request.get().getRequestURI();
			if(user != null) {
				logger.info("User (cpr='" + user.getCpr() + "', cvr='" + user.getCvr()
						+ "', subjectId='" + user.getRid() + "') accessed page='" + page);
				return user;
			}
			logger.info("No access for user (cvr='" + cvr + "', subjectId='" + subjectId + "') to page='" + page);
		}
		return null;
	}

}
