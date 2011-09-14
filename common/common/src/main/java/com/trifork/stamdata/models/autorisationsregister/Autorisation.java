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

package com.trifork.stamdata.models.autorisationsregister;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.trifork.stamdata.models.TemporalEntity;


@Entity
public class Autorisation implements TemporalEntity
{
	private String nummer;
	private String cpr;
	private String efternavn;
	private String fornavn;
	private String educationCode;
	private Date validFrom;
	private Date validTo;

	public Autorisation()
	{	
	}

	@Id
	@Column
	public String getAutorisationsnummer()
	{
		return nummer;
	}
	
	public void setAutorisationnummer(String nummer)
	{
		this.nummer = nummer;
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
	public String getEfternavn()
	{
		return efternavn;
	}
	
	public void setEfternavn(String value)
	{
		efternavn = value;
	}
	
	@Column
	public String getFornavn()
	{
		return fornavn;
	}
	
	public void setFornavn(String value)
	{
		fornavn = value;
	}

	@Column
	public String getUddannelsesKode()
	{
		return educationCode;
	}
	
	public void setUddannelsesKode(String value)
	{
		educationCode = value;
	}

	@Override
	public Date getValidFrom()
	{
		return validFrom;
	}
	
	public void setValidFrom(Date value)
	{
		validFrom = value;
	}

	@Override
	public Date getValidTo()
	{
		return validTo;
	}

	public void setValidTo(Date value)
	{
		validTo = value;
	}
}
