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

package com.trifork.stamdata.views.cpr;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.views.View;
import com.trifork.stamdata.views.ViewPath;


@Entity
@XmlRootElement
@ViewPath("cpr/umyndiggoerelsevaergerelation/v1")
public class UmyndiggoerelseVaergeRelation extends View {

	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "UmyndiggoerelseVaergeRelationPID")
	private BigInteger recordID;

	@Column(name = "Id")
	@XmlElement(required = true)
	protected String id;

	@XmlElement(required = true)
	@Column(name = "CPR")
	protected String cpr;

	@Column(name = "TypeKode")
	protected String typeKode;

	@Column(name = "TypeTekst")
	protected String typeTekst;

	@Column(name = "RelationCpr")
	protected String relationCpr;

	@Column(name = "RelationCprStartDato")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date relationCprStartDato;

	@Column(name = "VaergesNavn")
	protected String vaergesNavn;

	@Column(name = "VaergesNavnStartDato")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date vaergesNavnStartDato;

	@Column(name = "relationsTekst1")
	protected String RelationsTekst1;

	@Column(name = "relationsTekst2")
	protected String RelationsTekst2;

	@Column(name = "relationsTekst3")
	protected String RelationsTekst3;

	@Column(name = "relationsTekst4")
	protected String RelationsTekst4;

	@Column(name = "relationsTekst5")
	protected String RelationsTekst5;

	@XmlTransient
	@Column(name = "ModifiedDate")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date modifiedDate;

	@Column(name = "ValidFrom")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validFrom;

	@Column(name = "ValidTo")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validTo;

	@Override
	public BigInteger getRecordID() {
		return recordID;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Date getUpdated() {
		return modifiedDate;
	}

	@Override
	public String toString() {
		return "Umynddiggørelse-værge-relation[cpr=" + cpr + ", type=" + typeKode + ", tekst=" + typeTekst + ", relations-cpr=" + relationCpr + "]";
	}
}
