import static org.junit.Assert.assertEquals;

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
    	String validationResult = getValidationResult("0708610089");
        assertEquals("NO ERRORS", validationResult);
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
