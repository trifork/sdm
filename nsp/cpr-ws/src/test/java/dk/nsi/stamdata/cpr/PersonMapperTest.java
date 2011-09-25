package dk.nsi.stamdata.cpr;

import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.models.cpr.Person;

import dk.nsi.stamdata.cpr.ws.PersonInformationStructureType;

public class PersonMapperTest {
    private PersonMapper mapper;
    private Person person;

    @Before
    public void setup() {
        mapper = new PersonMapper();

        person = new Person();
        person.setFornavn("Fornavn");
    }

    @Test
    public void mapsFirstname() throws DatatypeConfigurationException {
        PersonInformationStructureType jaxbPerson = mapper.map(person);
        Assert.assertEquals(person.getFornavn(), jaxbPerson.getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonGivenName());
    }
}
