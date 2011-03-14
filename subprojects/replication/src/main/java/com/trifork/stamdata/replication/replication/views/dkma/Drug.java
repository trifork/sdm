package com.trifork.stamdata.replication.replication.views.dkma;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import com.trifork.stamdata.replication.replication.views.View;


@Entity(name = "dkma/laegemiddel/v1")
@Table(name = "Laegemiddel")
public class Drug extends View {

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
	public BigInteger getRecordID() {

		return recordID;
	}

	@Override
	public String getId() {

		return id.toString();
	}

	@Override
	public Date getUpdated() {

		return modifiedDate;
	}
}
