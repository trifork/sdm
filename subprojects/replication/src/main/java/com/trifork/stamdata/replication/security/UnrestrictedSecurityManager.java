package com.trifork.stamdata.replication.security;

import javax.servlet.http.HttpServletRequest;

/**
 * This security manager does not restrict access in any way.
 * 
 * @author Thomas Børlum (thb@trifork.com)
 */
public class UnrestrictedSecurityManager implements SecurityManager {

	@Override
	public boolean authorize(HttpServletRequest request) {

		return true;
	}
}
