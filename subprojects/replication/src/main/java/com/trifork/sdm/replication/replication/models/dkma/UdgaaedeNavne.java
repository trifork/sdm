package com.trifork.sdm.replication.replication.models.dkma;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import com.trifork.sdm.replication.replication.models.Record;
import com.trifork.sdm.replication.util.Namespace;

@Entity(name = "dkma/udgaaedenavne/v1")
@Table(name = "UdgaaedeNavne")
@XmlType(namespace = Namespace.STAMDATA_3_0)
@XmlAccessorType(XmlAccessType.FIELD)
public class UdgaaedeNavne extends Record
{
	@Id
	@GeneratedValue
	@Column(name = "UdgaaedeNavnePID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "CID")
	protected String id;

	@Column(name = "DrugID")
	protected BigInteger drugId;

	@Column(name = "DatoForAendringen")
	// TODO: Why String?
	protected String datoForAendringen;

	@Column(name = "TidligereNavn")
	protected String tidligereNavn;

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
