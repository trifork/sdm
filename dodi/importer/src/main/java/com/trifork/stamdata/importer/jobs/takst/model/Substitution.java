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

package com.trifork.stamdata.importer.jobs.takst.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.trifork.stamdata.importer.jobs.takst.TakstEntity;


@Entity
public class Substitution extends TakstEntity
{
	private Long substitutionsgruppenummer; // Substitutionsgruppe for pakningen
	private Long receptensVarenummer; // Varenr. hvis substitutionsmuligheder skal findes.
	private Long numeriskPakningsstoerrelse; // Felt 07 i LMS02
	private String prodAlfabetiskeSekvensplads; // Felt 04 i LMS01
	private String substitutionskodeForPakning; // Værdier=A-B-C (for varenr. i felt 02)
	private Long billigsteVarenummer; // Henvisning til billigste pakning

	@Column
	public Long getBilligsteVarenummer()
	{
		return this.billigsteVarenummer;
	}

	@Override
	public Long getKey()
	{
		return receptensVarenummer;
	}

	@Column
	public Long getNumeriskPakningsstoerrelse()
	{
		return numeriskPakningsstoerrelse;
	}

	@Column
	public String getProdAlfabetiskeSekvensplads()
	{
		return prodAlfabetiskeSekvensplads;
	}

	@Id
	@Column
	public Long getReceptensVarenummer()
	{
		return receptensVarenummer;
	}

	@Column
	public Long getSubstitutionsgruppenummer()
	{
		return substitutionsgruppenummer;
	}

	@Column
	public String getSubstitutionskodeForPakning()
	{
		return substitutionskodeForPakning;
	}

	public void setBilligsteVarenummer(Long billigsteVarenummer)
	{
		this.billigsteVarenummer = billigsteVarenummer;
	}

	public void setNumeriskPakningsstoerrelse(Long numeriskPakningsstoerrelse)
	{
		this.numeriskPakningsstoerrelse = numeriskPakningsstoerrelse;
	}

	public void setProdAlfabetiskeSekvensplads(String prodAlfabetiskeSekvensplads)
	{
		this.prodAlfabetiskeSekvensplads = prodAlfabetiskeSekvensplads;
	}

	public void setReceptensVarenummer(Long receptensVarenummer)
	{
		this.receptensVarenummer = receptensVarenummer;
	}

	public void setSubstitutionsgruppenummer(Long substitutionsgruppenummer)
	{
		this.substitutionsgruppenummer = substitutionsgruppenummer;
	}

	public void setSubstitutionskodeForPakning(String substitutionskodeForPakning)
	{
		this.substitutionskodeForPakning = substitutionskodeForPakning;
	}
}
