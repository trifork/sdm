package com.trifork.sdm.replication.admin.models;

public class User {
	
	private final String id;
	private final String name;
	private final String cvr;
	private final String cpr;

	public User(String id, String name, String cpr, String cvr) {
		
		this.id = id;
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
}
