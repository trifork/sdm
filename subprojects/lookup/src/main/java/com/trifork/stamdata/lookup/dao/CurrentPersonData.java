package com.trifork.stamdata.lookup.dao;

import java.math.BigInteger;
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
	
	public String getLokalitet() {
		return person.lokalitet;
	}

	public String getVejnavn() {
		return person.vejnavn;
	}

	public String getHusnummer() {
		return person.husnummer;
	}

	public String getBygningsnummer() {
		return person.bygningsnummer;
	}

	public String getEtage() {
		return person.etage;
	}

	public String getSidedoernummer() {
		return person.sideDoerNummer;
	}

	public String getBynavn() {
		return person.bynavn;
	}

	public BigInteger getPostnummer() {
		return person.postnummer;
	}

	public String getPostdistrikt() {
		return person.postdistrikt;
	}

}
