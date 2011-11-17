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
package dk.nsi.stamdata.cpr.medcom;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.soap.SOAPFaultException;

import com.trifork.stamdata.specs.SikredeRecordSpecs;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hisrc.hifaces20.testing.webappenvironment.testing.junit4.AbstractWebAppEnvironmentJUnit4Test;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.trifork.stamdata.jaxws.SealNamespaceResolver;
import com.trifork.stamdata.persistence.Record;
import com.trifork.stamdata.persistence.RecordMySQLTableGenerator;
import com.trifork.stamdata.persistence.RecordPersister;
import com.trifork.stamdata.persistence.RecordSpecification;

import dk.nsi.stamdata.cpr.Factories;
import dk.nsi.stamdata.cpr.models.Person;
import dk.nsi.stamdata.cpr.models.SikredeYderRelation;
import dk.nsi.stamdata.cpr.models.Yderregister;
import dk.nsi.stamdata.dgws.DGWSHeaderUtil;
import dk.nsi.stamdata.dgws.SecurityWrapper;
import dk.nsi.stamdata.guice.GuiceTestRunner;
import dk.nsi.stamdata.jaxws.generated.DetGodeCPROpslag;
import dk.nsi.stamdata.jaxws.generated.DetGodeCPROpslagService;
import dk.nsi.stamdata.jaxws.generated.GetPersonInformationIn;
import dk.nsi.stamdata.jaxws.generated.GetPersonInformationOut;
import dk.nsi.stamdata.jaxws.generated.GetPersonWithHealthCareInformationIn;
import dk.nsi.stamdata.jaxws.generated.GetPersonWithHealthCareInformationOut;
import dk.nsi.stamdata.jaxws.generated.PublicHealthInsuranceGroupIdentifierType;


@RunWith(GuiceTestRunner.class)
public class DetGodeCPROpslagIntegrationTest extends AbstractWebAppEnvironmentJUnit4Test
{

    public static final QName DET_GODE_CPR_OPSLAG_SERVICE = new QName("http://rep.oio.dk/medcom.sundcom.dk/xml/wsdl/2007/06/28/", "DetGodeCPROpslagService");
    public static final String RANDOM_CVR = "87654321";

    private DetGodeCPROpslag client;

    @Inject
    private Session session;

    private GetPersonInformationIn request = new GetPersonInformationIn();
    private GetPersonWithHealthCareInformationIn healthCareRequest = new GetPersonWithHealthCareInformationIn();

    private List<Yderregister> yderregistre = Lists.newArrayList();
    private List<SikredeYderRelation> yderrelationer = Lists.newArrayList();
    private List<Person> persons = Lists.newArrayList();
    private List<Record> sikredeRecords = Lists.newArrayList();

    private GetPersonInformationOut response;
    private GetPersonWithHealthCareInformationOut healthCareResponse;


    @Before
    public void setUp() throws MalformedURLException
    {

        // This client is used to access the web-service.

        URL wsdlLocation = new URL("http://localhost:8100/service/DetGodeCPROpslag?wsdl");
        DetGodeCPROpslagService serviceCatalog = new DetGodeCPROpslagService(wsdlLocation, DET_GODE_CPR_OPSLAG_SERVICE);

        serviceCatalog.setHandlerResolver(new SealNamespaceResolver());

        client = serviceCatalog.getDetGodeCPROpslag();

        org.hibernate.classic.Session session1 = session.getSessionFactory().openSession();
        Transaction transaction = session1.beginTransaction();
        SQLQuery sqlQuery = session1.createSQLQuery("REPLACE INTO whitelist_config (component_name, cvr) VALUES(?,?)");
        sqlQuery.setString(0, "dgcpr");
        sqlQuery.setString(1, "87654321");
        sqlQuery.executeUpdate();
        transaction.commit();
        session1.close();

    }

    @Before
    public void setupDatabase() throws SQLException
    {
        String sqlSchema = RecordMySQLTableGenerator.createSqlSchema(SikredeRecordSpecs.ENTRY_RECORD_SPEC);
        session.beginTransaction();
        Connection connection = session.connection();
        Statement statement = connection.createStatement();
        statement.executeUpdate("DROP TABLE IF EXISTS SikredeGenerated");
        statement.executeUpdate(sqlSchema);
    }

    @Test(expected = SOAPFaultException.class)
    public void requestWithoutPersonIdentifierGivesSenderSoapFault() throws Exception
    {
        request.setPersonCivilRegistrationIdentifier(null);

        sendPersonRequest();
    }


    @Test(expected = SOAPFaultException.class)
    public void requestWithNonExistingPersonIdentifierGivesSenderSoapFault() throws Exception
    {
        persons.add(Factories.createPersonWithCPR("2905852467"));

        request.setPersonCivilRegistrationIdentifier("2905852569");

        sendPersonRequest();
    }


    @Test
    public void requestForPersonWithActiveProtectionResultsInProtectedData() throws Exception
    {
        Person person = Factories.createPersonWithAddressProtection();
        persons.add(person);

        request.setPersonCivilRegistrationIdentifier(person.getCpr());

        sendPersonRequest();

        assertThat(response.getPersonInformationStructure().getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonGivenName(), is("ADRESSEBESKYTTET"));
    }


    @Test
    public void requestForExistingPersonGivesPersonInformation() throws Exception
    {
        Person person = Factories.createPerson();
        persons.add(person);
     
        request.setPersonCivilRegistrationIdentifier(person.getCpr());

        sendPersonRequest();

        assertThat(response.getPersonInformationStructure().getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonGivenName(), is(person.getFornavn()));
    }

    @Test
    public void requestPersonWithHealthcareInformationButNoPublicHealthInsuranceInformation() throws Exception
    {
        Person person1 = Factories.createPerson();
        persons.add(person1);
        Person person2 = Factories.createPerson();
        persons.add(person1);

        Yderregister yder1 = Factories.createYderregister();
        yderregistre.add(yder1);
        Yderregister yder2 = Factories.createYderregister();
        yderregistre.add(yder2);

        yderrelationer.add(Factories.createSikredeYderRelationFor(person1, yder1));
        yderrelationer.add(Factories.createSikredeYderRelationFor(person2, yder2));
        
        // Having multiple persons ensures that we are selecting the
        // right one.

        healthCareRequest.setPersonCivilRegistrationIdentifier(person1.getCpr());

        sendHealthCareRequest();

        assertThat(healthCareResponse.getPersonWithHealthCareInformationStructure().getPersonInformationStructure().getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonGivenName(), is(person1.getFornavn()));
        assertThat(healthCareResponse.getPersonWithHealthCareInformationStructure().getPersonHealthCareInformationStructure().getAssociatedGeneralPractitionerStructure().getAssociatedGeneralPractitionerIdentifier().intValue(), is(yder1.getNummer()));
        
        // If the sygesikringsgruppe is unknown we defaults to 1
        assertEquals(PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_1, healthCareResponse.getPersonWithHealthCareInformationStructure().getPersonHealthCareInformationStructure().getPersonPublicHealthInsurance().getPublicHealthInsuranceGroupIdentifier());
    }

    @Test
    public void requestPersonWithFullHealthcareInformation() throws Exception
    {
        Person person1 = Factories.createPerson();
        persons.add(person1);
        Person person2 = Factories.createPerson();
        persons.add(person1);
        
        Yderregister yder1 = Factories.createYderregister();
        yderregistre.add(yder1);
        Yderregister yder2 = Factories.createYderregister();
        yderregistre.add(yder2);
        
        yderrelationer.add(Factories.createSikredeYderRelationFor(person1, yder1));
        yderrelationer.add(Factories.createSikredeYderRelationFor(person2, yder2));
        
        Record createSikredeRecordFor = Factories.createSikredeRecordFor(person1, yder1, "2", new DateTime(Factories.YESTERDAY));
        sikredeRecords.add(createSikredeRecordFor);
        
        // Having multiple persons ensures that we are selecting the
        // right one.
        
        healthCareRequest.setPersonCivilRegistrationIdentifier(person1.getCpr());
        
        sendHealthCareRequest();
        
        assertThat(healthCareResponse.getPersonWithHealthCareInformationStructure().getPersonInformationStructure().getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonGivenName(), is(person1.getFornavn()));
        assertThat(healthCareResponse.getPersonWithHealthCareInformationStructure().getPersonHealthCareInformationStructure().getAssociatedGeneralPractitionerStructure().getAssociatedGeneralPractitionerIdentifier().intValue(), is(yder1.getNummer()));
        assertEquals(PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_2, healthCareResponse.getPersonWithHealthCareInformationStructure().getPersonHealthCareInformationStructure().getPersonPublicHealthInsurance().getPublicHealthInsuranceGroupIdentifier());
    }
    
    @Test
    public void requestForExistingPersonWhereHealthCareInformationCouldNotBeFoundReturnsMockData() throws Exception
    {
        Person person = Factories.createPerson();
        persons.add(person);
        
        healthCareRequest.setPersonCivilRegistrationIdentifier(person.getCpr());
        
        sendHealthCareRequest();
        
        assertThat(healthCareResponse.getPersonWithHealthCareInformationStructure().getPersonInformationStructure().getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonGivenName(), is(person.getFornavn()));
        assertThat(healthCareResponse.getPersonWithHealthCareInformationStructure().getPersonHealthCareInformationStructure().getAssociatedGeneralPractitionerStructure().getAssociatedGeneralPractitionerOrganisationName(), is("UKENDT"));
    }


    private void sendPersonRequest() throws Exception
    {
        Transaction t = session.beginTransaction();
        session.createQuery("DELETE FROM Person").executeUpdate();
        for (Person person : persons) session.persist(person);
        t.commit();

        SecurityWrapper securityHeaders = DGWSHeaderUtil.getVocesTrustedSecurityWrapper(RANDOM_CVR, "foo", "bar");
        response = client.getPersonInformation(securityHeaders.getSecurity(), securityHeaders.getMedcomHeader(), request);
    }


    private void sendHealthCareRequest() throws Exception
    {
        Transaction t = session.beginTransaction();

        session.createQuery("DELETE FROM Person").executeUpdate();
        session.createQuery("DELETE FROM SikredeYderRelation").executeUpdate();
        session.createQuery("DELETE FROM Yderregister").executeUpdate();
        session.connection().createStatement().executeUpdate("DELETE FROM SikredeGenerated");

        for (Person person : persons) session.persist(person);
        for (Yderregister yderregister : yderregistre) session.persist(yderregister);
        for (SikredeYderRelation relation : yderrelationer) session.persist(relation);
                
        for (Record sikredeRecord: sikredeRecords)
        {
            // RecordPersister should be injected
            RecordPersister recordPersister = new RecordPersister(SikredeRecordSpecs.ENTRY_RECORD_SPEC, session.connection());
            recordPersister.persist(sikredeRecord, "CPRnr", new Instant(Factories.YESTERDAY));
        }

        t.commit();

        SecurityWrapper securityHeaders = DGWSHeaderUtil.getVocesTrustedSecurityWrapper(RANDOM_CVR, "foo", "bar");
        healthCareResponse = client.getPersonWithHealthCareInformation(securityHeaders.getSecurity(), securityHeaders.getMedcomHeader(), healthCareRequest);
    }
}
