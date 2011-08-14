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
import com.trifork.stamdata.importer.util.Dates;


/**
 * LMS10
 */
@Output
public class UdgaaetNavn extends TakstEntity
{
	private Long drugID;
	private Date datoForAendringen;
	private String tidligereNavn;

	@Output
	public Date getDatoForAendringen()
	{
		return datoForAendringen;
	}

	public void setDatoForAendringen(Date datoForAendringen)
	{
		this.datoForAendringen = datoForAendringen;
	}

	@Output
	@Length(11)
	public Long getDrugID()
	{
		return drugID;
	}

	public void setDrugID(Long drugid)
	{
		this.drugID = drugid;
	}

	@Id
	@Override
	@Output(name = "CID")
	@Length(71)
	public String getKey()
	{
		String dateString = Dates.DK_yyyyMMdd.print(datoForAendringen.getTime());
		return dateString + '-' + tidligereNavn + '-' + drugID;
	}

	@Output
	@Length(50)
	public String getTidligereNavn()
	{
		return tidligereNavn;
	}

	public void setTidligereNavn(String tidligereNavn)
	{
		this.tidligereNavn = tidligereNavn;
	}
}
