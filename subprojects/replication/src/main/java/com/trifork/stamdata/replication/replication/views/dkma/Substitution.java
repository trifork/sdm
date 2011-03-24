package com.trifork.stamdata.replication.replication.views.dkma;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.replication.replication.annotations.ViewPath;
import com.trifork.stamdata.replication.replication.views.View;


@Entity
@XmlRootElement
@ViewPath("dkma/substitution/v1")
public class Substitution extends View {

	@Id
	@GeneratedValue
	@Column(name = "SubstitutionPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "ReceptensVarenummer")
	protected BigInteger receptvarenummer;

	@Column(name = "Substitutionsgruppenummer")
	protected BigInteger substitutionsgruppenummer;

	@Column(name = "NumeriskPakningsstoerrelse")
	protected BigInteger PakningsstoerrelseNumerisk;

	@Column(name = "ProdAlfabetiskeSekvensplads")
	protected String ProdAlfabetiskeSekvensplads;

	@Column(name = "SubstitutionskodeForPakning")
	protected String SubstitutionskodeForPakning;

	@Column(name = "BilligsteVarenummer")
	protected BigInteger billigsteVarenummer;

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

		return receptvarenummer.toString();
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
