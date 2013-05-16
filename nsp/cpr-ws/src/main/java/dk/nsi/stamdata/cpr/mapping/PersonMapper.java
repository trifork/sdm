package dk.nsi.stamdata.cpr.mapping;

import com.trifork.stamdata.Preconditions;
import dk.nsi.stamdata.cpr.models.Person;
import dk.nsi.stamdata.security.WhitelistService;
import dk.sosi.seal.model.SystemIDCard;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Instant;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;

import static dk.sosi.seal.model.constants.SubjectIdentifierTypeValues.CVR_NUMBER;

public class PersonMapper {

    protected static final String UKENDT = "UKENDT";

    protected static final int AUTHORITY_CODE_LENGTH = 4;
    protected static final String SERVICE_NAME_DGCPR = WhitelistService.DEFAULT_SERVICE_NAME;//"dgcpr";
    protected WhitelistService whitelistService;


    public static enum ServiceProtectionLevel
    {
        AlwaysCensorProtectedData,
        CensorProtectedDataForNonAuthorities
    }


    public static enum CPRProtectionLevel
    {
        CensorCPR,
        DoNotCensorCPR
    }


    protected static final String ADRESSEBESKYTTET = "ADRESSEBESKYTTET";
    protected static final String PROTECTED_CPR = "0000000000";
    protected final SystemIDCard idCard;
    protected final MunicipalityMapper munucipalityMapper;

    public PersonMapper(WhitelistService whitelistService, SystemIDCard idCard, MunicipalityMapper munucipalityMapper) {
        // Once we get this far the filter should have gotten rid of id cards
        // that are not CVR authenticated System ID Cards.
        this.whitelistService = whitelistService;
        this.idCard = idCard;
        this.munucipalityMapper = munucipalityMapper;
    }




    protected String getBuildingIdentifier(Person person) {
        // NSPSUPPORT-129 : buildingidentifier er alene husnummer + evt bogstav (fx 13B). Dette er indeholdt i person.husnummer
        return person.husnummer; // + person.bygningsnummer;
    }

    protected boolean isClientAnAuthority() {
        String careProviderType = idCard.getSystemInfo().getCareProvider().getType();
        Preconditions.checkState(CVR_NUMBER.equals(careProviderType), "ID Card Care provider is not a CVR. This is a programming error.");
        String clientCVR = idCard.getSystemInfo().getCareProvider().getID();
        return whitelistService.isCvrWhitelisted(clientCVR, SERVICE_NAME_DGCPR);
    }

    protected boolean isPersonProtected(Person person) {
        if (person.getNavnebeskyttelsestartdato() == null) return false;

        // We have to make the guard above to avoid null being passed into the
        // Instant
        // it is converted to the beginning of the era.

        Preconditions.checkState(person.getNavnebeskyttelseslettedato() != null, "The protection end date was not present. This is most unexpected and a programming error.");

        Instant protectionStart = new Instant(person.getNavnebeskyttelsestartdato());
        Instant protectionEnd = new Instant(person.getNavnebeskyttelseslettedato());

        return protectionStart.isEqualNow() || (protectionStart.isBeforeNow() && protectionEnd.isAfterNow());
    }

    protected String actualOrUnknown(String actual)
    {
        return !StringUtils.isBlank(actual) ? actual : UKENDT;
    }

    protected String actualOrNull(String actual)
    {
        return !StringUtils.isBlank(actual) ? actual : null;
    }

    public static XMLGregorianCalendar newXMLGregorianCalendar(Date date)
    {
        DatatypeFactory factory = null;
        try {
            factory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
        return factory.newXMLGregorianCalendar(new DateTime(date).toGregorianCalendar());
    }

}
