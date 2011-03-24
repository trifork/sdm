package com.trifork.stamdata.replication.replication.views.yderregisteret;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import com.trifork.stamdata.replication.replication.views.View;


@Entity(name = "yderegisteret/yderregister/v1")
@Table(name = "Yderregister")
public class Yderregister extends View {

	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "YderregisterPID")
	private BigInteger recordID;

	@Column(name = "Nummer")
	protected BigInteger nummer;

	@Column(name = "Telefon")
	protected String telefon;

	@Column(name = "Navn")
	protected String navn;

	@Column(name = "Vejnavn")
	protected String vejnavn;

	@Column(name = "Postnummer")
	protected String postnummer;

	@Column(name = "Bynavn")
	protected String bynavn;

	@Column(name = "AmtNummer")
	protected BigInteger amtNummer;

	@Column(name = "HistID")
	protected String histID;

	@Column(name = "Email")
	protected String email;

	@Column(name = "Www")
	protected String www;

	@Column(name = "HovedSpecialeKode")
	protected String hovedSpecialeKode;

	@Column(name = "HovedSpecialeTekst")
	protected String hovedSpecialeTekst;

	@XmlTransient
	@Column(name = "ModifiedDate")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date modifiedDate;

	@XmlTransient
	@Column(name = "ValidFrom")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validFrom;

	@XmlTransient
	@Column(name = "ValidTo")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validTo;

	@Override
	public BigInteger getRecordID() {

		return recordID;
	}

	@Override
	public String getId() {

		return nummer.toString();
	}

	@Override
	public Date getUpdated() {

		return modifiedDate;
	}
}
