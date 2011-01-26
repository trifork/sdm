package com.trifork.stamdata.registre.sor;


import javax.persistence.*;

import com.trifork.stamdata.*;


@Entity
public class Apotek extends AbstractRecord
{
	private long sorNummer;
	private Long apotekNummer;
	private long filialNummer;
	private Long eanLokationsnummer;
	private long cvr;
	private long pcvr;
	private String navn;
	private String telefon;
	private String vejnavn;
	private String postnummer;
	private String bynavn;
	private String email;
	private String www;


	@Id
	@Column
	@XmlOrder(1)
	@XmlName("sornummer")
	public long getSorNummer()
	{

		return sorNummer;
	}


	public void setSorNummer(long sorNummer)
	{

		this.sorNummer = sorNummer;
	}


	@Column
	@XmlOrder(2)
	@XmlName("apoteksnummer")
	public Long getApotekNummer()
	{

		return apotekNummer;
	}


	public void setApotekNummer(Long apotekNummer)
	{

		this.apotekNummer = apotekNummer;
	}


	@Column
	@XmlOrder(3)
	@XmlName("filialnummer")
	public Long getFilialNummer()
	{

		return filialNummer;
	}


	public void setFilialNummer(long filialNummer)
	{
		this.filialNummer = filialNummer;
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
	@XmlOrder(5)
	public long getCvr()
	{

		return cvr;
	}


	public void setCvr(long cvr)
	{

		this.cvr = cvr;
	}


	@Column
	@XmlOrder(6)
	public long getPcvr()
	{

		return pcvr;
	}


	public void setPcvr(long pcvr)
	{

		this.pcvr = pcvr;
	}


	@Column
	@XmlOrder(7)
	public String getNavn()
	{

		return navn;
	}


	public void setNavn(String navn)
	{

		this.navn = navn;
	}


	@Column
	@XmlOrder(8)
	public String getTelefon()
	{

		return telefon;
	}


	public void setTelefon(String telefon)
	{

		this.telefon = telefon;
	}


	@Column
	@XmlOrder(9)
	public String getVejnavn()
	{

		return vejnavn;
	}


	public void setVejnavn(String vejnavn)
	{

		this.vejnavn = vejnavn;
	}


	@Column
	@XmlOrder(10)
	public String getPostnummer()
	{

		return postnummer;
	}


	public void setPostnummer(String postnummer)
	{

		this.postnummer = postnummer;
	}


	@Column
	@XmlOrder(11)
	public String getBynavn()
	{

		return bynavn;
	}


	public void setBynavn(String bynavn)
	{
		this.bynavn = bynavn;
	}


	@Column
	@XmlOrder(12)
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
}
