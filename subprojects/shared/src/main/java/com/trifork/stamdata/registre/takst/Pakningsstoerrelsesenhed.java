package com.trifork.stamdata.registre.takst;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class Pakningsstoerrelsesenhed extends TakstRecord {

	private final DivEnheder enheder;


	public Pakningsstoerrelsesenhed(DivEnheder enheder) {

		this.enheder = enheder;
	}


	@Id
	@Column
	public String getPakningsstoerrelsesenhedKode() {

		return enheder.getKode();
	}


	@Column
	public String getPakningsstoerrelsesenhedTekst() {

		return enheder.getTekst();
	}


	@Override
	public String getKey() {

		return enheder.getKode();
	}

}
