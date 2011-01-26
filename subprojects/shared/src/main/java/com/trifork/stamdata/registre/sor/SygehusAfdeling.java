package com.trifork.stamdata.registre.sor;


import java.util.Date;

import javax.persistence.*;

import com.trifork.stamdata.*;


@Entity(name = "sygehusafdeling")
public class SygehusAfdeling extends AbstractRecord
{
	private String navn;
	private Long eanLokationsnummer;
	private String nummer;
	private String telefon;
	private String vejnavn;
	private String postnummer;
	private String bynavn;
	private String email;
	private String www;
	private Long afdelingTypeKode;
	private String afdelingTypeTekst;
	private Long hovedSpecialeKode;
	private String hovedSpecialeTekst;
	private Long sorNummer;
	private Long sygehusSorNummer;
	private Long overAfdelingSorNummer;
	private Long underlagtSygehusSorNummer;
	private Date validFrom;
	private Date validTo;


	@Column
	@XmlOrder(8)
	public String getNavn()
	{
		return navn;
	}


	public void setNavn(String navn)
	{

		this.navn = navn;
	}


	@Column
	@XmlOrder(4)
	public Long getEanLokationsnummer()
	{
		return eanLokationsnummer;
	}


	public void setEanLokationsnummer(Long eanLokationsnummer)
	{
		this.eanLokationsnummer = eanLokationsnummer;
	}


	@Column
	@XmlOrder(9)
	public String getNummer()
	{
		return nummer;
	}


	public void setNummer(String nummer)
	{
		this.nummer = nummer;
	}


	@Column
	@XmlOrder(10)
	public String getTelefon()
	{
		return telefon;
	}


	public void setTelefon(String telefon)
	{
		this.telefon = telefon;
	}


	@Column
	@XmlOrder(11)
	public String getVejnavn()
	{
		return vejnavn;
	}


	public void setVejnavn(String vejnavn)
	{
		this.vejnavn = vejnavn;
	}


	@Column
	@XmlOrder(12)
	public String getPostnummer()
	{
		return postnummer;
	}


	public void setPostnummer(String postnummer)
	{
		this.postnummer = postnummer;
	}


	@Column
	@XmlOrder(3)
	public String getBynavn()
	{
		return bynavn;
	}


	public void setBynavn(String bynavn)
	{
		this.bynavn = bynavn;
	}


	@Column
	@XmlOrder(5)
	public String getEmail()
	{
		return email;
	}


	public void setEmail(String email)
	{
		this.email = email;
	}


	@Column
	@XmlOrder(13)
	public String getWww()
	{
		return www;
	}


	public void setWww(String www)
	{
		this.www = www;
	}


	@Column
	@XmlName("typekode")
	@XmlOrder(1)
	public Long getAfdelingTypeKode()
	{
		return afdelingTypeKode;
	}


	public void setAfdelingTypeKode(Long afdelingTypeKode)
	{
		this.afdelingTypeKode = afdelingTypeKode;
	}


	@Column
	@XmlOrder(6)
	@XmlName("hovedspecialekode")
	public Long getHovedSpecialeKode()
	{
		return hovedSpecialeKode;
	}


	public void setHovedSpecialeKode(Long hovedSpecialeKode)
	{

		this.hovedSpecialeKode = hovedSpecialeKode;
	}


	@Column
	@XmlOrder(7)
	@XmlName("hovedspecialetekst")
	public String getHovedSpecialeTekst()
	{

		return hovedSpecialeTekst;
	}


	public void setHovedSpecialeTekst(String hovedSpecialeTekst)
	{

		this.hovedSpecialeTekst = hovedSpecialeTekst;
	}


	@Column
	@XmlOrder(2)
	@XmlName("typetekst")
	public String getAfdelingTypeTekst()
	{

		return afdelingTypeTekst;
	}


	public void setAfdelingTypeTekst(String afdelingTypeTekst)
	{

		this.afdelingTypeTekst = afdelingTypeTekst;
	}


	@Id
	@Column
	@XmlOrder(14)
	@XmlName("sornummer")
	public Long getSorNummer()
	{

		return sorNummer;
	}


	public void setSorNummer(Long sorNummer)
	{

		this.sorNummer = sorNummer;
	}


	@Column
	@XmlOrder(15)
	@XmlName("sygehusSornummer")
	public Long getSygehusSorNummer()
	{

		return sygehusSorNummer;
	}


	public void setSygehusSorNummer(Long sygehusSorNummer)
	{
		this.sygehusSorNummer = sygehusSorNummer;
	}


	@Column
	@XmlOrder(16)
	@XmlName("overafdelingSornummer")
	public Long getOverAfdelingSorNummer()
	{

		return overAfdelingSorNummer;
	}


	public void setOverAfdelingSorNummer(Long overAfdelingSorNummer)
	{

		this.overAfdelingSorNummer = overAfdelingSorNummer;
	}


	@Column
	@XmlOrder(17)
	public Long getUnderlagtSygehusSorNummer()
	{
		return underlagtSygehusSorNummer;
	}


	public void setUnderlagtSygehusSorNummer(Long underlagtSygehusSorNummer)
	{
		this.underlagtSygehusSorNummer = underlagtSygehusSorNummer;
	}


	@Override
	public void setValidFrom(Date validFrom)
	{
		this.validFrom = validFrom;
	}


	@Override
	public Date getValidFrom()
	{
		return validFrom;
	}


	@Override
	public Date getValidTo()
	{
		return (validTo != null) ? validTo : DateUtils.FOREVER;
	}


	public void setValidTo(Date validTo)
	{
		this.validTo = validTo;
	}
}
