/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */


package com.trifork.stamdata.views.dkma;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.views.View;
import com.trifork.stamdata.views.ViewPath;

@Entity
@XmlRootElement
@Table(name = "Laegemiddel")
@ViewPath("dkma/drug/v1")
public class Drug extends View
{

	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "LaegemiddelPID")
	protected BigInteger recordID;

	@Column(name = "DrugID")
	protected Long id;

	@Column(name = "DrugName")
	protected String nave;

	protected String formKode;

	protected String formTekst;

	protected String styrkeTekst;

	@Column(name = "StyrkeNumerisk")
	protected Double styrke;

	protected String styrkeenhed;

	@Column(name = "ATCKode")
	protected String atc;

	protected String atcTekst;

	protected Boolean dosisdispenserbar;

	protected String varetype;

	protected String varedeltype;

	protected String alfabetSekvensplads;

	protected String specNummer;

	protected String LaegemiddelformTekst;

	protected String kodeForYderligereFormOplysn;

	protected Boolean trafikadvarsel;

	protected String substitution;

	protected String laegemidletsSubstitutionsgruppe;

	protected String datoForAfregistrAfLaegemiddel;

	protected String karantaenedato;

	protected String administrationsvejKode;

	protected BigInteger mtIndehaverKode;

	protected BigInteger repraesentantDistributoerKode;

	@XmlTransient
	@Temporal(TIMESTAMP)
	protected Date modifiedDate;

	@Temporal(TIMESTAMP)
	protected Date validFrom;

	@Temporal(TIMESTAMP)
	protected Date validTo;

	@Override
	public BigInteger getRecordID()
	{
		return recordID;
	}

	@Override
	public String getId()
	{
		return id.toString();
	}

	@Override
	public Date getUpdated()
	{
		return modifiedDate;
	}
}
