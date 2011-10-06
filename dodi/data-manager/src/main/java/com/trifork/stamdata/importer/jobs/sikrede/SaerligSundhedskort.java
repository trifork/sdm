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
public class SaerligSundhedskort extends CPREntity
{
	private String adresseLinje1;
	private String adresseLinje2;
	private String bopelsLand;
	private String bopelsLandKode;
	private String emailAdresse;
	private String familieRelationCpr;
	private Date foedselsDato;
	private String mobilNummer;
	private String postnummerBy;
	private Date sskGyldigTil;
	private Date sskGyldigFra;

	@Id
	@Column
	public String getCpr()
	{
		return cpr;
	}

	@Column
	public String getAdresseLinje1()
	{
		return adresseLinje1;
	}

	public void setAdresseLinje1(String adresseLinje1)
	{
		this.adresseLinje1 = adresseLinje1;
	}

	@Column
	public String getAdresseLinje2()
	{
		return adresseLinje2;
	}

	public void setAdresseLinje2(String adresseLinje2)
	{
		this.adresseLinje2 = adresseLinje2;
	}

	@Column
	public String getBopelsLand()
	{
		return bopelsLand;
	}

	public void setBopelsLand(String bopelsLand)
	{
		this.bopelsLand = bopelsLand;
	}

	@Column
	public String getBopelsLandKode()
	{
		return bopelsLandKode;
	}

	public void setBopelsLandKode(String bopelsLandKode)
	{
		this.bopelsLandKode = bopelsLandKode;
	}

	@Column
	public String getEmailAdresse()
	{
		return emailAdresse;
	}

	public void setEmailAdresse(String emailAdresse)
	{
		this.emailAdresse = emailAdresse;
	}

	@Column
	public String getFamilieRelationCpr()
	{
		return familieRelationCpr;
	}

	public void setFamilieRelationCpr(String familieRelationCpr)
	{
		this.familieRelationCpr = familieRelationCpr;
	}

	@Column
	public Date getFoedselsDato()
	{
		return foedselsDato;
	}

	public void setFoedselsDato(Date foedselsDato)
	{
		this.foedselsDato = foedselsDato;
	}

	@Column
	public String getMobilNummer()
	{
		return mobilNummer;
	}

	public void setMobilNummer(String mobilNummer)
	{
		this.mobilNummer = mobilNummer;
	}

	@Column
	public String getPostnummerBy()
	{
		return postnummerBy;
	}

	public void setPostnummerBy(String postNummerBy)
	{
		this.postnummerBy = postNummerBy;
	}

	@Column
	public Date getSskGyldigFra()
	{
		return sskGyldigFra;
	}

	public void setSskGyldigFra(Date sskGyldigFra)
	{
		this.sskGyldigFra = sskGyldigFra;
	}

	@Column
	public Date getSskGyldigTil()
	{
		return sskGyldigTil;
	}

	public void setSskGyldigTil(Date gyldigTil)
	{
		this.sskGyldigTil = gyldigTil;
	}

	@Override
	public String toString()
	{
		return "SaerligSundhedskort{" + "adresseLinje1='" + adresseLinje1 + '\'' + ", adresseLinje2='" + adresseLinje2 + '\'' + ", bopelsLand='" + bopelsLand + '\'' + ", bopelsLandKode='" + bopelsLandKode + '\'' + ", emailAdresse='" + emailAdresse + '\'' + ", familieRelationCpr='" + familieRelationCpr + '\'' + ", foedselsDato=" + foedselsDato + ", mobilNummer='" + mobilNummer + '\'' + ", postnummerBy='" + postnummerBy + '\'' + ", sskGyldigTil=" + sskGyldigTil + ", sskGyldigFra=" + sskGyldigFra + '}';
	}
}
