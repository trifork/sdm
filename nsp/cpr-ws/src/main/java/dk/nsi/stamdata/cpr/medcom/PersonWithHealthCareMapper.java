package dk.nsi.stamdata.cpr.medcom;

import static com.trifork.stamdata.Preconditions.checkNotNull;
import static dk.nsi.stamdata.cpr.PersonMapper.newXMLGregorianCalendar;

import java.math.BigInteger;

import javax.xml.datatype.DatatypeConfigurationException;

import com.google.inject.Inject;
import com.trifork.stamdata.models.cpr.Person;
import com.trifork.stamdata.models.sikrede.SikredeYderRelation;
import com.trifork.stamdata.models.sikrede.Yderregister;

import dk.nsi.stamdata.cpr.PersonMapper;
import dk.nsi.stamdata.cpr.PersonMapper.ServiceProtectionLevel;
import dk.nsi.stamdata.cpr.ws.AssociatedGeneralPractitionerStructureType;
import dk.nsi.stamdata.cpr.ws.PersonHealthCareInformationStructureType;
import dk.nsi.stamdata.cpr.ws.PersonInformationStructureType;
import dk.nsi.stamdata.cpr.ws.PersonPublicHealthInsuranceType;
import dk.nsi.stamdata.cpr.ws.PersonWithHealthCareInformationStructureType;
import dk.nsi.stamdata.cpr.ws.PublicHealthInsuranceGroupIdentifierType;

public class PersonWithHealthCareMapper
{
	private PersonMapper personMapper;

	@Inject
	PersonWithHealthCareMapper(PersonMapper personMapper)
	{
		this.personMapper = checkNotNull(personMapper, "personMapper");
	}

	public PersonWithHealthCareInformationStructureType map(Person person, SikredeYderRelation sikredeYderRelation, Yderregister yderregister) throws DatatypeConfigurationException
	{
		PersonWithHealthCareInformationStructureType personWithHealthCare = new PersonWithHealthCareInformationStructureType();

		PersonInformationStructureType personInformation = personMapper.map(person, ServiceProtectionLevel.AlwaysCensorProtectedData);
		personWithHealthCare.setPersonInformationStructure(personInformation);

        PersonHealthCareInformationStructureType personHealthCareInformation = new PersonHealthCareInformationStructureType();
        personWithHealthCare.setPersonHealthCareInformationStructure(personHealthCareInformation);

        AssociatedGeneralPractitionerStructureType associatedGeneralPractitioner = map(yderregister);
        personHealthCareInformation.setAssociatedGeneralPractitionerStructure(associatedGeneralPractitioner);

        PersonPublicHealthInsuranceType personPublicHealthInsurance = map(sikredeYderRelation);
        personHealthCareInformation.setPersonPublicHealthInsurance(personPublicHealthInsurance);

		return personWithHealthCare;
	}

    public AssociatedGeneralPractitionerStructureType map(Yderregister yderregister) {
        AssociatedGeneralPractitionerStructureType associatedGeneralPractitioner =  new AssociatedGeneralPractitionerStructureType();
        associatedGeneralPractitioner.setAssociatedGeneralPractitionerIdentifier(BigInteger.valueOf(yderregister.getNummer()));
        associatedGeneralPractitioner.setAssociatedGeneralPractitionerOrganisationName(yderregister.getNavn());
        associatedGeneralPractitioner.setDistrictName(yderregister.getBynavn());
        associatedGeneralPractitioner.setEmailAddressIdentifier(yderregister.getEmail());
        associatedGeneralPractitioner.setPostCodeIdentifier(yderregister.getPostnummer());
        associatedGeneralPractitioner.setStandardAddressIdentifier(yderregister.getVejnavn());//TODO - fetch from ? (not in yderregister)
        associatedGeneralPractitioner.setTelephoneSubscriberIdentifier(yderregister.getTelefon());
        return associatedGeneralPractitioner;
    }

    public PersonPublicHealthInsuranceType map(SikredeYderRelation sikredeYderRelation) {
        PersonPublicHealthInsuranceType personPublicHealthInsurance = new PersonPublicHealthInsuranceType() ;
        try {
            personPublicHealthInsurance.setPublicHealthInsuranceGroupStartDate(newXMLGregorianCalendar(sikredeYderRelation.getGruppeKodeIkraftDato()));
            PublicHealthInsuranceGroupIdentifierType publicHealthInsuranceGroupIdentifier;
            if (sikredeYderRelation.getSikringsgruppeKode() == '1') {
                publicHealthInsuranceGroupIdentifier =  PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_1;
            } else if (sikredeYderRelation.getSikringsgruppeKode() == '2') {
                publicHealthInsuranceGroupIdentifier =  PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_2;
            } else {
                throw new RuntimeException(); //FIXME - do the right thing
            }
            personPublicHealthInsurance.setPublicHealthInsuranceGroupIdentifier(publicHealthInsuranceGroupIdentifier);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return personPublicHealthInsurance;
    }

}