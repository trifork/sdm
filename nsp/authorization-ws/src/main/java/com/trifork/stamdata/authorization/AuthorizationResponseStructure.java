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

package com.trifork.stamdata.authorization;

import static com.trifork.stamdata.Preconditions.checkNotNull;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "cpr", "firstName", "lastName", "authorizations" })
public class AuthorizationResponseStructure {

	protected AuthorizationResponseStructure() {
		
	}
	
	@Id
	@GeneratedValue
	@XmlTransient
	protected BigInteger AutorisationPID;

	@XmlElement(required = true)
	protected String cpr;
	
	@XmlElement(required = false)
	protected String firstName;

	@XmlElement(required = false)
	protected String lastName;

	@XmlElement(name = "authorization", required = false)
	protected List<Authorization> authorizations;

	protected AuthorizationResponseStructure(String cpr, List<Authorization> authorizations) {

		this.cpr = checkNotNull(cpr);
		this.authorizations = checkNotNull(authorizations);
		
		if (!authorizations.isEmpty()) {
			
			firstName = checkNotNull(authorizations.get(0).firstName);
			lastName = checkNotNull(authorizations.get(0).lastName);
		}
	}
}
