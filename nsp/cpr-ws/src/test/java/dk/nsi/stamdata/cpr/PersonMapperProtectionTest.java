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

import static dk.nsi.stamdata.cpr.PersonMapper.ServiceProtectionLevel.AlwaysCensorProtectedData;
import static dk.nsi.stamdata.cpr.PersonMapper.ServiceProtectionLevel.CensorProtectedDataForNonAuthorities;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.trifork.stamdata.persistence.Record;

import dk.nsi.stamdata.cpr.PersonMapper.CPRProtectionLevel;
import dk.nsi.stamdata.cpr.mapping.MunicipalityMapper;
import dk.nsi.stamdata.cpr.models.Person;
import dk.nsi.stamdata.jaxws.generated.PersonInformationStructureType;
import dk.nsi.stamdata.jaxws.generated.PersonWithHealthCareInformationStructureType;
import dk.nsi.stamdata.testing.MockSecureTokenService;
import dk.sosi.seal.model.AuthenticationLevel;
import dk.sosi.seal.model.SystemIDCard;

@RunWith(MockitoJUnitRunner.class)
public class PersonMapperProtectionTest
{
	private final boolean FOR_AUTORITY_CLIENT = true;
	private final boolean FOR_NON_AUTORITY_CLIENT = false;
	
	private Person person;
	
	private final static String WHITELISTED_CVR = "12345678";
	private SystemIDCard whitelistedIDCard;
	
	private static final String NON_WHITELISTED_CVR = "23456789";
	private SystemIDCard nonWhitelistedIDCard;
	
	private static final String CENSORED = "ADRESSEBESKYTTET";
	
	private Set<String> whitelist;
	private MunicipalityMapper municipalityMapper;
	private Record yderRecord;
	private Record sikredeRecord;
	
	@Before
	public void setUp()
	{
		whitelist = Sets.newHashSet(WHITELISTED_CVR);
		
		nonWhitelistedIDCard = MockSecureTokenService.createSignedSystemIDCard(NON_WHITELISTED_CVR, AuthenticationLevel.VOCES_TRUSTED_SYSTEM);
		whitelistedIDCard = MockSecureTokenService.createSignedSystemIDCard(WHITELISTED_CVR, AuthenticationLevel.VOCES_TRUSTED_SYSTEM);
		
		municipalityMapper = new MunicipalityMapper();
		person = Factories.createPerson();
		yderRecord = Factories.createYderRecord("1234");
		sikredeRecord = Factories.createSikredeRecordFor(person, yderRecord, "2", new DateTime(Factories.YESTERDAY));
	}
	
	public PersonMapper mapper(boolean isClientAnAuthority)
	{
		return new PersonMapper(whitelist, isClientAnAuthority ? whitelistedIDCard : nonWhitelistedIDCard, municipalityMapper);
	}
	
	@Test
	public void shouldNotProtectPersonWithNoActiveProtection() throws DatatypeConfigurationException
	{
		person.setNavnebeskyttelsestartdato(null);
		person.setNavnebeskyttelseslettedato(null);
		
		assertThatPersonIsNotProtected(mapper(FOR_AUTORITY_CLIENT).map(person, CensorProtectedDataForNonAuthorities, CPRProtectionLevel.DoNotCensorCPR));
		assertThatPersonIsNotProtected(mapper(FOR_NON_AUTORITY_CLIENT).map(person, CensorProtectedDataForNonAuthorities, CPRProtectionLevel.DoNotCensorCPR));
	}
	
	@Test
	public void shouldProtectPersonWithActiveProtectionIfClientIsNotAuthority() throws DatatypeConfigurationException
	{
		person.setNavnebeskyttelsestartdato(Factories.YESTERDAY);
		person.setNavnebeskyttelseslettedato(Factories.TOMORROW);
		
		assertThatPersonIsProtected(mapper(FOR_NON_AUTORITY_CLIENT).map(person, CensorProtectedDataForNonAuthorities, CPRProtectionLevel.DoNotCensorCPR));
	}
	
	@Test
	public void shouldNotProtectPersonWithActiveProtectionIfClientIsAuthority() throws DatatypeConfigurationException
	{
		person.setNavnebeskyttelsestartdato(Factories.YESTERDAY);
		person.setNavnebeskyttelseslettedato(Factories.TOMORROW);
		
		assertThatPersonIsNotProtected(mapper(FOR_AUTORITY_CLIENT).map(person, CensorProtectedDataForNonAuthorities, CPRProtectionLevel.DoNotCensorCPR));
	}
	
	@Test
	public void shouldAlwaysProtectPersonWithActiveProtectionIfAuthoritiesHaveNoSpecialRights() throws DatatypeConfigurationException
	{
		person.setNavnebeskyttelsestartdato(Factories.YESTERDAY);
		person.setNavnebeskyttelseslettedato(Factories.TOMORROW);
		
		assertThatPersonIsProtected(mapper(FOR_AUTORITY_CLIENT).map(person, AlwaysCensorProtectedData, CPRProtectionLevel.DoNotCensorCPR));
		assertThatPersonIsProtected(mapper(FOR_NON_AUTORITY_CLIENT).map(person, AlwaysCensorProtectedData, CPRProtectionLevel.DoNotCensorCPR));
	}
	
	@Test
	public void shouldNotProtectPersonIfProtectionHasNotStartedYet() throws DatatypeConfigurationException
	{
		person.setNavnebeskyttelsestartdato(Factories.TOMORROW);
		person.setNavnebeskyttelseslettedato(Factories.IN_TWO_DAYS);
		
		assertThatPersonIsNotProtected(mapper(FOR_NON_AUTORITY_CLIENT).map(person, AlwaysCensorProtectedData, CPRProtectionLevel.DoNotCensorCPR));
	}
	
	@Test
	public void shouldNotProtectPersonIfProtectionHasEnded() throws DatatypeConfigurationException
	{
		person.setNavnebeskyttelsestartdato(Factories.TWO_DAYS_AGO);
		person.setNavnebeskyttelseslettedato(Factories.YESTERDAY);
		
		assertThatPersonIsNotProtected(mapper(FOR_NON_AUTORITY_CLIENT).map(person, AlwaysCensorProtectedData, CPRProtectionLevel.DoNotCensorCPR));
	}
	
	@Test
	public void shouldProtectPersonHealthCareInfoIfProtectionIsActive() throws DatatypeConfigurationException
	{
		person.setNavnebeskyttelsestartdato(Factories.YESTERDAY);
		person.setNavnebeskyttelseslettedato(Factories.TOMORROW);
		
		assertThatHealthCareInfoIsProtected(mapper(FOR_NON_AUTORITY_CLIENT).map(person, sikredeRecord, yderRecord));
	}
	
	@Test
	public void shouldNotProtectPersonHealthCareInfoIfProtectionIsNotActive() throws DatatypeConfigurationException
	{
		person.setNavnebeskyttelsestartdato(Factories.TWO_DAYS_AGO);
		person.setNavnebeskyttelseslettedato(Factories.YESTERDAY);
		
		assertThatHealthCareInfoIsNotProtected(mapper(FOR_NON_AUTORITY_CLIENT).map(person, sikredeRecord, yderRecord));
	}
	
	private void assertThatPersonIsNotProtected(PersonInformationStructureType output)
	{
		assertThat(output.getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonGivenName(), is(person.getFornavn()));
	}
	
	private void assertThatPersonIsProtected(PersonInformationStructureType output)
	{
		assertThat(output.getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonGivenName(), is(CENSORED));
	}
	
	private void assertThatHealthCareInfoIsProtected(PersonWithHealthCareInformationStructureType output)
	{
		assertThat(output.getPersonHealthCareInformationStructure().getAssociatedGeneralPractitionerStructure().getDistrictName(), is(CENSORED));
	}
	
	private void assertThatHealthCareInfoIsNotProtected(PersonWithHealthCareInformationStructureType output)
	{
		assertThat(output.getPersonHealthCareInformationStructure().getAssociatedGeneralPractitionerStructure().getDistrictName(), is(yderRecord.get("PostdistYder")));
	}
}
