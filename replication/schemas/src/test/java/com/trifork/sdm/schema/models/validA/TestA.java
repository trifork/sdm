package com.trifork.sdm.schema.models.validA;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.trifork.stamdata.Versioned;


@Entity
public class TestA
{
	/**
	 * This property is placed at the top because we need to test that the
	 * order of the elements in the generated schema is alphabetical.
	 * 
	 * DO NOT MOVE.
	 */
	@Column
	public boolean getB()
	{
		return true;
	}
	
	
	@Column
	public String getA()
	{
		return "FAKE";
	}
	

	@Column
	public Date getC()
	{
		return new Date();
	}


	@Column
	public int getD()
	{
		return 1234;
	}


	@Column
	public long getE()
	{
		return 1234l;
	}


	/**
	 * This element should not be output, because supported versions is by
	 * default {1}.
	 */
	@Column
	@Versioned({2})
	public int getF()
	{
		return 1234;
	}


	/**
	 * This element should not be output because it is not annotated with
	 * @Column.
	 */
	public int getG()
	{
		return 1234;
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
