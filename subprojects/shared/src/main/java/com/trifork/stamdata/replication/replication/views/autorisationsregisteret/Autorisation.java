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

package com.trifork.stamdata.replication.replication.views.autorisationsregisteret;

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
@ViewPath("autorisationsregisteret/autorisation/v1")
public class Autorisation extends View {

	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "AutorisationPID")
	private BigInteger recordID;

	protected String autorisationsnummer;

	public String getAutorisationsnummer() {

		return autorisationsnummer;
	}

	protected String cpr;

	public String getCPR() {

		return cpr;
	}

	protected String fornavn;

	public String getFornave() {

		return fornavn;
	}

	protected String efternavn;

	protected String uddannelsesKode;
	public String getEfternavn() {

		return efternavn;
	}

	public String getUddannelseskode() {

		return uddannelseskode;
	}

	@Temporal(TIMESTAMP)
	protected Date validFrom;

	public Date getValidFrom() {

		return validFrom;
	}

	@Temporal(TIMESTAMP)
	protected Date validTo;

	public Date getValidTo() {

		return validTo;
	}

	@XmlTransient
	@Temporal(TIMESTAMP)
	private Date modifiedDate;

	@Override
	public BigInteger getRecordID() {

		return recordID;
	}

	@Override
	public String getId() {

		return autorisationsnummer;
	}

	@Override
	public Date getUpdated() {

		return modifiedDate;
	}
}
