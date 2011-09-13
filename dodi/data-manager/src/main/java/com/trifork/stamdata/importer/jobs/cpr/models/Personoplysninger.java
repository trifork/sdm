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

package com.trifork.stamdata.importer.jobs.cpr.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;



@Entity(name = "Person")
public class Personoplysninger extends CPREntity
{
	String cpr;
	String gaeldendeCpr;
	String status;
	Date statusDato;
	String statusMakering;
	String koen;
	Date foedselsdato;
	String foedselsdatoMarkering;
	Date startDato;
	String startDatoMarkering;
	Date slutDato;
	String slutDatoMarkering;
	String stilling;

	@Id
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
	public String getGaeldendeCpr()
	{
		return gaeldendeCpr;
	}

	public void setGaeldendeCpr(String gaeldendeCpr)
	{
		this.gaeldendeCpr = gaeldendeCpr;
	}

	@Column
	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public Date getStatusDato()
	{
		return statusDato;
	}

	public void setStatusDato(Date statusDato)
	{
		this.statusDato = statusDato;
	}

	public String getStatusMakering()
	{
		return statusMakering;
	}

	public void setStatusMakering(String statusMakering)
	{
		this.statusMakering = statusMakering;
	}

	@Column
	public String getKoen()
	{
		return koen;
	}

	public void setKoen(String koen)
	{
		this.koen = koen;
	}

	@Column
	public Date getFoedselsdato()
	{
		return foedselsdato;
	}

	public void setFoedselsdato(Date foedselsdato)
	{
		this.foedselsdato = foedselsdato;
	}

	public String getFoedselsdatoMarkering()
	{
		return foedselsdatoMarkering;
	}

	public void setFoedselsdatoMarkering(String foedselsdatoMarkering)
	{
		this.foedselsdatoMarkering = foedselsdatoMarkering;
	}

	public Date getStartDato()
	{
		return startDato;
	}

	public void setStartDato(Date startDato)
	{
		this.startDato = startDato;
	}

	public String getStartDatoMarkering()
	{
		return startDatoMarkering;
	}

	public void setStartDatoMarkering(String startDatoMarkering)
	{
		this.startDatoMarkering = startDatoMarkering;
	}

	public Date getSlutDato()
	{
		return slutDato;
	}

	public void setSlutdato(Date slutDato)
	{
		this.slutDato = slutDato;
	}

	public String getSlutDatoMarkering()
	{
		return slutDatoMarkering;
	}

	public void setSlutDatoMarkering(String slutDatoMarkering)
	{
		this.slutDatoMarkering = slutDatoMarkering;
	}

	@Column
	public String getStilling()
	{
		return stilling;
	}

	public void setStilling(String stilling)
	{
		this.stilling = stilling;
	}
}
