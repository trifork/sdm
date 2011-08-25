package com.trifork.stamdata.views.sks;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.views.View;
import com.trifork.stamdata.views.ViewPath;

@Entity(name = "Organisation")
@XmlRootElement
@ViewPath("sks/institution/v1")
public class Institution extends View
{
	@Id
	@GeneratedValue
	@Column(name = "OrganisationPID")
	@XmlTransient
	private BigInteger recordID;

	protected String navn;

	protected String nummer;

	protected String organisationstype;

	@XmlTransient
	@Temporal(TIMESTAMP)
	protected Date modifiedDate;

	@Temporal(TIMESTAMP)
	protected Date validFrom;

	@Temporal(TIMESTAMP)
	protected Date validTo;

	@Override
	public BigInteger getRecordID()
	{
		return recordID;
	}

	@Override
	public String getId()
	{
		return String.valueOf(nummer);
	}

	@Override
	public Date getUpdated()
	{
		return modifiedDate;
	}
}
