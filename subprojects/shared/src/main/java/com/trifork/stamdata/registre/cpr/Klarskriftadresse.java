package com.trifork.stamdata.registre.cpr;

import javax.persistence.*;

import com.trifork.stamdata.XmlName;
import com.trifork.stamdata.XmlOrder;


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
	@XmlOrder(4)
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
	@XmlOrder(3)
	public String getCoNavn() {

		return coNavn;
	}


	public void setCoNavn(String coNavn) {

		this.coNavn = coNavn;
	}


	@Column
	@XmlOrder(8)
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
	@XmlOrder(2)
	@XmlName("bynavn")
	public String getByNavn() {

		return byNavn;
	}


	public void setByNavn(String byNavn) {

		this.byNavn = byNavn;
	}


	@Column
	@XmlOrder(10)
	@XmlName("postnummer")
	public Long getPostNummer() {

		return postNummer;
	}


	public void setPostNummer(Long postNummer) {

		this.postNummer = postNummer;
	}


	@Column
	@XmlOrder(9)
	@XmlName("postdistrikt")
	public String getPostDistrikt() {

		return postDistrikt;
	}


	public void setPostDistrikt(String postDistrikt) {

		this.postDistrikt = postDistrikt;
	}


	@Column
	@XmlName("kommunekode")
	@XmlOrder(7)
	public Long getKommuneKode() {

		return kommuneKode;
	}


	public void setKommuneKode(Long kommuneKode) {

		this.kommuneKode = kommuneKode;
	}


	@Column
	@XmlOrder(12)
	@XmlName("vejkode")
	public Long getVejKode() {

		return vejKode;
	}


	public void setVejKode(Long vejKode) {

		this.vejKode = vejKode;
	}


	@Column
	@XmlName("husnummer")
	@XmlOrder(6)
	public String getHusNummer() {

		return husNummer;
	}


	public void setHusNummer(String husNummer) {

		this.husNummer = husNummer;
	}


	@Column
	@XmlOrder(5)
	public String getEtage() {

		return etage;
	}


	public void setEtage(String etage) {

		this.etage = etage;
	}


	@Column
	@XmlOrder(11)
	@XmlName("side")
	public String getSideDoerNummer() {

		return sideDoerNummer;
	}


	public void setSideDoerNummer(String sideDoerNummer) {

		this.sideDoerNummer = sideDoerNummer;
	}


	@Column
	@XmlOrder(1)
	@XmlName("bygningsnummer")
	public String getBygningsNummer() {

		return bygningsNummer;
	}


	public void setBygningsNummer(String bygningsNummer) {

		this.bygningsNummer = bygningsNummer;
	}


	@Column
	@XmlOrder(13)
	@XmlName("vejnavn")
	public String getVejNavn() {

		return vejNavn;
	}


	public void setVejNavn(String vejNavn) {

		this.vejNavn = vejNavn;
	}
}
