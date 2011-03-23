package com.trifork.stamdata.replication.replication.views.dkma;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.*;
import com.trifork.stamdata.replication.replication.views.View;
import com.trifork.stamdata.replication.util.Namespace;


@Entity(name = "dkma/udgaaedenavne/v1")
@XmlType(namespace = Namespace.STAMDATA_3_0)
@XmlAccessorType(XmlAccessType.FIELD)
public class UdgaaedeNavne extends View {

	@Id
	@GeneratedValue
	@Column(name = "UdgaaedeNavnePID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "CID")
	protected String id;

	@Column(name = "Drugid")
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

	@Override
	public String getId() {

		return id.toString();
	}

	@Override
	public Date getUpdated() {

		return modifiedDate;
	}

	@Override
	public BigInteger getRecordID() {

		return recordID;
	}
}
