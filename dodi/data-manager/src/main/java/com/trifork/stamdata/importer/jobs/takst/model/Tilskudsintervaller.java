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
public class Tilskudsintervaller extends TakstEntity
{
	private Long type; // Patienttype: almen, barn, kroniker, terminal
	private Long niveau; // fx 1-4 for alment tilskud, 1-3 for barn
	private Long nedreGraense; // Nedre beløbsgrænse for niveauet (i øre)
	private Long oevreGraense; // Øvre beløbsgrænse for niveauet (i øre)
	private Double procent; // Tilskudsprocent

	@Id
	@Column(name = "CID")
	public String getKey()
	{
		return type + "-" + niveau;
	}

	@Column
	public Long getNedreGraense()
	{
		return nedreGraense;
	}

	@Column
	public Long getNiveau()
	{
		return niveau;
	}

	@Column
	public Long getOevreGraense()
	{
		return this.oevreGraense;
	}

	@Column
	public Double getProcent()
	{
		return this.procent;
	}

	@Column
	public Long getType()
	{
		return type;
	}

	public void setNedreGraense(Long nedreGraense)
	{
		this.nedreGraense = nedreGraense;
	}

	public void setNiveau(Long niveau)
	{
		this.niveau = niveau;
	}

	public void setOevreGraense(Long oevreGraense)
	{
		this.oevreGraense = oevreGraense;
	}

	public void setProcent(Double procent)
	{
		this.procent = procent;
	}

	public void setType(Long type)
	{
		this.type = type;
	}
}
