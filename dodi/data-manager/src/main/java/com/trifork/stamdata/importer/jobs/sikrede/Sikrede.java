/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */
package com.trifork.stamdata.importer.jobs.sikrede;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class Sikrede extends CPREntity
{
	private String kommunekode;
	private Date kommunekodeIkraftDato;

	/* Optional field */
	private String foelgeskabsPersonCpr;

	private String status;
	private Date bevisIkraftDato;
	/* SSL elementer */
	private String forsikringsinstans;
	private String forsikringsinstansKode;
	private String forsikringsnummer;
	private Date sslGyldigFra;
	private Date SslGyldigTil;
	private String socialLand;
	private String socialLandKode;

	@Id
	@Column
	public String getCpr()
	{
		return cpr;
	}

	@Column
	public String getKommunekode()
	{
		return kommunekode;
	}

	@Column
	public String getStatus()
	{
		return status;
	}

	public void setKommunekode(String kommunekode)
	{
		this.kommunekode = kommunekode;
	}

	@Column
	public Date getKommunekodeIkraftDato()
	{
		return kommunekodeIkraftDato;
	}

	public void setKommunekodeIKraftDato(Date iKraftDato)
	{
		this.kommunekodeIkraftDato = iKraftDato;
	}

	public void setFoelgeskabsPersonCpr(String foelgeskabsPersonCpr)
	{
		this.foelgeskabsPersonCpr = foelgeskabsPersonCpr;
	}

	public void setKommunekodeIkraftDato(Date kommunekodeIkraftDato)
	{
		this.kommunekodeIkraftDato = kommunekodeIkraftDato;
	}

	public void setFoelgeskabsPerson(String cprFoelgeskabsPerson)
	{
		this.foelgeskabsPersonCpr = cprFoelgeskabsPerson;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public void setForsikringsinstans(String forsikringsinstans)
	{
		this.forsikringsinstans = forsikringsinstans;
	}

	public void setForsikringsinstansKode(String forsikringsinstansKode)
	{
		this.forsikringsinstansKode = forsikringsinstansKode;
	}

	public void setForsikringsnummer(String forsikringsnummer)
	{
		this.forsikringsnummer = forsikringsnummer;
	}

	public void setSikredesSocialeLand(String land)
	{
		this.socialLand = land;
	}

	public void setSikredesSocialeLandKode(String landekode)
	{
		this.socialLandKode = landekode;
	}

	@Column
	public String getSocialLand()
	{
		return socialLand;
	}

	public void setSocialLand(String socialLand)
	{
		this.socialLand = socialLand;
	}

	@Column
	public String getSocialLandKode()
	{
		return socialLandKode;
	}

	public void setSocialLandKode(String socialLandKode)
	{
		this.socialLandKode = socialLandKode;
	}

	@Column
	public Date getSslGyldigFra()
	{
		return sslGyldigFra;
	}

	public void setSslGyldigFra(Date gyldigFra)
	{
		this.sslGyldigFra = gyldigFra;
	}

	@Column
	public Date getSslGyldigTil()
	{
		return SslGyldigTil;
	}

	public void setSslGyldigTil(Date gyldigTil)
	{
		this.SslGyldigTil = gyldigTil;
	}

	@Column
	public Date getBevisIkraftDato()
	{
		return bevisIkraftDato;
	}

	public void setBevisIkraftDato(Date bevisIkraftDato)
	{
		this.bevisIkraftDato = bevisIkraftDato;
	}

	@Column
	public String getFoelgeskabsPersonCpr()
	{
		return foelgeskabsPersonCpr;
	}

	@Column
	public String getForsikringsinstans()
	{
		return forsikringsinstans;
	}

	@Column
	public String getForsikringsinstansKode()
	{
		return forsikringsinstansKode;
	}

	@Column
	public String getForsikringsnummer()
	{
		return forsikringsnummer;
	}

	@Override
	public String toString()
	{
		return "Sikrede{" + "bevisIkraftDato=" + bevisIkraftDato + ", kommunekode='" + kommunekode + '\'' + ", kommunekodeIkraftDato=" + kommunekodeIkraftDato + ", foelgeskabsPersonCpr='" + foelgeskabsPersonCpr + '\'' + ", status='" + status + '\'' + ", forsikringsinstans='" + forsikringsinstans + '\'' + ", forsikringsinstansKode='" + forsikringsinstansKode + '\'' + ", forsikringsnummer='" + forsikringsnummer + '\'' + ", sslGyldigFra=" + sslGyldigFra + ", SslGyldigTil=" + SslGyldigTil + ", socialLand='" + socialLand + '\'' + ", socialLandKode='" + socialLandKode + '\'' + '}';
	}

}
