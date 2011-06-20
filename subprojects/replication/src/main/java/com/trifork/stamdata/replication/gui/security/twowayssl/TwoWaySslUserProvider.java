package com.trifork.stamdata.replication.gui.security.twowayssl;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provider;
import com.trifork.stamdata.replication.gui.models.User;
import com.trifork.stamdata.replication.gui.models.UserDao;
import com.trifork.stamdata.ssl.AuthenticatedSsnProvider.AuthenticationFailedException;
import com.trifork.stamdata.ssl.AuthenticatedSsnProvider.AuthenticationFailedException.Reason;
import com.trifork.stamdata.ssl.SubjectSerialNumber;
import com.trifork.stamdata.ssl.SubjectSerialNumber.Kind;
import com.trifork.stamdata.ssl.UncheckedProvider;
import com.trifork.stamdata.ssl.annotations.AuthenticatedSSN;

public class TwoWaySslUserProvider implements Provider<User> {
	private static final Logger logger = LoggerFactory.getLogger(TwoWaySslUserProvider.class);
	private final UncheckedProvider<SubjectSerialNumber> ssnProvider;
	private final Provider<HttpServletRequest> request;
	private final UserDao userDao;
	
	@Inject
	public TwoWaySslUserProvider(@AuthenticatedSSN UncheckedProvider<SubjectSerialNumber> ssnProvider, UserDao userDao, Provider<HttpServletRequest> request) {
		this.ssnProvider = ssnProvider;
		this.userDao = userDao;
		this.request = request;
	}

	@Override
	public User get() {
		SubjectSerialNumber subjectSerialNumber;
		try {
			subjectSerialNumber = ssnProvider.get();
		} catch (AuthenticationFailedException e) {
			if(e.getReason() == Reason.NO_CERTIFICATE) {
				logger.info("Attempted to access with no certificate");
			} else {
				logger.info("Attempted to access with INVALID certificate: " + e.getSsn());
			}
			return null;
		}
		if(subjectSerialNumber.getKind() != Kind.MOCES) {
			logger.info("Attempted to access with non-MOCES: " + subjectSerialNumber.getKind());
			return null;
		}
		String cvr = subjectSerialNumber.getCvrNumber();
		String subjectId = subjectSerialNumber.getSubjectId();
		User user = userDao.findByCvrAndRid(cvr, subjectId);
		String page = request.get().getRequestURI();
		if(user != null) {
			logger.info("User (cpr='" + user.getCpr() + "', cvr='" + user.getCvr()
					+ "', subjectId='" + user.getRid() + "') accessed page='" + page);
			return user;
		}
		logger.info("No access for user (cvr='" + cvr + "', subjectId='" + subjectId + "') to page='" + page);
		return null;
	}

}
