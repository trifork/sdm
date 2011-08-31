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
public class LaegemiddelAdministrationsvejRef extends TakstEntity
{
	private long drugId;
	private String AdministrationsvejKode;

	public LaegemiddelAdministrationsvejRef(Laegemiddel lm, Administrationsvej av)
	{
		this.drugId = lm.getDrugid();
		this.AdministrationsvejKode = av.getKode();
	}

	@Column
	public String getAdministrationsvejKode()
	{
		return AdministrationsvejKode;
	}

	@Id
	@Column
	public String getCID()
	{
		return drugId + "-" + AdministrationsvejKode;
	}

	@Column
	public long getDrugId()
	{
		return drugId;
	}

	public void setAdministrationsvejKode(String administrationsvejKode)
	{
		AdministrationsvejKode = administrationsvejKode;
	}

	public void setDrugId(long drugId)
	{
		this.drugId = drugId;
	}
}
