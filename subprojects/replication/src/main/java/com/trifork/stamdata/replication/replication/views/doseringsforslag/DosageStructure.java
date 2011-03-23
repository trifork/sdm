package com.trifork.stamdata.replication.replication.views.doseringsforslag;

import static com.trifork.stamdata.replication.util.Namespace.STAMDATA_3_0;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.Documented;
import com.trifork.stamdata.replication.replication.views.View;


@Entity(name = "doseringsforslag/dosagestructure/v1")
@XmlRootElement(namespace = STAMDATA_3_0)
@Documented("Indeholder doseringsstrukturer.")
public class DosageStructure extends View {

	@Id
	@GeneratedValue
	@Column(name = "DosageStructurePID")
	protected BigInteger id;

	// Unik kode for doseringstrukturen. Obligatorisk. Heltal, 11 cifre.
	@Column(length = 11)
	protected String code;

	// Reference til releaseNumber i Version. Obligatorisk. Heltal, 15 cifre.
	@Column(length = 15)
	protected long releaseNumber;

	// Typen af dosering, enten "M+M+A+N", "PN", "N daglig",
	// "Fritekst" eller "Kompleks". Obligatorisk. Streng, 100 tegn.
	@Column(length = 100)
	protected String type;

	// For simple typer (dvs. alt andet end "Kompleks")
	// indeholder feltet doseringen på simpel form. Optionelt. Streng, 100 tegn.
	@Column(length = 100)
	protected String simpleString;

	// For simple typer en eventuel supplerende tekst.
	// Optionelt. Streng, 200 tegn.
	@Column(length = 200)
	protected String supplementaryText;

	// FMKs strukturerede dosering i XML format. Bemærk at enkelte
	// værdier vil være escaped. Obligatorisk. Streng, 10000 tegn.
	@Column(length = 10000)
	protected String xml;

	// Såfremt det er muligt at lave en kort
	// doseringstekst på baggrund af xml og lægemidlets doseringsenhed vil
	// dette felt indeholde denne. Optionelt. Streng, 70 tegn.
	@Column(length = 70)
	protected String shortTranslation;

	// En lang doseringstekst baggrund af xml og
	// lægemidlets doseringsenhed. Obligatorisk. Streng 10000 tegn.
	@Column(length = 10000)
	protected String longTranslation;

	@Temporal(TIMESTAMP)
	protected Date validFrom;

	@XmlTransient
	@Temporal(TIMESTAMP)
	protected Date modifiedDate;

	@Override
	public String getId() {

		return code;
	}

	@Override
	public BigInteger getRecordID() {

		return id;
	}

	@Override
	public Date getUpdated() {

		return modifiedDate;
	}
}
