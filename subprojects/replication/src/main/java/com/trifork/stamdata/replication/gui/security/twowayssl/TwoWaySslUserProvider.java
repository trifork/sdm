package com.trifork.stamdata.replication.gui.security.twowayssl;

import java.util.regex.Pattern;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provider;
import com.trifork.stamdata.replication.gui.models.User;
import com.trifork.stamdata.replication.gui.models.UserDao;
import com.trifork.stamdata.ssl.MocesCertificateWrapper.Kind;
import com.trifork.stamdata.ssl.UncheckedProvider;
import com.trifork.stamdata.ssl.annotations.AuthenticatedSSN;

public class TwoWaySslUserProvider implements Provider<User> {
	private static final Logger logger = LoggerFactory.getLogger(TwoWaySslUserProvider.class);
	private final UserDao userDao;
	private final UncheckedProvider<String> ssnProvider;
	
	@Inject
	public TwoWaySslUserProvider(UserDao userDao, @AuthenticatedSSN UncheckedProvider<String> ssnProvider) {
		this.userDao = userDao;
		this.ssnProvider = ssnProvider;
	}
	@Override
	public User get() {
		String ssn = ssnProvider.get();
		if(!isMoces(ssn)) {
			logger.info("Attempted to access with non-MOCES: {}", ssn);
			return null;
		}
			if(certificate.getKind() != Kind.MOCES) {
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
	Pattern mocesPattern = Pattern.compile("CVR:[\\d]{8}-RID:.+");
	private boolean isMoces(String ssn) {
		return mocesPattern.matcher(ssn).matches();
	}
}
