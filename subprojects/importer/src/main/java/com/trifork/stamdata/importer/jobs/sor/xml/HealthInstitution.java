package com.trifork.stamdata.importer.jobs.sor.xml;


import java.util.Date;


public class HealthInstitution extends AddressInformation
{
	private Long sorIdentifier;
	private String entityName;
	private Long institutionType;
	private String pharmacyIdentifier;
	private String shakIdentifier;
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


	public Long getInstitutionType()
	{
		return institutionType;
	}


	public void setInstitutionType(Long institutionType)
	{
		this.institutionType = institutionType;
	}


	public String getPharmacyIdentifier()
	{
		return pharmacyIdentifier;
	}


	public void setPharmacyIdentifier(String pharmacyIdentifier)
	{
		this.pharmacyIdentifier = pharmacyIdentifier;
	}


	public String getShakIdentifier()
	{
		return shakIdentifier;
	}


	public void setShakIdentifier(String shakIdentifier)
	{
		this.shakIdentifier = shakIdentifier;
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
