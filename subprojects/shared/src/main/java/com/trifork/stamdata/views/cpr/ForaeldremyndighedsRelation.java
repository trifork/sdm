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

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.views.ViewPath;


@Entity
@XmlRootElement
@Table(name = "ForaeldreMyndighedRelation")
@ViewPath("cpr/foraeldremyndighedrelation/v1")
@AttributeOverride(name = "recordID",column = @Column(name = "ForaeldreMyndighedRelationPID"))
public class ForaeldremyndighedsRelation extends CprView {

	@XmlTransient
	@Column(name = "Id")
	protected String id;

	@Column(name = "TypeKode")
	protected String typeKode;

	@Column(name = "TypeTekst")
	protected String typeTekst;

	@Column(name = "RelationCpr")
	protected String relationCpr;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Forældremyndighedsrelation[cpr=" + cpr + ", typekode=" + typeKode + ", typetekst=" + typeTekst + ", relations-cpr=" + relationCpr + "]";
	}
}
