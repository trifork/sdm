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

package com.trifork.stamdata.importer.jobs.sor;

import com.trifork.stamdata.importer.jobs.sor.model.Apotek;
import com.trifork.stamdata.importer.jobs.sor.model.Praksis;
import com.trifork.stamdata.importer.jobs.sor.model.Sygehus;
import com.trifork.stamdata.importer.jobs.sor.model.SygehusAfdeling;
import com.trifork.stamdata.importer.jobs.sor.model.Yder;
import com.trifork.stamdata.importer.jobs.sor.xmlmodel.HealthInstitutionEntity;
import com.trifork.stamdata.importer.jobs.sor.xmlmodel.OrganizationalUnitEntity;
import com.trifork.stamdata.importer.jobs.sor.xmlmodel.SpecialityMapper;
import com.trifork.stamdata.importer.jobs.sor.xmlmodel.UnitTypeMapper;


public class XMLModelMapper
{
	public static Praksis toPraksis(HealthInstitutionEntity hie)
	{
		Praksis p = new Praksis();
		p.setNavn(hie.getEntityName());
		p.setSorNummer(hie.getSorIdentifier());
		p.setEanLokationsnummer(hie.getEanLocationCode());
		p.setValidFrom(hie.getFromDate());
		p.setValidTo(hie.getToDate());
		p.setRegionCode(hie.getInstitutionOwnerEntity().getRegionCode());

		return p;
	}

	public static Yder toYder(OrganizationalUnitEntity oue)
	{
		Yder y = new Yder();
		y.setSorNummer(oue.getSorIdentifier());
		y.setPraksisSorNummer(oue.getHealthInstitutionEntity().getSorIdentifier());
		y.setEanLokationsnummer(oue.getEanLocationCode());
		y.setNavn(oue.getEntityName());
		if (oue.getProviderIdentifier() != null)
		{
			y.setNummer(oue.getProviderIdentifier().replaceAll("^0+(?!$)", ""));
		}
		y.setVejnavn(oue.getStreetName() + " " + oue.getStreetBuildingIdentifier());
		y.setBynavn(oue.getDistrictName());
		y.setPostnummer(oue.getPostCodeIdentifier());
		y.setEmail(oue.getEmailAddressIdentifier());
		y.setWww(oue.getWebsite());
		y.setTelefon(oue.getTelephoneNumberIdentifier());
		y.setHovedSpecialeKode(oue.getSpecialityCode());
		y.setHovedSpecialeTekst(SpecialityMapper.kodeToString(oue.getSpecialityCode()));
		y.setValidFrom(oue.getFromDate());
		y.setValidTo(oue.getToDate());
		return y;
	}

	public static Sygehus toSygehus(HealthInstitutionEntity hie)
	{

		Sygehus s = new Sygehus();
		s.setSorNummer(hie.getSorIdentifier());
		s.setNavn(hie.getEntityName());
		s.setEanLokationsnummer(hie.getEanLocationCode());
		s.setNummer(hie.getShakIdentifier());
		s.setVejnavn(hie.getStreetName() + " " + hie.getStreetBuildingIdentifier());
		s.setBynavn(hie.getDistrictName());
		s.setPostnummer(hie.getPostCodeIdentifier());
		s.setEmail(hie.getEmailAddressIdentifier());
		s.setWww(hie.getWebsite());
		s.setTelefon(hie.getTelephoneNumberIdentifier());
		s.setValidFrom(hie.getFromDate());
		s.setValidTo(hie.getToDate());

		return s;
	}

	public static SygehusAfdeling toSygehusAfdeling(OrganizationalUnitEntity oue)
	{

		SygehusAfdeling sa = new SygehusAfdeling();
		sa.setEanLokationsnummer(oue.getEanLocationCode());
		sa.setSorNummer(oue.getSorIdentifier());
		sa.setNavn(oue.getEntityName());
		sa.setNummer(oue.getShakIdentifier());
		sa.setVejnavn(oue.getStreetName() + " " + oue.getStreetBuildingIdentifier());
		sa.setBynavn(oue.getDistrictName());
		sa.setPostnummer(oue.getPostCodeIdentifier());
		sa.setEmail(oue.getEmailAddressIdentifier());
		sa.setWww(oue.getWebsite());
		sa.setTelefon(oue.getTelephoneNumberIdentifier());
		sa.setAfdelingTypeKode(oue.getUnitType());
		sa.setAfdelingTypeTekst(UnitTypeMapper.kodeToString(oue.getUnitType()));
		sa.setHovedSpecialeKode(oue.getSpecialityCode());
		sa.setHovedSpecialeTekst(SpecialityMapper.kodeToString(oue.getSpecialityCode()));
		if (oue.getParrent() != null)
		{
			// Subdivision of an other 'afdeling'
			sa.setOverAfdelingSorNummer(oue.getParrent().getSorIdentifier());
		}
		else
		{
			// Directly under a 'Sygehus'
			sa.setSygehusSorNummer(oue.getHealthInstitutionEntity().getSorIdentifier());
		}
		sa.setUnderlagtSygehusSorNummer(oue.getBelongsTo().getSorIdentifier());
		sa.setValidFrom(oue.getFromDate());
		sa.setValidTo(oue.getToDate());
		return sa;
	}

	public static Apotek toApotek(OrganizationalUnitEntity oue)
	{

		Apotek a = new Apotek();
		a.setSorNummer(oue.getSorIdentifier());
		if (oue.getPharmacyIdentifier() != null)
		{
			String[] pi = oue.getPharmacyIdentifier().split(",");
			a.setApotekNummer(Long.parseLong(pi[0]));
			if (pi.length > 1)
			{
				a.setFilialNummer(Long.parseLong(pi[1]));
			}

		}
		a.setEanLokationsnummer(oue.getEanLocationCode());
		a.setNavn(oue.getEntityName());
		a.setVejnavn(oue.getStreetName() + " " + oue.getStreetBuildingIdentifier());
		a.setBynavn(oue.getDistrictName());
		a.setPostnummer(oue.getPostCodeIdentifier());
		a.setEmail(oue.getEmailAddressIdentifier());
		a.setWww(oue.getWebsite());
		a.setTelefon(oue.getTelephoneNumberIdentifier());
		a.setValidFrom(oue.getFromDate());
		a.setValidTo(oue.getToDate());
		return a;
	}
}
