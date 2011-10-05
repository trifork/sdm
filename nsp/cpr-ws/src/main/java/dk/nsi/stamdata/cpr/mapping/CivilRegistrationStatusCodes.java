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
package dk.nsi.stamdata.cpr.mapping;

import java.util.List;

import com.google.common.collect.ImmutableList;


public interface CivilRegistrationStatusCodes
{
	static final String LIVING_ABROAD = "20";
	static final String CANCELLED = "30";
	static final String DELETED = "50";
	static final String CHANGED = "60";
	static final String MISSING = "70";
	static final String DEAD = "90";

	static final List<String> STATUSES_WITH_NO_ADDRESS = ImmutableList.of(
			LIVING_ABROAD,
			CANCELLED,
			DELETED,
			CHANGED,
			MISSING,
			DEAD
	);

}
