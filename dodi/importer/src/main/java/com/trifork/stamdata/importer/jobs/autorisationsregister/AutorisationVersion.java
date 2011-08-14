package com.trifork.stamdata.importer.jobs.autorisationsregister;

import java.util.Date;

import com.trifork.stamdata.importer.persistence.*;


@Output
public class AutorisationVersion extends AbstractStamdataEntity
{
	private final Date releaseDate;

	public AutorisationVersion(Date releaseDate)
	{
		this.releaseDate = releaseDate;
	}

	@Id
	@Output
	public Date getReleaseDate()
	{
		return this.releaseDate;
	}

	@Override
	public Date getValidFrom()
	{
		return null;
	}
}
