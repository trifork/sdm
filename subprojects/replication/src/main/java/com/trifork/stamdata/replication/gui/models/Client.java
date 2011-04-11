// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package com.trifork.stamdata.replication.gui.models;

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
	private String cvr;

	@ElementCollection
	private Set<String> permissions = Sets.newHashSet();
	
	protected Client() {

	}

	public Client(String name, String cvr) {

		this.name = name;
		this.cvr = cvr;
		this.permissions = new HashSet<String>();
	}

	public String getId() {

		return id;
	}

	public String getName() {

		return name;
	}

	public String getCvrNumber() {

		return cvr;
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
	
		return String.format("%s (cvr=%s)", name, cvr);
	}
}
