package com.trifork.stamdata.registre.takst;

import javax.persistence.Column;


public class Indholdsstoffer extends TakstRecord {

	private Long drugID; // Ref. t. LMS01
	private Long varenummer; // Ref. t. LMS02

	private String stofklasse;
	private String substansgruppe;

	private String substans; // Kun aktive substanser.


	public Long getDrugID() {

		return this.drugID;
	}


	public void setDrugID(Long drugID) {

		this.drugID = drugID;
	}


	public Long getVarenummer() {

		return this.varenummer;
	}


	public void setVarenummer(Long varenummer) {

		this.varenummer = varenummer;
	}


	@Column
	public String getStofklasse() {

		return this.stofklasse;
	}


	public void setStofklasse(String stofklasse) {

		this.stofklasse = stofklasse;
	}


	@Column
	public String getSubstansgruppe() {

		return this.substansgruppe;
	}


	public void setSubstansgruppe(String substansgruppe) {

		this.substansgruppe = substansgruppe;
	}


	@Column
	public String getSubstans() {

		return this.substans;
	}


	public void setSubstans(String substans) {

		this.substans = substans;
	}


	@Override
	public String getKey() {

		return substans + "-" + substansgruppe + "-" + stofklasse + "-" + drugID;
	}


	@Override
	public boolean equals(Object o) {

		if (o.getClass() != Indholdsstoffer.class) return false;
		Indholdsstoffer stof = (Indholdsstoffer) o;
		return getKey().equals(stof.getKey());

	}

}