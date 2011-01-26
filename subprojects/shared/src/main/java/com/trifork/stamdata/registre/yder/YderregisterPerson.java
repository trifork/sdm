package com.trifork.stamdata.registre.yder;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.trifork.stamdata.*;


@Entity
public class YderregisterPerson extends AbstractRecord {
	private String nummer;
	private String histIdPerson;
	private String cpr;
	private Long personrolleKode;
	private String personrolleTxt;
	private Date tilgangDato;
	private Date afgangDato;


	@Id
	@Column
	@XmlOrder(1)
	public String getId() {

		return nummer + "-" + cpr;
	}


	@Column
	@XmlOrder(2)
	public String getNummer() {

		return nummer;
	}


	public void setNummer(String nummer) {

		this.nummer = nummer;
	}


	@Column
	@XmlOrder(3)
	public String getHistIdPerson() {

		return histIdPerson;
	}


	public void setHistIdPerson(String histIdPerson) {

		this.histIdPerson = histIdPerson;
	}


	@Column
	@XmlOrder(4)
	public String getCpr() {

		return cpr;
	}


	public void setCpr(String cpr) {

		this.cpr = cpr;
	}


	@Column
	@XmlOrder(5)
	public Long getPersonrolleKode() {

		return personrolleKode;
	}


	public void setPersonrolleKode(Long personrolleKode) {

		this.personrolleKode = personrolleKode;
	}


	@Column
	@XmlOrder(6)
	public String getPersonrolleTxt() {

		return personrolleTxt;
	}


	public void setPersonrolleTxt(String personrolleTxt) {

		this.personrolleTxt = personrolleTxt;
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

		if (afgangDato != null) return afgangDato;

		return DateUtils.FOREVER;
	}
}
