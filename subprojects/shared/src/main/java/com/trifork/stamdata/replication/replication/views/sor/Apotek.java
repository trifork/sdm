// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
//
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
//
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
//
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
//
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package com.trifork.stamdata.replication.replication.views.sor;

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

import com.trifork.stamdata.replication.replication.views.View;
import com.trifork.stamdata.replication.replication.views.ViewPath;


@Entity
@XmlRootElement
@ViewPath("sor/apotek/v1")
public class Apotek extends View {

	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "ApotekPID")
	private BigInteger recordID;

	@Column(name = "SorNummer")
	protected BigInteger sorNummer;

	@Column(name = "ApotekNummer")
	protected BigInteger apotekNummer;

	@Column(name = "FilialNummer")
	protected BigInteger filialNummer;

	@Column(name = "EanLokationsnummer")
	protected BigInteger eanLokationsnummer;

	@Column(name = "cvr")
	protected BigInteger cvr;

	@Column(name = "pcvr")
	protected BigInteger pcvr;

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
	@Temporal(TIMESTAMP)
	protected Date modifiedDate;

	@XmlTransient
	@Column(name = "ValidFrom")
	@Temporal(TIMESTAMP)
	protected Date validFrom;

	@XmlTransient
	@Column(name = "ValidTo")
	@Temporal(TIMESTAMP)
	protected Date validTo;

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
