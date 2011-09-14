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
public class Enhedspriser extends TakstEntity
{
	private Long drugID; // Ref. t. LMS01
	private Long varenummer; // Ref. t. LMS02
	private Long prisPrEnhed; // Pris = Ekspeditionens samlede pris (ESP)
	private Long prisPrDDD; // Pris = ESP
	private String billigstePakning; // Markering af billigste pakning pr. enhed
										// for DrugID

	@Column
	public String getBilligstePakning()
	{
		return billigstePakning;
	}

	@Column
	public Long getDrugID()
	{
		return drugID;
	}

	@Override
	public String getKey()
	{
		return varenummer.toString();
	}

	@Column
	public Long getPrisPrDDD()
	{
		return prisPrDDD;
	}

	@Column
	public Long getPrisPrEnhed()
	{
		return prisPrEnhed;
	}

	@Id
	@Column
	public Long getVarenummer()
	{
		return varenummer;
	}

	public void setBilligstePakning(String billigstePakning)
	{
		this.billigstePakning = billigstePakning;
	}

	public void setDrugID(Long drugID)
	{
		this.drugID = drugID;
	}

	public void setPrisPrDDD(Long prisPrDDD)
	{
		this.prisPrDDD = prisPrDDD;
	}

	public void setPrisPrEnhed(Long prisPrEnhed)
	{
		this.prisPrEnhed = prisPrEnhed;
	}

	public void setVarenummer(Long varenummer)
	{
		this.varenummer = varenummer;
	}
}
