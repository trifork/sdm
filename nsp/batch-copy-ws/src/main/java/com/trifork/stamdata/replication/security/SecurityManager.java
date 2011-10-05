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
public interface SecurityManager
{
	/**
	 * @return true if the request is authorized.
	 */
	boolean isAuthorized(HttpServletRequest request);
	
	/**
	 * @return a unique identification of the client.
	 */
	String getClientId(HttpServletRequest request);
}
