package com.trifork.stamdata.registre.doseringsforslag;


import java.util.Date;

import javax.persistence.*;

import com.trifork.stamdata.*;


@Entity
@Documented("Indeholder versioneringsinformation.")
public class DosageVersion extends AbstractRecord
{

	// daDate: Dato for Apotekerforeningens mærkevaretakst, som datasættet er
	// udarbejdet på baggrund af. Obligatorisk. Dato, yyyy-MM-dd.
	private Date daDate;

	// lmsDate: Dato for Lægemiddelstyrelsens takst, som datasættet er
	// udarbejdet på baggrund af. Obligatorisk. Dato, yyyy-MM-dd.
	private Date lmsDate;

	// Dato filen er released. Obligatorisk. Dato, yyyy-MM-dd.
	private Date releaseDate;

	// Unikt release nummer. Obligatorisk. Heltal, 15 cifre.
	private long releaseNumber;

	private Date validFrom;


	@Column
	@XmlOrder(1)
	public Date getDaDate()
	{
		return daDate;
	}


	@Column
	@XmlOrder(2)
	public Date getLmsDate()
	{
		return lmsDate;
	}


	@Id
	@Column
	@XmlOrder(3)
	public Date getReleaseDate()
	{
		return releaseDate;
	}


	// Don't output this.
	// @Column(length=15)
	public long getReleaseNumber()
	{

		return releaseNumber;
	}


	public void setDaDate(Date daDate)
	{

		this.daDate = daDate;
	}


	public void setLmsDate(Date lmsDate)
	{

		this.lmsDate = lmsDate;
	}


	public void setReleaseDate(Date releaseDate)
	{

		this.releaseDate = releaseDate;
	}


	public void setReleaseNumber(long releaseNumber)
	{

		this.releaseNumber = releaseNumber;
	}


	@Override
	public void setValidFrom(Date validfrom)
	{

		this.validFrom = validfrom;
	}


	@Override
	public Date getValidFrom()
	{

		return validFrom;
	}
}
