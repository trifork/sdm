import static org.junit.Assert.*;

import javax.xml.bind.JAXBElement;

import oio.sagdok.person._1_0.PersonType;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.trifork.stamdata.client.security.TwoWaySslSecurityHandler;

public class LookupIntegrationTest {
	private static Client client = new Client();
    @BeforeClass
    public static void beforeClass() {
        new IntegrationTestTwoWaySslSecurityHandler();
    }
    
    @Before
    public void setup() {
    }

    @Test
    public void worksForSimplePerson() {
    	JAXBElement<PersonType> result = getPerson("0708610089");
        assertEquals("0708610089", result.getValue().getRegistrering().get(0).getAttributListe().getRegisterOplysning().get(0).getCprBorger().getPersonCivilRegistrationIdentifier());
    }

    @Test
    public void worksForUmyndiggoerelse() {
    	JAXBElement<PersonType> result = getPerson("0709614126");
    	assertEquals("URN:CPR:0904414131", result.getValue().getRegistrering().get(0).getRelationListe().getRetligHandleevneVaergeForPersonen().get(0).getReferenceID().getURNIdentifikator());
    }

    @Test
    public void canValidatePerson() {
	checkValidPerson("0708610089");
   }

	private void checkValidPerson(String cpr) {
		String validationResult = getValidationResult(cpr);
        assertEquals("Person with cpr " + cpr + " is valid", "NO ERRORS", validationResult);
	}

	private void checkInvalidPerson(String cpr) {
		String validationResult = getValidationResult(cpr);
        assertFalse("NO ERRORS".equals(validationResult));
	}
    @Test
    public void canValidateAllValidPersons() {
		String[] validCprs = new String[] {  "0101429059",
				"0101520013", "0101965058", "0101980014", "0107529039",
				"0108610018", "0108610069", "0307610078", "0701614011",
				"0701614038", "0701614089", "0702614074", "0702614082",
				"0702614147", "0702614155", "0703614167", "0706614184",
				"0706614818", "0707614218", "0707614226", "0707614234",
				"0707614285", "0707614293", "0708610038", "0708610089",
				"0708614246", "0708614319", "0708614327", "0708614335",
				"0708614866", "0709610015", "0709610058", "0709614037",
				"0709614045", "0709614096", "0709614118", "0709614126",
				"0709614169", "0709614215", "0709614231", "0709614258",
				"0709614304", "0709614347", "0709614398", "0709614401",
				"0709614428", "0709614452", "0709614495", "0709614568",
				"0709614592", "0709614673", "0709614738", "0709614754",
				"0709614762", "0710614326", "0711614354", "0712614382",
				"0712614455", "0901414025", "0901414084", "0904414131",
				"0905414143", "0909610028", "0909610036", "0909970016",
				"0912414426", "1303814074", "1306814172", "1306814180",
				"1307610015", "1312814362", "2802363039", "2802980011",
				"2906980013", "3012995007", "3112000010",
				"3112420028", "3112970079", };
		for(String cpr : validCprs) {
			String validationResult = getValidationResult(cpr);
			System.out.println("CPR " + cpr + ": " + validationResult);

		}
		for(String cpr : validCprs) {
			checkValidPerson(cpr);
		}
    }

    @Test
    public void validationFailsForAllInvalidPersons() {
		String[] invalidCprs = new String[] {
				"0101005038", // addresseringsnavn tom streng
				"3006980014", // navn og adresseringsnavn tom streng
		};
		for(String cpr : invalidCprs) {
			checkInvalidPerson(cpr);
		}

    }
    
	private JAXBElement<PersonType> getPerson(String cpr) {
    	GenericType<JAXBElement<PersonType>> type = new GenericType<JAXBElement<PersonType>>() {};
    	return client.resource("https://localhost:8444/lookup/person").path(cpr).get(type);    	
    }
	
	private String getValidationResult(String cpr) {
	   	return client.resource("https://localhost:8444/lookup/person").path(cpr).path("validate").get(String.class);    	
		
	}
	
    static class IntegrationTestTwoWaySslSecurityHandler extends TwoWaySslSecurityHandler {
        @Override
        protected String getTrustStorePassword() {
            return "Test1234";
        }

        @Override
        protected String getTrustStorePath() {
            return "classpath:/truststore.jks";
        }

        @Override
        protected String getKeyStorePassword() {
            return "Test1234";
        }

        @Override
        protected String getKeyStorePath() {
            return "classpath:/keystore.jks";
        }
    }
}
