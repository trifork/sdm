
// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// Contributor(s): Contributors are attributed in the source code
// where applicable.
// 
// The Original Code is "Stamdata".
// 
// The Initial Developer of the Original Code is Trifork Public A/S.
// 
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
// 
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.replication.gui.security.saml;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.trifork.rid2cpr.RID2CPRFacade;
import com.trifork.stamdata.replication.gui.models.User;
import com.trifork.stamdata.replication.gui.models.UserDao;
import com.trifork.wsclient.SoapException;

import dk.itst.oiosaml.sp.UserAssertion;
import dk.itst.oiosaml.sp.UserAssertionHolder;


/**
 * A filter that denies unauthorized access to the administration GUI.
 * 
 * Responsibilities:
 * 
 * <ol>
 * <li>Deny access to unauthorized requests.</li>
 * <li>Provide the currently logged-in user.</li>
 * </ol>
 * 
 * Note:
 * 
 * Currently all users in the database are administrators.
 * 
 * This implementation requires that you have filtered requests through
 * OIOSAML's SPFilter before trying to get the user.
 * 
 * @see dk.itst.oiosaml.sp.service.SPFilter
 */
@Singleton
public class LoginFilter implements Filter, Provider<User> {

	private static final Logger logger = getLogger(LoginFilter.class);
	private final RID2CPRFacade ridHelper;
	private final Provider<UserDao> userDao;

	@Inject
	LoginFilter(RID2CPRFacade ridHelper, Provider<UserDao> userDao) {

		this.ridHelper = checkNotNull(ridHelper);
		this.userDao = checkNotNull(userDao);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;

		// If a user is not authorized then the user will be null.
		User currentUser = get();

		String remoteIP = request.getRemoteAddr();

		if (currentUser == null) {
			logger.warn("Unauthorized access attempt to page={}. ip={}", httpRequest.getPathInfo(), remoteIP);
		}
		else {
			if (logger.isInfoEnabled()) {
				logger.info("User (cpr={}, cvr={}) accessed page={}. ip={}", new Object[] { currentUser.getCpr(), currentUser.getCvr(), httpRequest.getPathInfo(), remoteIP });
			}
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		// Do nothing
	}

	@Override
	public void destroy() {

		// Do nothing
	}

	@Override
	public User get() {

		User user = null;

		try {
			// User assertion has an unfortunate name.

			// TODO: Do logging for these error cases. This is actually // quite
			// important since it is a bit foggy which properties // are set on
			// the
			// assertions from different IdP's.

			UserAssertion assertion = UserAssertionHolder.get();

			if (assertion == null) return null;

			String cvr = assertion.getCVRNumberIdentifier();
			if (cvr == null) return null;

			String cvrRid = assertion.getSubject();
			if (cvrRid == null) return null;

			String cpr = ridHelper.getCPR(cvrRid);
			if (cpr == null) return null;

			user = userDao.get().find(cvr, cpr);
		}
		catch (SoapException e) {

			// This is crazy API design, but if the user cannot looked up, an
			// exception if thrown because of the SOAP fault.

			logger.warn("RID2CPR lookup failed.", e);
		}
		catch (Exception e) {

			logger.error("Error while fetching user CPR from the RID2CPR service.", e);
		}

		return user;
	}
}
