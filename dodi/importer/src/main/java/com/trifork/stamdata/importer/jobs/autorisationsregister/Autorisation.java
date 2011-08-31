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

package com.trifork.stamdata.importer.jobs.autorisationsregister;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.trifork.stamdata.importer.persistence.*;
import com.trifork.stamdata.importer.util.DateUtils;


@Entity
public class Autorisation extends AbstractStamdataEntity implements StamdataEntity
{
	private String nummer;
	private String cpr;
	private String efternavn;
	private String fornavn;
	private String educationCode;

	Autorisationsregisterudtraek dataset;

	public Autorisation(String number, String cpr, String firstName, String lastName, String educationCode)
	{
		this.nummer = number;
		this.cpr = cpr;
		this.fornavn = firstName;
		this.efternavn = lastName;
		this.educationCode = educationCode;
	}

	@Id
	@Column
	public String getAutorisationsnummer()
	{
		return nummer;
	}

	@Column
	public String getCpr()
	{
		return cpr;
	}

	@Column
	public String getEfternavn()
	{
		return efternavn;
	}

	@Column
	public String getFornavn()
	{
		return fornavn;
	}

	@Column
	public String getUddannelsesKode()
	{
		return educationCode;
	}

	@Override
	public Date getValidFrom()
	{
		return dataset.getValidFrom();
	}

	@Override
	public Date getValidTo()
	{
		return DateUtils.THE_END_OF_TIME;
	}
}
