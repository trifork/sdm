package com.trifork.stamdata.registre.takst;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.trifork.stamdata.XmlName;
import com.trifork.stamdata.XmlOrder;


@Entity
public class Dosering extends TakstRecord
{
	private Long doseringKode; // Ref. t. LMS27
	private String doseringKortTekst;
	private String doseringstekstTotal; // Felt 05 + 06 + 07
	private Long antalEnhDoegn;
	private String doseringstekstLinie1;
	private String doseringstekstLinie2;
	private String doseringstekstLinie3;
	private String aktivInaktiv; // A = Aktiv kode. I = Inaktiv kode (bør ikke
									// anvendes)


	@Id
	@Column
	@XmlOrder(1)
	@XmlName("kode")
	public Long getDoseringKode()
	{

		return this.doseringKode;
	}


	public void setDoseringskode(Long doseringskode)
	{

		this.doseringKode = doseringskode;
	}


	public String getDoseringKortTekst()
	{

		return this.doseringKortTekst;
	}


	public void setDoseringKortTekst(String doseringKortTekst)
	{

		this.doseringKortTekst = doseringKortTekst;
	}


	@Column(name = "DoseringTekst")
	@XmlOrder(2)
	@XmlName("tekst")
	public String getDoseringstekstTotal()
	{
		return this.doseringstekstTotal;
	}


	public void setDoseringstekstTotal(String value)
	{
		doseringstekstTotal = value;
	}


	@Column(name = "AntalEnhederPrDoegn")
	@XmlName("enhederPerDoegn")
	@XmlOrder(3)
	public Double getAntalEnhDoegn()
	{
		return this.antalEnhDoegn / 1000.0;
	}


	public void setAntalEnhDoegn(Long value)
	{
		this.antalEnhDoegn = value;
	}


	public String getDoseringstekstLinie1()
	{
		return this.doseringstekstLinie1;
	}


	public void setDoseringstekstLinie1(String doseringstekstLinie1)
	{
		this.doseringstekstLinie1 = doseringstekstLinie1;
	}


	public String getDoseringstekstLinie2()
	{
		return this.doseringstekstLinie2;
	}


	public void setDoseringstekstLinie2(String doseringstekstLinie2)
	{
		this.doseringstekstLinie2 = doseringstekstLinie2;
	}


	public String getDoseringstekstLinie3()
	{
		return this.doseringstekstLinie3;
	}


	public void setDoseringstekstLinie3(String doseringstekstLinie3)
	{
		this.doseringstekstLinie3 = doseringstekstLinie3;
	}


	@Column(name = "Aktiv")
	@XmlOrder(4)
	public Boolean getAktivInaktiv()
	{
		return "A".equalsIgnoreCase(this.aktivInaktiv);
	}


	public void setAktivInaktiv(String aktivInaktiv)
	{
		this.aktivInaktiv = aktivInaktiv;
	}


	@Override
	public Long getKey()
	{
		return this.doseringKode;
	}
}
