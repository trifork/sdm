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
