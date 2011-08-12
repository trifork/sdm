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

package com.trifork.stamdata.importer.jobs.dkma.model;

import java.util.Date;

import com.trifork.stamdata.importer.jobs.dkma.TakstEntity;
import com.trifork.stamdata.importer.persistence.*;


@Output
public class UdgaaedeNavne extends TakstEntity
{
	private Long drugid; // Ref. t. LMS01
	private Date datoForAendringen;
	private String tidligereNavn;

	@Output
	public Date getDatoForAendringen()
	{
		return datoForAendringen;
	}

	@Output
	public Long getDrugid()
	{
		return drugid;
	}

	@SuppressWarnings("deprecation")
	@Override
	@Id
	@Output(name = "CID")
	public String getKey()
	{
		return datoForAendringen.toGMTString() + '-' + tidligereNavn + '-' + drugid;
	}

	@Output
	public String getTidligereNavn()
	{
		return tidligereNavn;
	}

	public void setDatoForAendringen(Date datoForAendringen)
	{
		this.datoForAendringen = datoForAendringen;
	}

	public void setDrugid(Long drugid)
	{
		this.drugid = drugid;
	}

	public void setTidligereNavn(String tidligereNavn)
	{
		this.tidligereNavn = tidligereNavn;
	}
}
