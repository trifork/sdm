package com.trifork.stamdata.importer.jobs.cpr;

import java.util.Date;

public class CPRMetaData
{
	private Date previousFileValidFrom;
	
	public Date getPreviousFileValidFrom()
	{
		return previousFileValidFrom;
	}

	public void setPreviousFileValidFrom(Date previousFileValidFrom)
	{
		this.previousFileValidFrom = previousFileValidFrom;
	}
}
