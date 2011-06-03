package com.trifork.stamdata.lookup.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.trifork.stamdata.views.cpr.BarnRelation;
import com.trifork.stamdata.views.cpr.Civilstand;
import com.trifork.stamdata.views.cpr.Foedselsregistreringsoplysninger;
import com.trifork.stamdata.views.cpr.Folkekirkeoplysninger;
import com.trifork.stamdata.views.cpr.Person;
import com.trifork.stamdata.views.cpr.Statsborgerskab;
import com.trifork.stamdata.views.cpr.Udrejseoplysninger;
import com.trifork.stamdata.views.cpr.UmyndiggoerelseVaergeRelation;

public class CurrentPersonData {

	private final Person person;
	private final Folkekirkeoplysninger folkekirkeoplysninger;
	private final Statsborgerskab statsborgerskab;
	private final Foedselsregistreringsoplysninger foedselsregistreringsoplysninger;
	private final Civilstand civilstand;
	private final Udrejseoplysninger udrejseoplysninger;
	private final UmyndiggoerelseVaergeRelation vaerge;
	private final List<UmyndiggoerelseVaergeRelation> vaergemaal;
	private final List<BarnRelation> boern;
	private final List<BarnRelation> foraeldre;

	public CurrentPersonData(Person person,
			Folkekirkeoplysninger folkekirkeoplysninger,
			Statsborgerskab statsborgerskab,
			Foedselsregistreringsoplysninger foedselsregistreringsoplysninger,
			Civilstand civilstand,
			Udrejseoplysninger udrejseoplysninger,
			UmyndiggoerelseVaergeRelation vaerge,
			List<UmyndiggoerelseVaergeRelation> vaergemaal,
			List<BarnRelation> boern,
			List<BarnRelation> foraeldre) {
		this.person = person;
		this.folkekirkeoplysninger = folkekirkeoplysninger;
		this.statsborgerskab = statsborgerskab;
		this.foedselsregistreringsoplysninger = foedselsregistreringsoplysninger;
		this.civilstand = civilstand;
		this.udrejseoplysninger = udrejseoplysninger;
		this.vaerge = vaerge;
		this.vaergemaal = vaergemaal;
		this.boern = boern;
		this.foraeldre = foraeldre;
	}
	
	public String getFornavn() {
		return person.fornavn;
	}

	public String getMellemnavn() {
		return person.mellemnavn;
	}

	public String getEfternavn() {
		return person.efternavn;
	}

	public Date getValidFrom() {
		return person.getValidFrom();
	}

	public String getCprNumber() {
		return person.getCpr();
	}
	
	public String getKoen() {
		return person.koen;
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

	public String getKommuneKode() {
		if(person == null || person.kommuneKode == null) {
			return null;
		}
		return person.kommuneKode.toString();
	}

	public String getVejKode() {
		if(person == null || person.vejKode == null) {
			return null;
		}
		return person.vejKode.toString();
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
	
	public Civilstand getCivilstand() {
		return civilstand;
	}
	public String getCivilstandskode() {
		if(civilstand == null) {
			return null;
		}
		return civilstand.civilstandskode;
	}
	
	public String getAegtefaelleCpr() {
		if(civilstand == null) {
			return null;
		}
		return civilstand.aegtefaellePersonnummer;
	}

	public Date getSeparationsdato() {
		return civilstand.separation;
	}

	public Udrejseoplysninger getUdrejseoplysninger() {
		return udrejseoplysninger;
	}
	
	public UmyndiggoerelseVaergeRelation getVaerge() {
		return vaerge;
	}
	
	public List<UmyndiggoerelseVaergeRelation> getVaergemaal() {
		List<UmyndiggoerelseVaergeRelation> empty = Collections.emptyList();
		return vaergemaal == null ? empty : vaergemaal;
	}

	public Date getFoedselsdato() {
		return person.foedselsdato;
	}
	
	public Date getNavnebeskyttelsesstartdato() {
		return person.navneBeskyttelsestartdato;
	}
	public Date getNavnebeskyttelsesslettedato() {
		return person.navnebeskyttelseslettedato;
	}
	
	public List<String> getBoernCpr() {
		List<String> result = new ArrayList<String>();
		if(boern == null) {
			return result;
		}
		for(BarnRelation barnRelation : boern) {
			result.add(barnRelation.barnCPR);
		}
		return result;
	}
	
	public List<String> getForaeldreCpr() {
		List<String> result = new ArrayList<String>();
		if(boern == null) {
			return result;
		}
		for(BarnRelation barnRelation : foraeldre) {
			result.add(barnRelation.getCpr());
		}
		return result;
	}
}
