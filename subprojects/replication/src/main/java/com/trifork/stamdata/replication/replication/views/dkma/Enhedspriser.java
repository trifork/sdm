package com.trifork.stamdata.replication.replication.views.dkma;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import com.trifork.stamdata.replication.replication.views.View;


@Entity(name = "dkma/enhedspriser/v1")
public class Enhedspriser extends View {

	@Id
	@GeneratedValue
	@Column(name = "EnhedspriserPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "Varenummer")
	protected BigInteger varenummer;
	
	@Column(name = "DrugID")
	protected BigInteger drugId;
	
	@Column(name = "PrisPrEnhed")
	protected BigInteger prisPrEnhed;
	
	@Column(name = "PrisPrDDD")
	protected BigInteger prisPrDDD;
	
	@Column(name = "BilligstePakning")
	protected String billigstePakning;

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

		return varenummer.toString();
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
