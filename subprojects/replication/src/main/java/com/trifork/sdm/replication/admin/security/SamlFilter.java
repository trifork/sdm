package com.trifork.sdm.replication.admin.security;


import static com.trifork.sdm.replication.admin.models.RequestAttributes.*;
import static org.slf4j.LoggerFactory.*;

import java.io.IOException;

import javax.servlet.*;

import org.slf4j.Logger;

import com.google.inject.*;
import com.trifork.rid2cpr.RID2CPRFacade;
import com.trifork.sdm.replication.admin.models.IUserRepository;

import dk.itst.oiosaml.sp.UserAssertion;


/**
 * Inserts the user's CPR into a request attribute called 'CPR'.
 */
@Singleton
public class SamlFilter implements Filter {

	private static final Logger logger = getLogger(SamlFilter.class);
	private final RID2CPRFacade ridHelper;
	private final Provider<UserAssertion> userAssertion;
	private final IUserRepository userRepository;


	@Inject
	public SamlFilter(RID2CPRFacade ridHelper, Provider<UserAssertion> userAssertionProvider, IUserRepository userRepository) {

		this.ridHelper = ridHelper;
		this.userAssertion = userAssertionProvider;
		this.userRepository = userRepository;
	}


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			UserAssertion assertion = userAssertion.get();

			// Look up the user's CPR by converting the
			// rID (user id) via a remote web-service.
			// The results are cached.

			String userID = assertion.getRIDNumber();
			String userCPR = ridHelper.getCPR(userID);
			String userCVR = assertion.getCPRNumber();

			// Make sure that the user is authorized as admin.

			if (userRepository.isAdmin(userCPR, userCVR)) {

				logger.info("User CPR='{}' accessing the admin GUI. Converted from rID='{}'.", userCPR, userID);
				request.setAttribute(USER_CPR, userCPR);
				chain.doFilter(request, response);
			}
			else {
				logger.warn("Unauthorized access attempt by user CPR={}, CVR={}.", userCPR, userCVR);
			}
		}
		catch (Exception e) {
			throw new ServletException(e);
		}
	}


	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}


	@Override
	public void destroy() {}
}
