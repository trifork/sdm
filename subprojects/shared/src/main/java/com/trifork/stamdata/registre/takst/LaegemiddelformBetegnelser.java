package com.trifork.stamdata.registre.takst;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.trifork.stamdata.XmlOrder;


@Entity(name = "formbetegnelse")
@Table(name = "Formbetegnelse")
public class LaegemiddelformBetegnelser extends TakstRecord
{
	// Ref. t. LMS01, felt 08
	private String kode;
	private String tekst;

	// A (Aktiv)=DLS o.l.-I (inaktiv)=Ikke
	// anerkendt term
	private String aktivInaktiv;


	@Id
	@Column
	@XmlOrder(1)
	public String getKode()
	{
		return this.kode;
	}


	public void setKode(String kode)
	{
		this.kode = kode;
	}


	@Column
	@XmlOrder(2)
	public String getTekst()
	{
		return this.tekst;
	}


	public void setTekst(String tekst)
	{
		this.tekst = tekst;
	}


	public Boolean getAktivInaktiv()
	{
		return "A".equalsIgnoreCase(this.aktivInaktiv);
	}


	public void setAktivInaktiv(String aktivInaktiv)
	{
		this.aktivInaktiv = aktivInaktiv;
	}


	@Override
	public String getKey()
	{
		return kode;
	}
}
