package com.trifork.stamdata.registre.takst;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.trifork.stamdata.XmlName;


@Entity
public class Administrationsvej extends TakstRecord
{
	private String kode; // Ref. t. LMS01, felt 16
	private String kortTekst;
	private String tekst;


	@Override
	@Id
	@Column(name = "AdministrationsvejKode")
	@XmlName("kode")
	public String getKey()
	{
		return kode;
	}


	public void setKode(String kode)
	{
		this.kode = kode;
	}


	public String getKortTekst()
	{
		return this.kortTekst;
	}


	public void setKortTekst(String kortTekst)
	{
		this.kortTekst = kortTekst;
	}


	@Column(name = "AdministrationsvejTekst")
	@XmlName("tekst")
	public String getTekst()
	{
		return this.tekst;
	}


	public void setTekst(String tekst)
	{
		this.tekst = tekst;
	}
}
