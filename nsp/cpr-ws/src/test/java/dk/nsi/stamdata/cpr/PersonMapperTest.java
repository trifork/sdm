package dk.nsi.stamdata.cpr;

import static dk.nsi.stamdata.cpr.PersonMapper.ServiceProtectionLevel.AlwaysCensorProtectedData;
import static dk.nsi.stamdata.cpr.PersonMapper.ServiceProtectionLevel.CensorProtectedDataForNonAuthorities;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.trifork.stamdata.models.cpr.Person;

import dk.nsi.stamdata.cpr.PersonMapper.ServiceProtectionLevel;
import dk.nsi.stamdata.cpr.integrationtest.dgws.TestSTSMock;
import dk.nsi.stamdata.cpr.ws.PersonInformationStructureType;
import dk.sosi.seal.model.SystemIDCard;

@RunWith(MockitoJUnitRunner.class)
public class PersonMapperTest
{
	private final boolean FOR_AUTORITY_CLIENT = true;
	private final boolean FOR_NON_AUTORITY_CLIENT = false;
	
	private Person person;
	
	private final static String WHITELISTED_CVR = "12345678";
	private SystemIDCard whitelistedIDCard;
	
	private String NON_WHITELISTED_CVR = "23456789";
	private SystemIDCard nonWhitelistedIDCard;
	
	private static final String CENSORED = "ADRESSEBESKYTTET";
	
	private Set<String> whitelist;
	
	private static final Date TWO_DAYS_AGO = DateTime.now().minusDays(2).toDate();
	private static final Date YESTERDAY = DateTime.now().minusDays(1).toDate();
	private static final Date TOMORROW = DateTime.now().plusDays(1).toDate();
	private static final Date IN_TWO_DAYS = DateTime.now().plusDays(2).toDate();
	
	private static final String FIRSTNAME = "Peter";
	
	@Before
	public void setUp()
	{
		whitelist = Sets.newHashSet(WHITELISTED_CVR);
		
		nonWhitelistedIDCard = TestSTSMock.createTestSTSSignedIDCard(NON_WHITELISTED_CVR);
		whitelistedIDCard = TestSTSMock.createTestSTSSignedIDCard(WHITELISTED_CVR);
		
		person = new Person();
		person.setFornavn(FIRSTNAME);
	}
	
	public PersonMapper mapper(boolean isClientAnAuthority)
	{
		return new PersonMapper(whitelist, isClientAnAuthority ? whitelistedIDCard : nonWhitelistedIDCard);
	}
	
	@Test
	public void shouldNotProtectPersonWithNoActiveProtection() throws DatatypeConfigurationException
	{
		person.setNavnebeskyttelsestartdato(null);
		person.setNavnebeskyttelseslettedato(null);
		
		assertThatPersonIsNotProtected(mapper(FOR_AUTORITY_CLIENT).map(person, CensorProtectedDataForNonAuthorities));
		assertThatPersonIsNotProtected(mapper(FOR_NON_AUTORITY_CLIENT).map(person, CensorProtectedDataForNonAuthorities));
	}
	
	@Test
	public void shouldProtectPersonWithActiveProtectionIfClientIsNotAuthority() throws DatatypeConfigurationException
	{
		person.setNavnebeskyttelsestartdato(YESTERDAY);
		person.setNavnebeskyttelseslettedato(TOMORROW);
		
		assertThatPersonIsProtected(mapper(FOR_NON_AUTORITY_CLIENT).map(person, CensorProtectedDataForNonAuthorities));
	}
	
	@Test
	public void shouldNotProtectPersonWithActiveProtectionIfClientIsAuthority() throws DatatypeConfigurationException
	{
		person.setNavnebeskyttelsestartdato(YESTERDAY);
		person.setNavnebeskyttelseslettedato(TOMORROW);
		
		assertThatPersonIsNotProtected(mapper(FOR_AUTORITY_CLIENT).map(person, CensorProtectedDataForNonAuthorities));
	}
	
	@Test
	public void shouldAlwaysProtectPersonWithActiveProtectionIfAuthoritiesHaveNoSpecialRights() throws DatatypeConfigurationException
	{
		person.setNavnebeskyttelsestartdato(YESTERDAY);
		person.setNavnebeskyttelseslettedato(TOMORROW);
		
		assertThatPersonIsProtected(mapper(FOR_AUTORITY_CLIENT).map(person, AlwaysCensorProtectedData));
		assertThatPersonIsProtected(mapper(FOR_NON_AUTORITY_CLIENT).map(person, AlwaysCensorProtectedData));
	}
	
	@Test
	public void shouldNotProtectPersonIfProtectionHasNotStartedYet() throws DatatypeConfigurationException
	{
		person.setNavnebeskyttelsestartdato(TOMORROW);
		person.setNavnebeskyttelseslettedato(IN_TWO_DAYS);
		
		assertThatPersonIsNotProtected(mapper(FOR_NON_AUTORITY_CLIENT).map(person, AlwaysCensorProtectedData));
	}
	
	@Test
	public void shouldNotProtectPersonIfProtectionHasEnded() throws DatatypeConfigurationException
	{
		person.setNavnebeskyttelsestartdato(TWO_DAYS_AGO);
		person.setNavnebeskyttelseslettedato(YESTERDAY);
		
		assertThatPersonIsNotProtected(mapper(FOR_NON_AUTORITY_CLIENT).map(person, AlwaysCensorProtectedData));
	}
	
	private void assertThatPersonIsNotProtected(PersonInformationStructureType output)
	{
		assertThat(output.getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonGivenName(), is(FIRSTNAME));
	}
	
	private void assertThatPersonIsProtected(PersonInformationStructureType output)
	{
		assertThat(output.getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonGivenName(), is(CENSORED));
	}
}
