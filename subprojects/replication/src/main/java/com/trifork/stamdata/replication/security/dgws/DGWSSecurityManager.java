package com.trifork.stamdata.replication.security.dgws;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.trifork.stamdata.replication.replication.views.Views.checkViewIntegrity;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.MULTILINE;
import java.security.SecureRandom;
import java.sql.SQLException;
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

	@Inject
	DGWSSecurityManager(AuthorizationDao authorizationDao) {

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

		if (authorizationDao.isClientAuthorized(cvr, Views.getViewName(viewClass))) {

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
	public boolean authorize(HttpServletRequest request) {

		checkNotNull(request);

		// TODO: Cache the ~20 most recent tokens. We can use a priority queue
		// for efficiency.

		byte[] token = parseAuthenticationToken(request.getHeader("Authentication"));

		if (token == null) return false;

		// AUTHORIZE

		return authorizationDao.isTokenStillValid(token);
	}

	protected byte[] parseAuthenticationToken(String header) {

		if (header == null) return null;

		Matcher matcher = authenticationRegex.matcher(header);

		return matcher.find() ? Base64.decode(matcher.group(1)) : null;
	}
}
