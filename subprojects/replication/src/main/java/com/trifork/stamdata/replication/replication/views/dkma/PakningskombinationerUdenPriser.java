package com.trifork.stamdata.replication.replication.views.dkma;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.*;
import com.trifork.stamdata.replication.replication.views.View;
import com.trifork.stamdata.replication.util.Namespace;


@Entity(name = "dkma/pakningskombinationudenpriser/v1")
@Table(name = "PakningskombinationerUdenPriser")
@XmlRootElement(namespace = Namespace.STAMDATA_3_0)
@XmlAccessorType(XmlAccessType.FIELD)
public class PakningskombinationerUdenPriser extends View {

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
