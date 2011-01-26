package com.trifork.stamdata.registre.takst;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.trifork.stamdata.XmlName;
import com.trifork.stamdata.XmlOrder;


@Entity
public class Pakningsstoerrelsesenhed extends TakstRecord {

	private final DivEnheder enheder;


	public Pakningsstoerrelsesenhed(DivEnheder enheder) {

		this.enheder = enheder;
	}


	@Id
	@Column
	@XmlOrder(1)
	@XmlName("kode")
	public String getPakningsstoerrelsesenhedKode() {

		return enheder.getKode();
	}


	@Column
	@XmlOrder(2)
	@XmlName("tekst")
	public String getPakningsstoerrelsesenhedTekst() {

		return enheder.getTekst();
	}


	@Override
	public String getKey() {

		return enheder.getKode();
	}
}
