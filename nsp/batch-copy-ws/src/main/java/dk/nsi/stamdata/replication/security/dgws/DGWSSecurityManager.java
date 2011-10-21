/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */

package dk.nsi.stamdata.replication.security.dgws;

import static com.trifork.stamdata.Preconditions.checkNotNull;
import static dk.nsi.stamdata.views.Views.checkViewIntegrity;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.MULTILINE;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;

import dk.nsi.stamdata.replication.models.Client;
import dk.nsi.stamdata.replication.models.ClientDao;
import dk.nsi.stamdata.replication.security.SecurityManager;
import dk.nsi.stamdata.views.View;
import dk.nsi.stamdata.views.Views;

import org.apache.commons.codec.binary.Base64;

/**
 * Manage all aspects of authentication and authorization for DGWS Token.
 * 
 * @author Thomas Børlum <thb@trifork.com>
 */
@RequestScoped
public class DGWSSecurityManager implements SecurityManager
{
	private static final Pattern authenticationRegex = Pattern.compile("STAMDATA (.*)", MULTILINE | CASE_INSENSITIVE);
	private static final SecureRandom random = new SecureRandom();

	private final AuthorizationDao authorizationDao;
	private ClientDao clientDao;

	@Inject
	DGWSSecurityManager(AuthorizationDao authorizationDao, ClientDao clientDao)
	{
		this.clientDao = checkNotNull(clientDao);
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
	public String issueAuthenticationToken(String cvr, Class<? extends View> viewClass, Date expiryDate)
	{
		checkNotNull(cvr);
		checkNotNull(expiryDate);
		checkViewIntegrity(viewClass);

		String authorization = null;
		
		Client client = clientDao.findByCvr(cvr);

		if (client != null && client.isAuthorizedFor(Views.getViewPath(viewClass)))
		{
			byte[] token = new byte[512];
			random.nextBytes(token);

			authorizationDao.save(new Authorization(viewClass, cvr, expiryDate, token));

            authorization = asString(new Base64().encode(token));
        }
		
		return authorization;
	}

    private String asString(byte[] tokenInBase64) {
        String authorization;
        try {
            authorization = new String(tokenInBase64, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("utf-8 not support", e);
        }
        return authorization;
    }

    @Override
	public boolean isAuthorized(HttpServletRequest request) {

		checkNotNull(request);

		byte[] token = authenticationToken(request);

		if (token == null) return false;

		// AUTHORIZE
		//
		// Fetch the view name from the URL and check the token validity.

		String viewName = request.getPathInfo().substring(1);
		
		return authorizationDao.isTokenValid(token, viewName);
	}

	@Override
	public String getClientId(HttpServletRequest request)
	{
		return "CVR:" + authorizationDao.findCvr(authenticationToken(request));
	}

	protected byte[] authenticationToken(HttpServletRequest request)
	{
		String header = request.getHeader("Authentication");
		if (header == null) return null;

		Matcher matcher = authenticationRegex.matcher(header);
		return null;
	}
}
