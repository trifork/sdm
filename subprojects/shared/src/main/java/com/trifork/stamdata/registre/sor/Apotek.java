package com.trifork.stamdata.registre.sor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.trifork.stamdata.AbstractRecord;


@Entity
public class Apotek extends AbstractRecord {

	private long sorNummer;
	private Long apotekNummer;
	private long filialNummer;
	private Long eanLokationsnummer;
	private long cvr;
	private long pcvr;
	private String navn;
	private String telefon;
	private String vejnavn;
	private String postnummer;
	private String bynavn;
	private String email;
	private String www;


	@Id
	@Column
	public long getSorNummer() {

		return sorNummer;
	}


	public void setSorNummer(long sorNummer) {

		this.sorNummer = sorNummer;
	}


	@Column
	public Long getApotekNummer() {

		return apotekNummer;
	}


	public void setApotekNummer(Long apotekNummer) {

		this.apotekNummer = apotekNummer;
	}


	@Column
	public Long getFilialNummer() {

		return filialNummer;
	}


	public void setFilialNummer(long filialNummer) {

		this.filialNummer = filialNummer;
	}


	@Column
	public Long getEanLokationsnummer() {

		return eanLokationsnummer;
	}


	public void setEanLokationsnummer(Long eanLokationsnummer) {

		this.eanLokationsnummer = eanLokationsnummer;
	}


	@Column
	public long getCvr() {

		return cvr;
	}


	public void setCvr(long cvr) {

		this.cvr = cvr;
	}


	@Column
	public long getPcvr() {

		return pcvr;
	}


	public void setPcvr(long pcvr) {

		this.pcvr = pcvr;
	}


	@Column
	public String getNavn() {

		return navn;
	}


	public void setNavn(String navn) {

		this.navn = navn;
	}


	@Column
	public String getTelefon() {

		return telefon;
	}


	public void setTelefon(String telefon) {

		this.telefon = telefon;
	}


	@Column
	public String getVejnavn() {

		return vejnavn;
	}


	public void setVejnavn(String vejnavn) {

		this.vejnavn = vejnavn;
	}


	@Column
	public String getPostnummer() {

		return postnummer;
	}


	public void setPostnummer(String postnummer) {

		this.postnummer = postnummer;
	}


	@Column
	public String getBynavn() {

		return bynavn;
	}


	public void setBynavn(String bynavn) {

		this.bynavn = bynavn;
	}


	@Column
	public String getEmail() {

		return email;
	}


	public void setEmail(String email) {

		this.email = email;
	}


	@Column
	public String getWww() {

		return www;
	}


	public void setWww(String www) {

		this.www = www;
	}
}
