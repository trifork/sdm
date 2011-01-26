package com.trifork.stamdata.registre.sor;


import java.util.Date;

import javax.persistence.*;

import com.trifork.stamdata.*;


@Entity
public class Praksis extends AbstractRecord
{
	private Date validFrom;
	private String navn;
	private Long eanLokationsnummer;
	private Long regionCode;
	private Long sorNummer;
	private Date validTo;


	public Praksis()
	{

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
	@XmlName("regionskode")
	public Long getRegionCode()
	{

		return regionCode;
	}


	public void setRegionCode(Long regionCode)
	{

		this.regionCode = regionCode;
	}


	@Id
	@Column
	@XmlOrder(1)
	@XmlName("sornummer")
	public Long getSorNummer()
	{

		return sorNummer;
	}


	public void setSorNummer(Long sorNummer)
	{

		this.sorNummer = sorNummer;
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
