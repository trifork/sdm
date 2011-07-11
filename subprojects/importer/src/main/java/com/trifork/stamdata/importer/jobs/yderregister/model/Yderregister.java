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

import com.trifork.stamdata.importer.persistence.AbstractStamdataEntity;
import com.trifork.stamdata.importer.persistence.Id;
import com.trifork.stamdata.importer.persistence.Output;
import com.trifork.stamdata.importer.persistence.StamdataEntity;
import com.trifork.stamdata.importer.util.DateUtils;


@Output
public class Yderregister extends AbstractStamdataEntity implements StamdataEntity
{
	private String nummer;
	private String telefon;
	private String navn;
	private String vejnavn;
	private String postnummer;
	private String bynavn;
	private int amtNummer;
	private String email;
	private String www;
	private String hovedSpecialeKode;
	private String hovedSpecialeTekst;
	private String histID;
	private Date tilgangDato;
	private Date afgangDato;

	@Id
	@Output
	public String getNummer()
	{
		return nummer;
	}

	public void setNummer(String nummer)
	{
		this.nummer = nummer;
	}

	@Output
	public String getTelefon()
	{
		return telefon;
	}

	public void setTelefon(String telefon)
	{
		this.telefon = telefon;
	}

	@Output
	public String getNavn()
	{
		return navn;
	}

	public void setNavn(String navn)
	{
		this.navn = navn;
	}

	@Output
	public String getVejnavn()
	{
		return vejnavn;
	}

	public void setVejnavn(String vejnavn)
	{
		this.vejnavn = vejnavn;
	}

	@Output
	public String getPostnummer()
	{
		return postnummer;
	}

	public void setPostnummer(String postnummer)
	{
		this.postnummer = postnummer;
	}

	@Output
	public String getBynavn()
	{
		return bynavn;
	}

	public void setBynavn(String bynavn)
	{
		this.bynavn = bynavn;
	}

	@Output
	public int getAmtNummer()
	{
		return amtNummer;
	}

	public void setAmtNummer(int amtNummer)
	{
		this.amtNummer = amtNummer;
	}

	@Output
	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	@Output
	public String getWww()
	{
		return www;
	}

	public void setWww(String www)
	{
		this.www = www;
	}

	@Output
	public String getHovedSpecialeKode()
	{
		return hovedSpecialeKode;
	}

	public void setHovedSpecialeKode(String hovedSpecialeKode)
	{
		this.hovedSpecialeKode = hovedSpecialeKode;
	}

	@Output
	public String getHovedSpecialeTekst()
	{
		return hovedSpecialeTekst;
	}

	public void setHovedSpecialeTekst(String hovedSpecialeTekst)
	{
		this.hovedSpecialeTekst = hovedSpecialeTekst;
	}

	@Output
	public String getHistID()
	{
		return histID;
	}

	public void setHistID(String histID)
	{
		this.histID = histID;
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
		return DateUtils.FUTURE;
	}
}
