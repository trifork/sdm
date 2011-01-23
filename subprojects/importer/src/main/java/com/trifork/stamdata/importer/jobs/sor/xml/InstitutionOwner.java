package com.trifork.stamdata.importer.jobs.sor.xml;


import java.util.Date;


public class InstitutionOwner extends AddressInformation
{

	private Long sorIdentifier;
	private String entityName;

	private Date fromDate;
	private Date toDate;


	public Long getSorIdentifier()
	{

		return sorIdentifier;
	}


	public void setSorIdentifier(Long sorIdentifier)
	{

		this.sorIdentifier = sorIdentifier;
	}


	public String getEntityName()
	{

		return entityName;
	}


	public void setEntityName(String entityName)
	{

		this.entityName = entityName;
	}


	public Date getFromDate()
	{

		return fromDate;
	}


	public void setFromDate(Date validFrom)
	{

		this.fromDate = validFrom;
	}


	public Date getToDate()
	{

		return toDate;
	}


	public void setToDate(Date toDate)
	{

		this.toDate = toDate;
	}
}
