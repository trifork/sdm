package com.trifork.stamdata.replication.replication.views.sor;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import com.trifork.stamdata.replication.replication.views.View;


@Entity(name = "sor/sygehusafdeling/v1")
@Table(name = "SygehusAfdeling")
public class SygehusAfdeling extends View {

	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "SygeHusAfdelingPID")
	private BigInteger recordID;

	@Column(name = "SorNummer")
	protected BigInteger sorNummer;

	@Column(name = "SygehusSorNummer")
	protected BigInteger sygehusSorNummer;

	@Column(name = "OverAfdelingSorNummer")
	protected BigInteger overafdelingSorNummer;

	@Column(name = "UnderlagtSygehusSorNummer")
	protected BigInteger underlagtSygehusSorNummer;

	@Column(name = "EanLokationsnummer")
	protected BigInteger eanLokationsnummer;

	@Column(name = "AfdelingTypeKode")
	protected BigInteger afdelingTypeKode;

	@Column(name = "AfdelingTypeTekst")
	protected String afdelingTypeTekst;

	@Column(name = "HovedSpecialeKode")
	protected String hovedSpecialeKode;

	@Column(name = "HovedSpecialeTekst")
	protected String hovedSpecialeTekst;

	@Column(name = "Nummer")
	protected BigInteger nummer;

	@Column(name = "Navn")
	protected String navn;

	@Column(name = "Telefon")
	protected String telefon;

	@Column(name = "Vejnavn")
	protected String vejnavn;

	@Column(name = "Postnummer")
	protected String postnummer;

	@Column(name = "Bynavn")
	protected String bynavn;

	@Column(name = "Email")
	protected String email;

	@Column(name = "Www")
	protected String www;

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
	Date validTo;

	@Override
	public BigInteger getRecordID() {

		return recordID;
	}

	@Override
	public String getId() {

		return sorNummer.toString();
	}

	@Override
	public Date getUpdated() {

		return modifiedDate;
	}
}
