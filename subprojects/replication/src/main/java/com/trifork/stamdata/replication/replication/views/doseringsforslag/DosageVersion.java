package com.trifork.stamdata.replication.replication.views.doseringsforslag;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.*;
import com.trifork.stamdata.replication.replication.views.View;


@Entity(name = "doseringsforslag/version/v1")
@Documented("Indeholder versioneringsinformation.")
public class DosageVersion extends View {

	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "DosageVersionPID")
	protected BigInteger id;

	// Dato for Apotekerforeningens mærkevaretakst, som datasættet er
	// udarbejdet på baggrund af. Obligatorisk. Dato, yyyy-MM-dd.
	protected Date daDate;

	// Dato for Lægemiddelstyrelsens takst, som datasættet er
	// udarbejdet på baggrund af. Obligatorisk. Dato, yyyy-MM-dd.
	protected Date lmsDate;

	// Dato filen er released. Obligatorisk. Dato, yyyy-MM-dd.
	protected Date releaseDate;

	// Unikt release nummer. Obligatorisk. Heltal, 15 cifre.
	protected long releaseNumber;

	@Column(name="ValidFrom")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validFrom;

	@Column(name="ModifiedDate")
	@Temporal(TemporalType.TIMESTAMP)
	@XmlTransient
	protected Date modifiedDate;

	@Override
	public String getId() {

		return Long.toString(releaseDate.getTime());
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
