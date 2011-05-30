package com.trifork.stamdata.lookup.dao;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;

import com.trifork.stamdata.views.cpr.Civilstand;
import com.trifork.stamdata.views.cpr.Foedselsregistreringsoplysninger;
import com.trifork.stamdata.views.cpr.Folkekirkeoplysninger;
import com.trifork.stamdata.views.cpr.Person;
import com.trifork.stamdata.views.cpr.Statsborgerskab;

public class CurrentPersonData {

	private final Person person;
	private final Folkekirkeoplysninger folkekirkeoplysninger;
	private final Statsborgerskab statsborgerskab;
	private final Foedselsregistreringsoplysninger foedselsregistreringsoplysninger;
	private final Civilstand civilstand;

	public CurrentPersonData(Person person, Folkekirkeoplysninger folkekirkeoplysninger, Statsborgerskab statsborgerskab, Foedselsregistreringsoplysninger foedselsregistreringsoplysninger, Civilstand civilstand) {
		this.person = person;
		this.folkekirkeoplysninger = folkekirkeoplysninger;
		this.statsborgerskab = statsborgerskab;
		this.foedselsregistreringsoplysninger = foedselsregistreringsoplysninger;
		this.civilstand = civilstand;
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
	public boolean getMedlemAfFolkekirken() {
		return folkekirkeoplysninger != null &&
		Arrays.asList("F", "M", "S").contains(folkekirkeoplysninger.forholdsKode);
	}
	public String getStatsborgerskab() {
		if(statsborgerskab == null) {
			return null;
		}
		return statsborgerskab.landekode;
	}
	public String getFoedselsregistreringsstedkode() {
		if(foedselsregistreringsoplysninger == null) {
			return null;
		}
		return foedselsregistreringsoplysninger.foedselsregistreringsstedkode;
	}
	public String getFoedselsregistreringstekst() {
		if(foedselsregistreringsoplysninger == null) {
			return null;
		}
		return foedselsregistreringsoplysninger.foedselsregistreringstekst;
	}
	public String getCivilstandskode() {
		if(civilstand == null) {
			return null;
		}
		return civilstand.civilstandskode;
	}
}
