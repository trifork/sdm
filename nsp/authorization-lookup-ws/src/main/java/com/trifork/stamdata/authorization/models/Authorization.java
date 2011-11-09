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

package com.trifork.stamdata.authorization.models;

import static com.trifork.stamdata.Preconditions.checkNotNull;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "autreg")
public class Authorization {

	protected Authorization() {
		
	}

	public Authorization(String cpr, String givenName, String surname, String authorizationCode, String educationCode) {

		this.cpr = checkNotNull(cpr);
		this.firstName = checkNotNull(givenName);
		this.lastName = checkNotNull(surname);
		this.authorizationCode = checkNotNull(authorizationCode);
		this.educationCode = checkNotNull(educationCode);
	}

	@Id
	@GeneratedValue
	public BigInteger id;
	
	@Column(nullable = false, length = 10)
	public String cpr;

	@Column(name = "given_name", nullable = false, length = 50)
	public String firstName;

	@Column(name = "surname", nullable = false, length = 100)
	public String lastName;

	@Column(name = "aut_id", nullable = false, length = 5)
	public String authorizationCode;

	@Column(name = "edu_id", nullable = false, length = 4)
	public String educationCode;
}
