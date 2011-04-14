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

package com.trifork.stamdata.replication.replication.views.dkma;

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

import com.trifork.stamdata.replication.replication.views.View;
import com.trifork.stamdata.replication.replication.views.ViewPath;


@Entity
@XmlRootElement
@Table(name = "Laegemiddel")
@ViewPath("dkma/drug/v1")
public class Drug extends View {

	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "LaegemiddelPID")
	protected BigInteger recordID;

	@Column(name = "DrugID")
	protected Long id;

	@Column(name = "DrugName")
	protected String nave;

	@Column(name = "FormKode")
	protected String formKode;

	@Column(name = "FormTekst")
	protected String formTekst;

	@Column(name = "StyrkeTekst")
	protected String styrkeTekst;

	@Column(name = "StyrkeNumerisk")
	protected Double styrke;

	@Column(name = "StyrkeEnhed")
	protected String styrkeenhed;

	@Column(name = "ATCKode")
	protected String atc;

	@Column(name = "ATCTekst")
	protected String atcTekst;

	@Column(name = "Dosisdispenserbar")
	protected Boolean dosisdispenserbar;

	@Column(name = "Varetype")
	protected String varetype;

	@Column(name = "Varedeltype")
	protected String varedeltype;

	@Column(name = "AlfabetSekvensplads")
	protected String alphabetSekvensplads;

	@Column(name = "SpecNummer")
	protected String specNummer;

	@Column(name = "LaegemiddelformTekst")
	protected String LaegemiddelformTekst; // TODO: What's the difference from formTekst?

	@Column(name = "KodeForYderligereFormOplysn")
	protected String kodeForYderligereFormOplysn;

	@Column(name = "Trafikadvarsel")
	protected Boolean trafikadvarsel;

	@Column(name = "Substitution")
	protected String substitution;

	@Column(name = "LaegemidletsSubstitutionsgruppe")
	protected String laegemidletsSubstitutionsgruppe;

	@Column(name = "DatoForAfregistrAfLaegemiddel")
	protected String datoForAfregistrAfLaegemiddel;

	@Column(name = "Karantaenedato")
	protected String karantaenedato;

	@Column(name = "AdministrationsvejKode")
	protected String administrationsvejKode;

	@Column(name = "MTIndehaverKode")
	protected BigInteger mtIndehaverKode;

	@Column(name = "RepraesentantDistributoerKode")
	protected BigInteger repraesentantDistributoerKode;

	@XmlTransient
	@Column(name = "ModifiedDate")
	protected Date modifiedDate;

	@Column(name = "ValidFrom")
	@Temporal(TIMESTAMP)
	protected Date validFrom;

	@Column(name = "ValidTo")
	@Temporal(TIMESTAMP)
	protected Date validTo;

	@Override
	public BigInteger getRecordID() {

		return recordID;
	}

	@Override
	public String getId() {

		return id.toString();
	}

	@Override
	public Date getUpdated() {

		return modifiedDate;
	}
}
