package com.trifork.sdm.schema.models.invalidC;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.trifork.sdm.schema.models.invalidD.TestD;


/**
 * All these properties will result in the same element name, and you should not
 * be allowed to generate schemas with a class like this. They will override
 * each other and an exception should be thrown.
 * 
 * @see TestD
 */
@Entity
public class TestC
{
	@Column
	public String getSomeString()
	{
		return "FAKE";
	}

	@Column
	public String someString()
	{
		return "FAKE";
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
