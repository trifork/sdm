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

package com.trifork.stamdata.importer.jobs.yderregister.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.trifork.stamdata.importer.persistence.AbstractStamdataEntity;
import com.trifork.stamdata.importer.util.DateUtils;
import com.trifork.stamdata.models.TemporalEntity;

@Entity
public class YderregisterPerson extends AbstractStamdataEntity implements TemporalEntity
{
	private String nummer;
	private String histIdPerson;
	private String cpr;
	private Long personrolleKode;
	private String personrolleTxt;
	private Date tilgangDato;
	private Date afgangDato;

	@Id
	@Column
	public String getId()
	{
		return nummer + "-" + cpr;
	}

	@Column
	public String getNummer()
	{
		return nummer;
	}

	public void setNummer(String nummer)
	{
		this.nummer = nummer;
	}

	@Column
	public String getHistIdPerson()
	{
		return histIdPerson;
	}

	public void setHistIdPerson(String histIdPerson)
	{
		this.histIdPerson = histIdPerson;
	}

	@Column
	public String getCpr()
	{
		return cpr;
	}

	public void setCpr(String cpr)
	{
		this.cpr = cpr;
	}

	@Column
	public Long getPersonrolleKode()
	{
		return personrolleKode;
	}

	public void setPersonrolleKode(Long personrolleKode)
	{
		this.personrolleKode = personrolleKode;
	}

	@Column
	public String getPersonrolleTxt()
	{
		return personrolleTxt;
	}

	public void setPersonrolleTxt(String personrolleTxt)
	{
		this.personrolleTxt = personrolleTxt;
	}

	public Date getTilgangDato()
	{
		return tilgangDato;
	}

	public void setTilgangDato(Date tilgangDato)
	{
		this.tilgangDato = tilgangDato;
	}

	public Date getAfgangDato()
	{
		return afgangDato;
	}

	public void setAfgangDato(Date afgangDato)
	{
		this.afgangDato = afgangDato;
	}

	@Override
	public Date getValidFrom()
	{
		return tilgangDato;
	}

	@Override
	public Date getValidTo()
	{
		if (afgangDato != null) return afgangDato;
		return DateUtils.THE_END_OF_TIME;
	}
}
