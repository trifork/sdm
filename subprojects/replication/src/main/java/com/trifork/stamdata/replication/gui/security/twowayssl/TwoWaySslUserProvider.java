package com.trifork.stamdata.replication.gui.security.twowayssl;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Provider;
import com.trifork.stamdata.replication.gui.models.User;
import com.trifork.stamdata.replication.gui.models.UserDao;
import com.trifork.stamdata.replication.security.ssl.MocesCertificateWrapper;
import com.trifork.stamdata.replication.security.ssl.MocesCertificateWrapper.Kind;
import com.trifork.stamdata.replication.security.ssl.OcesHelper;

public class TwoWaySslUserProvider implements Provider<User> {
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
			if(certificate.getKind() != Kind.MOCES) {
				return null;
			}
			User user = userDao.findByCvrAndRid(certificate.getCvr(), certificate.getSubjectId());
			if(user != null) {
				return user;
			}
			// TODO use CPR to RID
		}
		return null;
	}

}
