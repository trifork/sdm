package com.trifork.stamdata.registre.sor;


import java.util.Date;

import javax.persistence.*;

import com.trifork.stamdata.*;


@Entity
public class Yder extends AbstractRecord
{
	private String nummer;
	private Long eanLokationsnummer;
	private String telefon;
	private String navn;
	private String vejnavn;
	private String postnummer;
	private String bynavn;
	private String email;
	private String www;
	private Long hovedSpecialeKode;
	private String hovedSpecialeTekst;
	private Long sorNummer;
	private Long praktisSorNummer;
	private Date validFrom;
	private Date validTo;


	@Column
	@XmlOrder(1)
	public String getNummer()
	{
		return nummer;
	}


	public void setNummer(String nummer)
	{
		this.nummer = nummer;
	}


	@Column
	@XmlOrder(2)
	public Long getEanLokationsnummer()
	{

		return eanLokationsnummer;
	}


	public void setEanLokationsnummer(Long eanLokationsnummer)
	{

		this.eanLokationsnummer = eanLokationsnummer;
	}


	@Column
	@XmlOrder(3)
	public String getTelefon()
	{

		return telefon;
	}


	public void setTelefon(String telefon)
	{

		this.telefon = telefon;
	}


	@Column
	@XmlOrder(4)
	public String getNavn()
	{

		return navn;
	}


	public void setNavn(String navn)
	{

		this.navn = navn;
	}


	@Column
	@XmlOrder(5)
	public String getVejnavn()
	{

		return vejnavn;
	}


	public void setVejnavn(String vejnavn)
	{

		this.vejnavn = vejnavn;
	}


	@Column
	@XmlOrder(6)
	public String getPostnummer()
	{

		return postnummer;
	}


	public void setPostnummer(String postnummer)
	{

		this.postnummer = postnummer;
	}


	@Column
	@XmlOrder(7)
	public String getBynavn()
	{
		return bynavn;
	}


	public void setBynavn(String bynavn)
	{

		this.bynavn = bynavn;
	}


	@Column
	@XmlOrder(8)
	public String getEmail()
	{

		return email;
	}


	public void setEmail(String email)
	{

		this.email = email;
	}


	@Column
	@XmlOrder(9)
	public String getWww()
	{

		return www;
	}


	public void setWww(String www)
	{

		this.www = www;
	}


	@Column
	@XmlOrder(10)
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
	@XmlOrder(11)
	@XmlName("hovedspecialetekst")
	public String getHovedSpecialeTekst()
	{

		return hovedSpecialeTekst;
	}


	public void setHovedSpecialeTekst(String hovedSpecialeTekst)
	{
		this.hovedSpecialeTekst = hovedSpecialeTekst;
	}


	@Id
	@Column
	@XmlOrder(12)
	public Long getSorNummer()
	{
		return sorNummer;
	}


	public void setSorNummer(Long sorNummer)
	{

		this.sorNummer = sorNummer;
	}


	@Column
	@XmlOrder(13)
	public Long getPraksisSorNummer()
	{
		return praktisSorNummer;
	}


	public void setPraksisSorNummer(Long praktisSorNummer)
	{
		this.praktisSorNummer = praktisSorNummer;
	}


	@Override
	public Date getValidFrom()
	{
		return validFrom;
	}


	@Override
	public void setValidFrom(Date validFrom)
	{
		this.validFrom = validFrom;
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
