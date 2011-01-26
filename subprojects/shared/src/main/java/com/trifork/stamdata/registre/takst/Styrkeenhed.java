package com.trifork.stamdata.registre.takst;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.trifork.stamdata.XmlName;
import com.trifork.stamdata.XmlOrder;


@Entity
public class Styrkeenhed extends TakstRecord {

	private final DivEnheder enheder;


	public Styrkeenhed(DivEnheder enheder) {

		this.enheder = enheder;
	}


	@Id
	@Column
	@XmlName("kode")
	@XmlOrder(1)
	public String getStyrkeenhedKode() {

		return enheder.getKode();
	}


	@Column
	@XmlName("tekst")
	@XmlOrder(2)
	public String getStyrkeenhedTekst() {

		return enheder.getTekst();
	}


	@Override
	public String getKey() {

		return enheder.getKode();
	}

}