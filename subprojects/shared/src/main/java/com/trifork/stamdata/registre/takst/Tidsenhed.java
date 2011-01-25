package com.trifork.stamdata.registre.takst;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.trifork.stamdata.XmlName;


@Entity
public class Tidsenhed extends TakstRecord
{

	private final DivEnheder enheder;


	public Tidsenhed(DivEnheder enheder)
	{
		this.enheder = enheder;
	}


	@Id
	@Column
	@XmlName("kode")
	public String getTidsenhedKode()
	{
		return enheder.getKode();
	}


	@Column
	public String getTidsenhedTekst()
	{
		return enheder.getTekst();
	}


	@Override
	public String getKey()
	{
		return enheder.getKode();
	}
}
