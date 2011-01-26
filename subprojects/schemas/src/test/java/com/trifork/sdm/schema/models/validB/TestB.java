package com.trifork.sdm.schema.models.validB;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.trifork.stamdata.Versioned;


@Entity
@Versioned({ 1, 3 })
public class TestB
{
	@Column
	@Versioned({ 1 })
	public boolean getA()
	{
		return true;
	}


	@Column
	@Versioned({ 1, 2 })
	public long getB()
	{
		return 12345;
	}


	@Column
	@Versioned({ 3 })
	public String getC()
	{

		return "Test";
	}


	@Column
	@Versioned({ 2 })
	public Date getD()
	{
		return new Date();
	}


	public Date getValidTo()
	{
		return null;
	}


	public Date getValidFrom()
	{
		return null;
	}


	public Date getCreatedDate()
	{
		return null;
	}


	public Date getModifiedDate()
	{
		return null;
	}
}
