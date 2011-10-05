package dk.nsi.stamdata.cpr.medcom;

import com.google.inject.Inject;
import com.trifork.stamdata.models.cpr.Person;
import com.trifork.stamdata.models.sikrede.SikredeYderRelation;
import com.trifork.stamdata.models.sikrede.Yderregister;
import dk.nsi.stamdata.cpr.PersonMapper;
import dk.nsi.stamdata.cpr.PersonMapper.ServiceProtectionLevel;
import dk.nsi.stamdata.cpr.ws.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import java.math.BigInteger;

import static com.trifork.stamdata.Preconditions.checkNotNull;
import static dk.nsi.stamdata.cpr.PersonMapper.newXMLGregorianCalendar;

public class PersonWithHealthCareMapper
{
	private static final Logger logger = LoggerFactory.getLogger(PersonWithHealthCareMapper.class);
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

    private PersonPublicHealthInsuranceType map(SikredeYderRelation sikredeYderRelation) throws DatatypeConfigurationException {
        PersonPublicHealthInsuranceType personPublicHealthInsurance = new PersonPublicHealthInsuranceType() ;
		personPublicHealthInsurance.setPublicHealthInsuranceGroupStartDate(newXMLGregorianCalendar(sikredeYderRelation.getGruppeKodeIkraftDato()));
		PublicHealthInsuranceGroupIdentifierType publicHealthInsuranceGroupIdentifier;
		if (sikredeYderRelation.getSikringsgruppeKode() == '1') {
			publicHealthInsuranceGroupIdentifier =  PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_1;
		} else if (sikredeYderRelation.getSikringsgruppeKode() == '2') {
			publicHealthInsuranceGroupIdentifier =  PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_2;
		} else {
			String msg = "SikredeYderRelation for cpr" + sikredeYderRelation.getCpr() + " and yder " + sikredeYderRelation.getYdernummer() + " has unsupported group code " + sikredeYderRelation.getSikringsgruppeKode() + ". Cannot proceed with request";
			logger.error(msg + ". Returning fault to caller");
			throw new IllegalStateException(msg);
		}
		personPublicHealthInsurance.setPublicHealthInsuranceGroupIdentifier(publicHealthInsuranceGroupIdentifier);

        return personPublicHealthInsurance;
    }

}