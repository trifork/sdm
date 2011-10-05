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
import dk.nsi.stamdata.cpr.PersonMapper.CPRProtectionLevel;
import dk.nsi.stamdata.cpr.PersonMapper.ServiceProtectionLevel;
import dk.nsi.stamdata.cpr.ws.AssociatedGeneralPractitionerStructureType;
import dk.nsi.stamdata.cpr.ws.PersonHealthCareInformationStructureType;
import dk.nsi.stamdata.cpr.ws.PersonInformationStructureType;
import dk.nsi.stamdata.cpr.ws.PersonPublicHealthInsuranceType;
import dk.nsi.stamdata.cpr.ws.PersonWithHealthCareInformationStructureType;
import dk.nsi.stamdata.cpr.ws.PublicHealthInsuranceGroupIdentifierType;

public class PersonWithHealthCareMapper
{

}