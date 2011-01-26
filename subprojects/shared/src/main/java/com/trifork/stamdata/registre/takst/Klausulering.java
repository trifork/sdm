package com.trifork.stamdata.registre.takst;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.trifork.stamdata.XmlOrder;


@Entity
public class Klausulering extends TakstRecord {

	private String kode; // Ref. t. LMS02, felt 13
	private String kortTekst; // Klausultekst, forkortet
	private String tekst; // Tilskudsklausul (sygdom/pensionist/kroniker)


	@Id
	@Column
	@XmlOrder(1)
	public String getKode() {

		return this.kode;
	}


	public void setKode(String kode) {

		this.kode = kode;
	}


	@Column
	@XmlOrder(2)
	public String getKortTekst() {

		return this.kortTekst;
	}


	public void setKortTekst(String kortTekst) {

		this.kortTekst = kortTekst;
	}


	@Column
	@XmlOrder(3)
	public String getTekst() {

		return this.tekst;
	}


	public void setTekst(String tekst) {

		this.tekst = tekst;
	}


	@Override
	public String getKey() {

		return "" + this.kode;
	}
}