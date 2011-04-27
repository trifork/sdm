
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

package com.trifork.stamdata.replication.gui.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
public class User {

	@Id
	@GeneratedValue
	private String id;

	private String name;
	private String cvr;
	private String cpr;

	protected User() {

	}

	public User(String name, String cpr, String cvr) {

		this.name = name;
		this.cpr = cpr;
		this.cvr = cvr;
	}

	public String getId() {

		return id;
	}

	public String getName() {

		return name;
	}

	public String getCvr() {

		return cvr;
	}

	public String getCpr() {

		return cpr;
	}

	@Override
	public String toString() {

		return String.format("%s (cpr=%s, cvr=%s)", name, cpr, cvr);
	}
}
