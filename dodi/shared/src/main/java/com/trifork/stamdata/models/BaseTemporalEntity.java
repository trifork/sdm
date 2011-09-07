package com.trifork.stamdata.models;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;

@MappedSuperclass
public abstract class BaseTemporalEntity implements TemporalEntity
{
	private Date validFrom, validTo;

	@Column
	@Temporal(TIMESTAMP)
	public Date getValidFrom()
	{
		return validFrom;
	}

	public void setValidFrom(Date validFrom)
	{
		this.validFrom = validFrom;
	}

	@Column
	@Temporal(TIMESTAMP)
	public Date getValidTo()
	{
		return validTo;
	}

	public void setValidTo(Date validTo)
	{
		this.validTo = validTo;
	}
}
