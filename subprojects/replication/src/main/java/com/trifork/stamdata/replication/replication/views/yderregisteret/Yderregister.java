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

package com.trifork.stamdata.replication.replication.views.yderregisteret;

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
@ViewPath("yderegisteret/yderregister/v1")
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
