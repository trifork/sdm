package com.trifork.stamdata.persistence;

import java.util.Date;

import com.trifork.stamdata.importer.persistence.*;


@Output
public class PetEntity implements Record
{
	private final String name;
	private String address;

	public PetEntity(String name, String address)
	{
		this.name = name;
		this.address = address;
	}

	@Id
	@Output
	public String getName()
	{
		return name;
	}

	@Output
	public String getAddress()
	{
		return address;
	}
	
	public void setAddress(String value)
	{
		address = value;
	}

	@Override
	public Object getKey()
	{
		return name;
	}

	@Override
	public Date getValidFrom()
	{
		return null;
	}

	@Override
	public Date getValidTo()
	{
		return null;
	}
}
