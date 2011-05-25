package com.trifork.stamdata.lookup.dao;

import java.util.Date;

import com.trifork.stamdata.views.cpr.Person;

public class CurrentPersonData {

	private final Person person;

	public CurrentPersonData(Person person) {
		this.person = person;
	}
	
	public Date getValidFrom() {
		return person.validFrom;
	}

	public String getCprNumber() {
		return person.cpr;
	}

}
