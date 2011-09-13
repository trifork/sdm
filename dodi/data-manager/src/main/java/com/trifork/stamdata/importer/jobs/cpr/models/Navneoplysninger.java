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
public class Navneoplysninger extends CPREntity
{
	String cpr;
	String fornavn;
	String fornavnMarkering;
	String mellemnavn;
	String mellemnavnMarkering;
	String efternavn;
	String efternavnMarkering;
	Date startDato;
	String startDatoMarkering;
	String adresseringsNavn;

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
	public String getFornavn()
	{
		return fornavn;
	}

	public void setFornavn(String fornavn)
	{
		this.fornavn = fornavn;
	}

	public String getFornavnMarkering()
	{
		return fornavnMarkering;
	}

	public void setFornavnMarkering(String fornavnMarkering)
	{
		this.fornavnMarkering = fornavnMarkering;
	}

	@Column
	public String getMellemnavn()
	{
		return mellemnavn;
	}

	public void setMellemnavn(String mellemnavn)
	{
		this.mellemnavn = mellemnavn;
	}

	public String getMellemnavnMarkering()
	{
		return mellemnavnMarkering;
	}

	public void setMellemnavnMarkering(String mellemnavnMarkering)
	{
		this.mellemnavnMarkering = mellemnavnMarkering;
	}

	@Column
	public String getEfternavn()
	{
		return efternavn;
	}

	public void setEfternavn(String efternavn)
	{
		this.efternavn = efternavn;
	}

	public String getEfternavnMarkering()
	{
		return efternavnMarkering;
	}

	public void setEfternavnMarkering(String efternavnMarkering)
	{
		this.efternavnMarkering = efternavnMarkering;
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

	public String getAdresseringsNavn()
	{
		return adresseringsNavn;
	}

	public void setAdresseringsNavn(String adresseringsNavn)
	{
		this.adresseringsNavn = adresseringsNavn;
	}
}
