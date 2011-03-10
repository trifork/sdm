package com.trifork.sdm.replication.replication.models.dkma;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import com.trifork.sdm.replication.replication.models.Record;
import com.trifork.sdm.replication.util.Namespace;

@Entity(name = "dkma/pakningskombinationudenpriser/v1")
@Table(name = "PakningskombinationerUdenPriser")
@XmlRootElement(namespace = Namespace.STAMDATA_3_0)
@XmlAccessorType(XmlAccessType.FIELD)
public class PakningskombinationerUdenPriser extends Record
{
	@Id
	@GeneratedValue
	@Column(name = "PakningskombinationerUdenPriserPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "CID")
	protected String id;

	@Column(name = "VarenummerOrdineret")
	protected BigInteger varenummerOrdineret;

	@Column(name = "VarenummerSubstitueret")
	protected BigInteger varenummerSubstitueret;

	@Column(name = "VarenummerAlternativt")
	protected BigInteger varenummerAlternativt;

	@Column(name = "AntalPakninger")
	protected BigInteger antalPakninger;

	@Column(name = "InformationspligtMarkering")
	protected String informationspligtMarkering;

	// Metadata

	@XmlTransient
	@Column(name = "ModifiedDate")
	private Date modifiedDate;

	@Column(name = "ValidFrom")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validFrom;

	@Column(name = "ValidTo")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validTo;
	public String getID()
	{
		return id.toString();
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
