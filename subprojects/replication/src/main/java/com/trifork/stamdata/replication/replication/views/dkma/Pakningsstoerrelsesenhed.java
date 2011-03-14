package com.trifork.stamdata.replication.replication.views.dkma;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.*;
import com.trifork.stamdata.replication.replication.views.View;
import com.trifork.stamdata.replication.util.Namespace;


@Entity(name = "dkma/pakningsstoerrelsesenhed/v1")
@Table(name = "Pakningsstoerrelsesenhed")
@XmlRootElement(namespace = Namespace.STAMDATA_3_0)
@XmlAccessorType(XmlAccessType.FIELD)
public class Pakningsstoerrelsesenhed extends View {

	@Id
	@GeneratedValue
	@Column(name = "PakningsstoerrelsesenhedPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "PakningsstoerrelsesenhedKode")
	protected String id;

	@Column(name = "PakningsstoerrelsesenhedTekst")
	protected String tekst;

	@Column(name = "PakningsstoerrelsesenhedKortTekst")
	protected BigInteger kortTekst;

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
