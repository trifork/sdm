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
package dk.nsi.stamdata.cpr;

import static dk.nsi.stamdata.cpr.Factories.YESTERDAY;
import static dk.nsi.stamdata.cpr.PersonMapper.newXMLGregorianCalendar;
import static dk.nsi.stamdata.cpr.ws.PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_1;
import static dk.nsi.stamdata.cpr.ws.PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_2;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import dk.nsi.stamdata.cpr.mapping.MunicipalityMapper;
import dk.nsi.stamdata.cpr.models.Person;
import dk.nsi.stamdata.cpr.models.SikredeYderRelation;
import dk.nsi.stamdata.cpr.models.Yderregister;
import dk.nsi.stamdata.cpr.ws.AssociatedGeneralPractitionerStructureType;
import dk.nsi.stamdata.cpr.ws.PersonPublicHealthInsuranceType;
import dk.nsi.stamdata.cpr.ws.PersonWithHealthCareInformationStructureType;
import dk.nsi.stamdata.testing.MockSecureTokenService;
import dk.sosi.seal.model.SystemIDCard;


public class PersonHealthCareInfoMapperTest
{
	private static final String NOT_WHITELISTED_CVR = "99999999";
	private static final String ADRESSEBESKYTTET = "ADRESSEBESKYTTET";
	private static final String UKENDT = "UKENDT";

	private Person person;
	private Yderregister yder;
	private SikredeYderRelation relation;

	private PersonWithHealthCareInformationStructureType output;


	@Before
	public void setUp()
	{
		person = Factories.createPersonWithoutAddressProtection();
		yder = Factories.createYderregister();
		relation = Factories.createSikredeYderRelation();
	}


	private void doMapping() throws DatatypeConfigurationException
	{
		MunicipalityMapper municipalityMapper = new MunicipalityMapper();
		Set<String> whitelist = Sets.newHashSet();
		SystemIDCard idCard = MockSecureTokenService.createSignedSystemIDCard(NOT_WHITELISTED_CVR);

		output = new PersonMapper(whitelist, idCard, municipalityMapper).map(person, relation, yder);
	}
	
	@Test
	public void itInsertsRealDataIfNoneAreMissing() throws DatatypeConfigurationException
	{
		doMapping();

		assertThatRelationIsRealData();
		assertThatGeneralPractitionerIsRealData();
	}


	@Test
	public void itInsertsDummyDataIfTheYderDataIsMissing() throws DatatypeConfigurationException
	{
		yder = null;

		doMapping();

		assertThatRelationIsRealData();
		assertThatGeneralPractitionerIsDummy(UKENDT);
	}
	
	@Test
	public void itInsertsDummyDataIfThePersonIsProtectedAndDataIsMissing() throws DatatypeConfigurationException
	{
		person = Factories.createPersonWithAddressProtection();
		yder = null;
		relation = null;
		
		doMapping();
		
		assertThatGeneralPractitionerIsDummy(ADRESSEBESKYTTET);
		assertThatRelationIsDummy();
	}
	
	@Test
	public void itInsertsDummyDataIfPersonIsProtected() throws DatatypeConfigurationException
	{
		person = Factories.createPersonWithAddressProtection();
		
		doMapping();

		assertThatRelationIsDummy();
		assertThatGeneralPractitionerIsDummy(ADRESSEBESKYTTET);
	}

	@Test
	public void itInsertsDummyDataIfTheRelationDataIsMissing() throws DatatypeConfigurationException
	{
		relation = null;

		doMapping();

		assertThatGeneralPractitionerIsRealData();
		assertThatRelationIsDummy();
	}


	@Test
	public void itInsertsDummyDataIfBothAreMissing() throws DatatypeConfigurationException
	{
		relation = null;
		yder = null;

		doMapping();

		assertThatRelationIsDummy();
		assertThatGeneralPractitionerIsDummy(UKENDT);
	}


	private void assertThatGeneralPractitionerIsDummy(String placeholderText)
	{
		AssociatedGeneralPractitionerStructureType g = output.getPersonHealthCareInformationStructure().getAssociatedGeneralPractitionerStructure();

		assertThat(g.getAssociatedGeneralPractitionerOrganisationName(), is(placeholderText));
		assertThat(g.getDistrictName(), is(placeholderText));
		assertThat(g.getStandardAddressIdentifier(), is(placeholderText));
		assertThat(g.getAssociatedGeneralPractitionerIdentifier(), is(BigInteger.ZERO));
		assertThat(g.getTelephoneSubscriberIdentifier(), is("00000000"));
		assertThat(g.getPostCodeIdentifier(), is("0000"));
		assertThat(g.getEmailAddressIdentifier(), is(placeholderText + "@example.com"));
	}


	private void assertThatGeneralPractitionerIsRealData()
	{
		AssociatedGeneralPractitionerStructureType g = output.getPersonHealthCareInformationStructure().getAssociatedGeneralPractitionerStructure();

		assertThat(g.getAssociatedGeneralPractitionerOrganisationName(), is(yder.getNavn()));
		assertThat(g.getDistrictName(), is(yder.getBynavn()));
		assertThat(g.getStandardAddressIdentifier(), is(yder.getVejnavn() + ", " + yder.getPostnummer() + " " + yder.getBynavn()));
		assertThat(g.getAssociatedGeneralPractitionerIdentifier().intValue(), is(yder.getNummer()));
		assertThat(g.getTelephoneSubscriberIdentifier(), is(yder.getTelefon()));
		assertThat(g.getPostCodeIdentifier(), is(yder.getPostnummer()));
		assertThat(g.getEmailAddressIdentifier(), is(yder.getEmail()));
	}


	private void assertThatRelationIsDummy() throws DatatypeConfigurationException
	{
		PersonPublicHealthInsuranceType g = output.getPersonHealthCareInformationStructure().getPersonPublicHealthInsurance();

		assertThat(g.getPublicHealthInsuranceGroupIdentifier(), is(SYGESIKRINGSGRUPPE_1));
		assertThat(g.getPublicHealthInsuranceGroupStartDate(), is(newXMLGregorianCalendar(new Date(0))));
	}
	
	private void assertThatRelationIsRealData() throws DatatypeConfigurationException
	{
		PersonPublicHealthInsuranceType g = output.getPersonHealthCareInformationStructure().getPersonPublicHealthInsurance();

		assertThat(g.getPublicHealthInsuranceGroupIdentifier(), is(SYGESIKRINGSGRUPPE_2));
		assertThat(g.getPublicHealthInsuranceGroupStartDate().toGregorianCalendar().getTime(), is(YESTERDAY));
	}
}
