package com.trifork.stamdata.registre.yder;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.trifork.stamdata.AbstractRecord;
import com.trifork.stamdata.DateUtils;


@Entity
public class Yderregister extends AbstractRecord {

	private String nummer;
	private String telefon;
	private String navn;
	private String vejnavn;
	private String postnummer;
	private String bynavn;
	private int amtNummer;
	private String email;
	private String www;
	private String hovedSpecialeKode;
	private String hovedSpecialeTekst;
	private String histID;
	private Date tilgangDato;
	private Date afgangDato;


	@Id
	@Column
	public String getNummer() {

		return nummer;
	}


	public void setNummer(String nummer) {

		this.nummer = nummer;
	}


	@Column
	public String getTelefon() {

		return telefon;
	}


	public void setTelefon(String telefon) {

		this.telefon = telefon;
	}


	@Column
	public String getNavn() {

		return navn;
	}


	public void setNavn(String navn) {

		this.navn = navn;
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
	public int getAmtNummer() {

		return amtNummer;
	}


	public void setAmtNummer(int amtNummer) {

		this.amtNummer = amtNummer;
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


	@Column
	public String getHovedSpecialeKode() {

		return hovedSpecialeKode;
	}


	public void setHovedSpecialeKode(String hovedSpecialeKode) {

		this.hovedSpecialeKode = hovedSpecialeKode;
	}


	@Column
	public String getHovedSpecialeTekst() {

		return hovedSpecialeTekst;
	}


	public void setHovedSpecialeTekst(String hovedSpecialeTekst) {

		this.hovedSpecialeTekst = hovedSpecialeTekst;
	}


	@Column
	public String getHistID() {

		return histID;
	}


	public void setHistID(String histID) {

		this.histID = histID;
	}


	public Date getTilgangDato() {

		return tilgangDato;
	}


	public void setTilgangDato(Date tilgangDato) {

		this.tilgangDato = tilgangDato;
	}


	public Date getAfgangDato() {

		return afgangDato;
	}


	public void setAfgangDato(Date afgangDato) {

		this.afgangDato = afgangDato;
	}


	@Override
	public Date getValidFrom() {

		return tilgangDato;
	}


	@Override
	public Date getValidTo() {

		Date validTo;

		if (afgangDato != null)
			validTo = afgangDato;
		else
			validTo = DateUtils.FOREVER;

		return validTo;
	}

}
