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

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.trifork.stamdata.jaxws.SealNamespaceResolver;
import com.trifork.stamdata.persistence.Record;
import com.trifork.stamdata.persistence.RecordPersister;
import com.trifork.stamdata.specs.SikredeRecordSpecs;
import com.trifork.stamdata.specs.YderregisterRecordSpecs;
import dk.nsi.stamdata.cpr.Factories;
import dk.nsi.stamdata.cpr.models.Person;
import dk.nsi.stamdata.dgws.DGWSHeaderUtil;
import dk.nsi.stamdata.dgws.SecurityWrapper;
import dk.nsi.stamdata.guice.GuiceTestRunner;
import oio.medcom.cprservice._1_0.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hisrc.hifaces20.testing.webappenvironment.testing.junit4.AbstractWebAppEnvironmentJUnit4Test;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(GuiceTestRunner.class)
public class DetGodeCPROpslag102IntegrationTest extends AbstractWebAppEnvironmentJUnit4Test {

    public static final QName DET_GODE_CPR_OPSLAG_102_SERVICE = new QName("urn:oio:medcom:cprservice:1.0.2", "DetGodeCPROpslagService");
    public static final String RANDOM_CVR = "87654321";

    @Inject
    private Session session;

    @Inject
    private Provider<Connection> connectionProvider;

    private DetGodeCPROpslag client;

    private GetPersonInformationIn request = new GetPersonInformationIn();
    private GetPersonInformationOut response;
    private GetPersonWithHealthCareInformationOut healthCareResponse;

    private GetPersonWithHealthCareInformationIn healthCareRequest = new GetPersonWithHealthCareInformationIn();

    private List<Record> yderRecords = Lists.newArrayList();
    private List<Person> persons = Lists.newArrayList();
    private List<Record> sikredeRecords = Lists.newArrayList();

    @Before
    public void setUp() throws MalformedURLException {
        URL wsdlLocation = new URL("http://localhost:8100/service/DetGodeCPROpslag-1.0.2?wsdl");
        DetGodeCPROpslagService serviceCatalog =
                new DetGodeCPROpslagService(wsdlLocation, DET_GODE_CPR_OPSLAG_102_SERVICE);
        serviceCatalog.setHandlerResolver(new SealNamespaceResolver());
        client = serviceCatalog.getDetGodeCPROpslag();
    }

    private void sendPersonRequest() throws Exception {
        Transaction t = session.beginTransaction();
        session.createQuery("DELETE FROM Person").executeUpdate();
        for (Person person : persons) session.persist(person);
        t.commit();

        SecurityWrapper securityHeaders = DGWSHeaderUtil.getVocesTrustedSecurityWrapper(RANDOM_CVR, "foo", "bar");
        response = client.getPersonInformation(securityHeaders.getSecurity(), securityHeaders.getMedcomHeader(), request);
    }

    @Test
    public void requestForExistingPersonGivesPersonInformation() throws Exception {
        Person person = Factories.createPerson();
        persons.add(person);
        request.setPersonCivilRegistrationIdentifier(person.getCpr());
        sendPersonRequest();

        assertThat(response.getPersonInformationStructure().getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonGivenName(), is(person.getFornavn()));
    }

    @Test
    public void requestPersonWithFullHealthcareInformation() throws Exception {
        Person person1 = Factories.createPerson();
        persons.add(person1);
        Person person2 = Factories.createPerson();
        persons.add(person1);

        Record yderRecord1 = Factories.createYderRecord("1234");
        yderRecords.add(yderRecord1);
        Record yderRecord2 = Factories.createYderRecord("4321");
        yderRecords.add(yderRecord2);

        Record sikredeRecord1 = Factories.createSikredeRecordFor(person1, yderRecord1, "9", new DateTime(2011, 10, 10, 0, 0));
        sikredeRecords.add(sikredeRecord1);
        Record sikredeRecord2 = Factories.createSikredeRecordFor(person2, yderRecord2, "4", new DateTime(2011, 10, 10, 0, 0));
        sikredeRecords.add(sikredeRecord2);
        // Having multiple persons ensures that we are selecting the
        // right one.

        healthCareRequest.setPersonCivilRegistrationIdentifier(person1.getCpr());
        sendHealthCareRequest();

        assertThat(healthCareResponse.getPersonWithHealthCareInformationStructure().getPersonInformationStructure().getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonGivenName(), is(person1.getFornavn()));
        assertThat(healthCareResponse.getPersonWithHealthCareInformationStructure().getPersonHealthCareInformationStructure().getAssociatedGeneralPractitionerStructure().getAssociatedGeneralPractitionerIdentifier().intValue(), is(1234));
        assertEquals(PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_9, healthCareResponse.getPersonWithHealthCareInformationStructure().getPersonHealthCareInformationStructure().getPersonPublicHealthInsurance().getPublicHealthInsuranceGroupIdentifier());
    }

    private void sendHealthCareRequest() throws Exception {
        Transaction t = session.beginTransaction();

        session.createQuery("DELETE FROM Person").executeUpdate();
        session.connection().createStatement().executeUpdate("DELETE FROM " + SikredeRecordSpecs.ENTRY_RECORD_SPEC.getTable());
        session.connection().createStatement().executeUpdate("DELETE FROM Yderregister");

        for (Person person : persons) session.persist(person);

        t.commit();

        for (Record sikredeRecord: sikredeRecords) {
            // RecordPersister should be injected
            RecordPersister recordPersister = new RecordPersister(connectionProvider, Instant.now());
            recordPersister.persist(sikredeRecord, SikredeRecordSpecs.ENTRY_RECORD_SPEC);
        }

        for (Record yderRecord: yderRecords) {
            // RecordPersister should be injected
            RecordPersister recordPersister = new RecordPersister(connectionProvider, Instant.now());
            recordPersister.persist(yderRecord, YderregisterRecordSpecs.YDER_RECORD_TYPE);
        }
        SecurityWrapper securityHeaders = DGWSHeaderUtil.getVocesTrustedSecurityWrapper(RANDOM_CVR, "foo", "bar");
        healthCareResponse = client.getPersonWithHealthCareInformation(securityHeaders.getSecurity(), securityHeaders.getMedcomHeader(), healthCareRequest);
    }
}
