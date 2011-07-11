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

package com.trifork.stamdata.importer.jobs.cpr.model;

import com.trifork.stamdata.importer.persistence.Id;
import com.trifork.stamdata.importer.persistence.Output;


@Output(name = "Person")
public class Klarskriftadresse extends CPREntity
{
	String cpr;
	String adresseringsNavn;
	String coNavn;
	String lokalitet;
	String adresseringsVejnavn;
	String byNavn;
	Long postNummer;
	String postDistrikt;
	Long kommuneKode;
	Long vejKode;
	String husNummer;
	String etage;
	String sideDoerNummer;
	String bygningsNummer;
	String vejNavn;

	@Id
	@Output
	public String getCpr()
	{
		return cpr;
	}

	public void setCpr(String cpr)
	{
		this.cpr = cpr;
	}

	public String getAdresseringsNavn()
	{
		return adresseringsNavn;
	}

	public void setAdresseringsNavn(String adresseringsNavn)
	{
		this.adresseringsNavn = adresseringsNavn;
	}

	@Output
	public String getCoNavn()
	{
		return coNavn;
	}

	public void setCoNavn(String coNavn)
	{
		this.coNavn = coNavn;
	}

	@Output
	public String getLokalitet()
	{
		return lokalitet;
	}

	public void setLokalitet(String lokalitet)
	{
		this.lokalitet = lokalitet;
	}

	public String getVejnavnTilAdresseringsNavn()
	{
		return adresseringsVejnavn;
	}

	public void setVejnavnTilAdresseringsNavn(String adresse)
	{
		this.adresseringsVejnavn = adresse;
	}

	@Output
	public String getByNavn()
	{
		return byNavn;
	}

	public void setByNavn(String byNavn)
	{
		this.byNavn = byNavn;
	}

	@Output
	public Long getPostNummer()
	{
		return postNummer;
	}

	public void setPostNummer(Long postNummer)
	{
		this.postNummer = postNummer;
	}

	@Output
	public String getPostDistrikt()
	{
		return postDistrikt;
	}

	public void setPostDistrikt(String postDistrikt)
	{
		this.postDistrikt = postDistrikt;
	}

	@Output
	public Long getKommuneKode()
	{
		return kommuneKode;
	}

	public void setKommuneKode(Long kommuneKode)
	{
		this.kommuneKode = kommuneKode;
	}

	@Output
	public Long getVejKode()
	{
		return vejKode;
	}

	public void setVejKode(Long vejKode)
	{
		this.vejKode = vejKode;
	}

	@Output
	public String getHusNummer()
	{
		return husNummer;
	}

	public void setHusNummer(String husNummer)
	{
		this.husNummer = husNummer;
	}

	@Output
	public String getEtage()
	{
		return etage;
	}

	public void setEtage(String etage)
	{
		this.etage = etage;
	}

	@Output
	public String getSideDoerNummer()
	{
		return sideDoerNummer;
	}

	public void setSideDoerNummer(String sideDoerNummer)
	{
		this.sideDoerNummer = sideDoerNummer;
	}

	@Output
	public String getBygningsNummer()
	{
		return bygningsNummer;
	}

	public void setBygningsNummer(String bygningsNummer)
	{
		this.bygningsNummer = bygningsNummer;
	}

	@Output
	public String getVejNavn()
	{
		return vejNavn;
	}

	public void setVejNavn(String vejNavn)
	{
		this.vejNavn = vejNavn;
	}
}
