package com.trifork.sdm.replication.replication.models.dkma;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import com.trifork.sdm.replication.replication.models.Record;
import com.trifork.sdm.replication.util.Namespace;

@Entity(name = "dkma/drug/v1")
@Table(name = "Laegemiddel")
@XmlRootElement(namespace = Namespace.STAMDATA_3_0)
@XmlAccessorType(XmlAccessType.FIELD)
public class Drug extends Record
{
	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "LaegemiddelPID")
	protected BigInteger recordID;

	@Column(name = "DrugID")
	protected Long id;

	@Column(name = "DrugName")
	protected String name;

	@Column(name = "FormKode")
	protected String formCode;

	@Column(name = "FormTekst")
	protected String formDescription;

	@Column(name = "StyrkeTekst")
	protected String strengthDescription;

	@Column(name = "StyrkeNumerisk")
	protected Double stength;

	@Column(name = "StyrkeEnhed")
	protected String stengthUnit;

	@Column(name = "ATCKode")
	protected String atcID;

	@Column(name = "ATCTekst")
	protected String atcDescription;

	@Column(name = "Dosisdispenserbar")
	protected Boolean isDosageDispensable;

	@XmlTransient
	@Column(name = "ModifiedDate")
	protected Date modifiedDate;

	@Column(name = "ValidFrom")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validFrom;

	@Column(name = "ValidTo")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validTo;

	@Override
	public BigInteger getRecordID()
	{
		return recordID;
	}


	@Override
	public String getID()
	{
		return id.toString();
	}


	@Override
	public Date getUpdated()
	{
		return modifiedDate;
	}
}
