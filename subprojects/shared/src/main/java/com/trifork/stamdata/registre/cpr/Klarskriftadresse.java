package com.trifork.stamdata.registre.cpr;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "Person")
public class Klarskriftadresse extends CPRRecord {
	String cpr;
	String adresseringsNavn;
	String coNavn;
	String Lokalitet;
	String standardAdresse;
	String byNavn;
	Long postNummer;
	String postDistrikt;
	Long kommuneKode;
	Long vejKode;
	String husNummer;
	String etage;
	String sideDoerNummer;
	String bygningsNummer;
	String vejNavn;


	@Id
	@Column
	@Override
	public String getCpr() {

		return super.getCpr();
	}


	public String getAdresseringsNavn() {

		return adresseringsNavn;
	}


	public void setAdresseringsNavn(String adresseringsNavn) {

		this.adresseringsNavn = adresseringsNavn;
	}


	@Column
	public String getCoNavn() {

		return coNavn;
	}


	public void setCoNavn(String coNavn) {

		this.coNavn = coNavn;
	}


	@Column
	public String getLokalitet() {

		return Lokalitet;
	}


	public void setLokalitet(String lokalitet) {

		Lokalitet = lokalitet;
	}


	public String getStandardAdresse() {

		return standardAdresse;
	}


	public void setStandardAdresse(String standardAdresse) {

		this.standardAdresse = standardAdresse;
	}


	@Column
	public String getByNavn() {

		return byNavn;
	}


	public void setByNavn(String byNavn) {

		this.byNavn = byNavn;
	}


	@Column
	public Long getPostNummer() {

		return postNummer;
	}


	public void setPostNummer(Long postNummer) {

		this.postNummer = postNummer;
	}


	@Column
	public String getPostDistrikt() {

		return postDistrikt;
	}


	public void setPostDistrikt(String postDistrikt) {

		this.postDistrikt = postDistrikt;
	}


	@Column
	public Long getKommuneKode() {

		return kommuneKode;
	}


	public void setKommuneKode(Long kommuneKode) {

		this.kommuneKode = kommuneKode;
	}


	@Column
	public Long getVejKode() {

		return vejKode;
	}


	public void setVejKode(Long vejKode) {

		this.vejKode = vejKode;
	}


	@Column
	public String getHusNummer() {

		return husNummer;
	}


	public void setHusNummer(String husNummer) {

		this.husNummer = husNummer;
	}


	@Column
	public String getEtage() {

		return etage;
	}


	public void setEtage(String etage) {

		this.etage = etage;
	}


	@Column
	public String getSideDoerNummer() {

		return sideDoerNummer;
	}


	public void setSideDoerNummer(String sideDoerNummer) {

		this.sideDoerNummer = sideDoerNummer;
	}


	@Column
	public String getBygningsNummer() {

		return bygningsNummer;
	}


	public void setBygningsNummer(String bygningsNummer) {

		this.bygningsNummer = bygningsNummer;
	}


	@Column
	public String getVejNavn() {

		return vejNavn;
	}


	public void setVejNavn(String vejNavn) {

		this.vejNavn = vejNavn;
	}
}
