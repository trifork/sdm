package com.trifork.stamdata;


import static java.lang.String.format;

import java.lang.reflect.Method;
import java.util.Date;


/**
 * Convenience super-class that implements the Record interface.
 */
public abstract class AbstractRecord implements Record
{
	private long pid = -1l;

	private Date validFrom = null;
	private Date validTo = null;

	private Date modifiedDate;


	@Override
	public long getPID()
	{

		return pid;
	}


	@Override
	public void setPID(long pid)
	{

		this.pid = pid;
	}


	@Override
	public void setModifiedDate(Date modifiedDate)
	{

		this.modifiedDate = modifiedDate;
	}


	@Override
	public Date getModifiedDate()
	{

		return modifiedDate;
	}


	@Override
	public void setValidFrom(Date validfrom)
	{

		this.validFrom = validfrom;
	}


	@Override
	public Date getValidFrom()
	{

		return validFrom != null ? validFrom : DateUtils.PAST;
	}


	@Override
	public Date getValidTo()
	{

		return validTo != null ? validTo : DateUtils.FOREVER;
	}


	@Override
	public void setValidTo(Date validTo)
	{

		this.validTo = validTo;
	}


	@Override
	public Object getKey()
	{

		// TODO: This method should not really be part of this class.

		Method id = EntityHelper.getIdMethod(getClass());

		try
		{
			return id.invoke(this);
		}
		catch (Exception e)
		{
			throw new RuntimeException(format(
				"Error getting id for object of class '%s'.", getClass()
						.getSimpleName()));
		}
	}
}
