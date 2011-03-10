package com.trifork.sdm.replication.replication.models.dkma;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import com.trifork.sdm.replication.replication.models.Record;
import com.trifork.sdm.replication.util.Namespace;

@Entity(name = "dkma/beregningsregler/v1")
@Table(name = "ATC")
@XmlRootElement(namespace = Namespace.STAMDATA_3_0)
@XmlAccessorType(XmlAccessType.FIELD)
public class Beregningsregler extends Record
{
	@Id
	@GeneratedValue
	@Column(name = "BeregningsreglerPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "Kode")
	protected String code;

	@Column(name = "Tekst")
	protected String text;

	@XmlTransient
	@Column(name = "ModifiedDate")
	private Date modifiedDate;

	@XmlTransient
	@Column(name = "ValidFrom")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validFrom;

	@XmlTransient
	@Column(name = "ValidTo")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validTo;


	@Override
	public String getID()
	{
		return code;
	}


	@Override
	public Date getUpdated()
	{
		return modifiedDate;
	}


	@Override
	public BigInteger getRecordID()
	{
		return recordID;
	}
}
