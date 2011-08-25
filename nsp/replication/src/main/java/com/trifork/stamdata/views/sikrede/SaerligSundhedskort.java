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

package com.trifork.stamdata.views.sikrede;

import static javax.persistence.TemporalType.DATE;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.views.View;
import com.trifork.stamdata.views.ViewPath;

@Entity
@XmlRootElement
@ViewPath("sikrede/saerligsundhedskort/v1")
@Table(name = "SaerligSundhedskort")
public class SaerligSundhedskort extends View
{
	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "SaerligSundhedskortPID")
	protected BigInteger recordID;

	@XmlElement(required = true)
	protected String cpr;

	@Temporal(TIMESTAMP)
	@XmlElement(required = true)
	protected Date validFrom;

	@Temporal(TIMESTAMP)
	@XmlElement(required = true)
	protected Date validTo;

	@XmlTransient
	@Temporal(TIMESTAMP)
	protected Date modifiedDate;

	protected String adresseLinje1;

	protected String adresseLinje2;

	protected String bopelsLand;

	protected String bopelsLandKode;

	protected String emailAdresse;

	protected String familieRelationCpr;

	@Temporal(DATE)
	protected Date foedselsDato;

	@Temporal(DATE)
	protected Date sskGyldigFra;

	@Temporal(DATE)
	protected Date sskGyldigTil;

	protected String mobilNummer;

	protected String postnummerBy;

	@Override
	public String getId()
	{
		return cpr;
	}

	@Override
	public BigInteger getRecordID()
	{
		return recordID;
	}

	@Override
	public Date getUpdated()
	{
		return modifiedDate;
	}
}
