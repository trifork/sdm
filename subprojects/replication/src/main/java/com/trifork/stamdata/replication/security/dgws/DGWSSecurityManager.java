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

package com.trifork.stamdata.replication.security.dgws;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.trifork.stamdata.replication.replication.views.Views.checkViewIntegrity;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.MULTILINE;

import java.security.SecureRandom;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
import com.trifork.stamdata.replication.replication.views.View;
import com.trifork.stamdata.replication.replication.views.Views;
import com.trifork.stamdata.replication.security.SecurityManager;

import dk.sosi.seal.xml.Base64;


/**
 * Manage all aspects of authentication and authorization for DGWS Token.
 * 
 * @author Thomas Børlum (thomas@borlum.dk)
 */
public class DGWSSecurityManager implements SecurityManager {

	private static final Pattern authenticationRegex = Pattern.compile("STAMDATA (.*)", MULTILINE | CASE_INSENSITIVE);
	private static final SecureRandom random = new SecureRandom();

	private final AuthorizationDao authorizationDao;
	private final HttpServletRequest request;

	@Inject
	DGWSSecurityManager(AuthorizationDao authorizationDao, HttpServletRequest request) {
		this.request = request;
		this.authorizationDao = checkNotNull(authorizationDao);
	}

	/**
	 * Checks to see if the client has access to issues an access token if
	 * authorized.
	 * 
	 * The token is Base64 encoded and is suitable for placement in a HTTP
	 * header.
	 * 
	 * @param cvr
	 *            the client's CVR number.
	 * @param viewClass
	 *            the requested view, to authorize the client for.
	 * @param expiryDate
	 *            the time at which the authentication token will expire.
	 * 
	 * @return an authentication token, or null if the client is not authorized.
	 */
	public String issueAuthenticationToken(String cvr, Class<? extends View> viewClass, Date expiryDate) {

		checkNotNull(cvr);
		checkNotNull(expiryDate);
		checkViewIntegrity(viewClass);

		String authorization;

		if (authorizationDao.isClientAuthorized(cvr, Views.getViewPath(viewClass))) {

			byte[] token = new byte[512];
			random.nextBytes(token);

			authorizationDao.save(new Authorization(viewClass, cvr, expiryDate, token));

			authorization = Base64.encode(token);
		}
		else {
			authorization = null;
		}

		return authorization;
	}

	@Override
	public boolean isAuthorized() {

		checkNotNull(request);

		// TODO: Cache the ~20 most recent tokens. We can use a priority queue
		// for efficiency.

		byte[] token = authenticationToken();

		if (token == null) return false;

		// AUTHORIZE
		//
		// Fetch the view name from the URL and check the token validity.

		String viewName = request.getPathInfo().substring(1);
		
		return authorizationDao.isTokenValid(token, viewName);
	}

	@Override
	public String getClientId() {
		return "CVR:" + authorizationDao.findCvr(authenticationToken());
	}

	protected byte[] authenticationToken() {
		String header = request.getHeader("Authentication");
		if (header == null) return null;

		Matcher matcher = authenticationRegex.matcher(header);
		return matcher.find() ? Base64.decode(matcher.group(1)) : null;
	}
}
