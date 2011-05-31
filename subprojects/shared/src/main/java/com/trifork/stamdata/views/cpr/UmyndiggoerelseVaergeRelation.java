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

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.views.ViewPath;


@Entity
@XmlRootElement
@ViewPath("cpr/umyndiggoerelsevaergerelation/v1")
@AttributeOverride(name = "recordID",column = @Column(name = "UmyndiggoerelseVaergeRelationPID"))
public class UmyndiggoerelseVaergeRelation extends CprView {

	@Column(name = "Id")
	@XmlTransient
	public String id;

	@Column(name = "TypeKode")
	public String typeKode;

	@Column(name = "TypeTekst")
	public String typeTekst;

	@Column(name = "RelationCpr")
	public String relationCpr;

	@Column(name = "RelationCprStartDato")
	@Temporal(TemporalType.TIMESTAMP)
	public Date relationCprStartDato;

	@Column(name = "VaergesNavn")
	public String vaergesNavn;

	@Column(name = "VaergesNavnStartDato")
	@Temporal(TemporalType.TIMESTAMP)
	public Date vaergesNavnStartDato;

	@Column(name = "relationsTekst1")
	public String RelationsTekst1;

	@Column(name = "relationsTekst2")
	public String RelationsTekst2;

	@Column(name = "relationsTekst3")
	public String RelationsTekst3;

	@Column(name = "relationsTekst4")
	public String RelationsTekst4;

	@Column(name = "relationsTekst5")
	public String RelationsTekst5;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Umynddiggørelse-værge-relation[cpr=" + cpr + ", type=" + typeKode + ", tekst=" + typeTekst + ", relations-cpr=" + relationCpr + "]";
	}
}
