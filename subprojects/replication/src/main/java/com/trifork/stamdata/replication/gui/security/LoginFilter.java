// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package com.trifork.stamdata.replication.gui.security;

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
		User currentUser = getCurrentUser();

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

	public User getCurrentUser() {

		User user = null;

		// HACK: Since the RID2CPR client is not working,
		// we have to hack in a user.

		return get();

		/*
		 * 
		 * try { // User assertion has an unfortunate name.
		 * 
		 * // TODO: Do logging for these error cases. This is actually // quite
		 * important since it is a bit foggy which properties // are set on the
		 * assertions from different IdP's.
		 * 
		 * UserAssertion assertion = UserAssertionHolder.get(); if (assertion ==
		 * null) return null;
		 * 
		 * String cvr = assertion.getCVRNumberIdentifier(); if (cvr == null)
		 * return null;
		 * 
		 * String cvrRid = assertion.getSubject(); if (cvrRid == null) return
		 * null;
		 * 
		 * String cpr = ridHelper.getCPR(cvrRid); if (cpr == null) return null;
		 * 
		 * user = userDao.get().find(cvr, cpr); } catch (SoapException e) {
		 * 
		 * // This is crazy API design, but if the user cannot // looked up, an
		 * exception if thrown because of the // SOAP fault.
		 * 
		 * logger.warn("RID2CPR lookup failed.", e); } catch (Exception e) {
		 * 
		 * logger.error("Error while fetching user CPR from the RID2CPR service."
		 * , e); }
		 * 
		 * return user;
		 */
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

		return userDao.get().findAll().get(0);
	}
}
