// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// Contributor(s): Contributors are attributed in the source code
// where applicable.
// 
// The Original Code is "Stamdata".
// 
// The Initial Developer of the Original Code is Trifork Public A/S.
// 
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
// 
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

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

	public String getEfternavn() {

		return efternavn;
	}

	protected String uddannelseskode;

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
