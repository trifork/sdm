package com.trifork.stamdata.registre.takst;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class Styrkeenhed extends TakstRecord {

	private final DivEnheder enheder;


	public Styrkeenhed(DivEnheder enheder) {

		this.enheder = enheder;
	}


	@Id
	@Column
	public String getStyrkeenhedKode() {

		return enheder.getKode();
	}


	@Column
	public String getStyrkeenhedTekst() {

		return enheder.getTekst();
	}


	@Override
	public String getKey() {

		return enheder.getKode();
	}

}