
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

package com.trifork.stamdata.replication.models;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.google.common.collect.Sets;


@Entity
public class Client {

	@Id
	@GeneratedValue
	private String id;

	private String name;
	private String subjectSerialNumber;

	@ElementCollection
	private Set<String> permissions = Sets.newHashSet();
	
	protected Client() {

	}

	public Client(String name, String subjectSerialNumber) {

		this.name = name;
		this.subjectSerialNumber = subjectSerialNumber;
		this.permissions = new HashSet<String>();
	}

	public String getId() {

		return id;
	}

	public String getName() {

		return name;
	}

	public String getSubjectSerialNumber() {

		return subjectSerialNumber;
	}

	public boolean addPermission(String viewName) {

		return permissions.add(viewName);
	}

	public boolean removePermission(String viewName) {

		return permissions.remove(viewName);
	}

	public boolean isAuthorizedFor(String viewName) {

		return permissions.contains(viewName);
	}

	public Set<String> getPermissions() {

		return Collections.unmodifiableSet(permissions);
	}
	
	@Override
	public String toString() {
	
		return String.format("%s (subjectSerialNumber=%s)", name, subjectSerialNumber);
	}
}
