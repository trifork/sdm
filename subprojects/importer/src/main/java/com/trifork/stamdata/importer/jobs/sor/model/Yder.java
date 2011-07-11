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

package com.trifork.stamdata.importer.jobs.sor.model;

import java.util.Date;

import com.trifork.stamdata.importer.persistence.AbstractStamdataEntity;
import com.trifork.stamdata.importer.persistence.Id;
import com.trifork.stamdata.importer.persistence.Output;
import com.trifork.stamdata.importer.persistence.StamdataEntity;
import com.trifork.stamdata.importer.util.DateUtils;


@Output(name = "Yder")
public class Yder extends AbstractStamdataEntity implements StamdataEntity
{
	private String nummer;
	private Long eanLokationsnummer;
	private String telefon;
	private String navn;
	private String vejnavn;
	private String postnummer;
	private String bynavn;
	private String email;
	private String www;
	private Long hovedSpecialeKode;
	private String hovedSpecialeTekst;
	private Long sorNummer;
	private Long praktisSorNummer;
	private Date validFrom;
	private Date validTo;

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
	public Long getEanLokationsnummer()
	{
		return eanLokationsnummer;
	}

	public void setEanLokationsnummer(Long eanLokationsnummer)
	{
		this.eanLokationsnummer = eanLokationsnummer;
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
	public Long getHovedSpecialeKode()
	{
		return hovedSpecialeKode;
	}

	public void setHovedSpecialeKode(Long hovedSpecialeKode)
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

	@Id
	@Output
	public Long getSorNummer()
	{
		return sorNummer;
	}

	public void setSorNummer(Long sorNummer)
	{
		this.sorNummer = sorNummer;
	}

	@Output
	public Long getPraksisSorNummer()
	{
		return praktisSorNummer;
	}

	public void setPraksisSorNummer(Long praktisSorNummer)
	{
		this.praktisSorNummer = praktisSorNummer;
	}

	@Override
	public Date getValidFrom()
	{
		return validFrom;
	}

	public void setValidFrom(Date validFrom)
	{
		this.validFrom = validFrom;
	}

	@Override
	public Date getValidTo()
	{
		return (validTo != null) ? validTo : DateUtils.FUTURE;
	}

	public void setValidTo(Date validTo)
	{
		this.validTo = validTo;
	}
}
