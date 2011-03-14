package com.trifork.stamdata.replication.gui.models;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
public class Client {

	@Id
	@GeneratedValue
	private String id;

	private String name;
	private String cvr;

	@ElementCollection
	private Set<String> permissions;
	
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
