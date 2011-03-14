package com.trifork.stamdata.replication.security;

import javax.servlet.http.HttpServletRequest;


/**
 * A unit that authorizes client HTTP request access to the system in some way.
 * 
 * Responsibilities:
 * 
 * <ul>
 * <il>Authenticate requests.</il>
 * <il>Authorize access based on the authentication and authorization level.</il>
 * </ul>
 * 
 * @author Thomas Børlum (thb@trifork.com)
 */
public interface SecurityManager {

	/**
	 * Authenticates the request and authorizes access to the requested resource
	 * if the user has the right access level.
	 * 
	 * @param request The HTTP request to authorize.
	 * @return true if the request is authorized.
	 */
	boolean authorize(HttpServletRequest request);
}
