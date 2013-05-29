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

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.soap.SOAPFaultException;

import com.google.inject.Provider;
import com.trifork.stamdata.specs.SikredeRecordSpecs;
import com.trifork.stamdata.specs.YderregisterRecordSpecs;

import dk.nsi.stamdata.testing.RequestHelper;
import dk.oio.rep.medcom_sundcom_dk.xml.schemas._2007._02._01.PublicHealthInsuranceGroupIdentifierType;
import dk.oio.rep.medcom_sundcom_dk.xml.wsdl._2007._06._28.*;
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

import dk.nsi.stamdata.cpr.Factories;
import dk.nsi.stamdata.cpr.models.Person;
import dk.nsi.stamdata.dgws.DGWSHeaderUtil;
import dk.nsi.stamdata.dgws.SecurityWrapper;
import dk.nsi.stamdata.guice.GuiceTestRunner;

@RunWith(GuiceTestRunner.class)
public class DetGodeCPROpslagIntegrationTest extends AbstractWebAppEnvironmentJUnit4Test
{

    public static final QName DET_GODE_CPR_OPSLAG_SERVICE = new QName("http://rep.oio.dk/medcom.sundcom.dk/xml/wsdl/2007/06/28/", "DetGodeCPROpslagService");
    public static final String RANDOM_CVR = "87654321";

    private DetGodeCPROpslag client;

    @Inject
    private Session session;

    @Inject
    private Provider<Connection> connectionProvider;

    private GetPersonInformationIn request = new GetPersonInformationIn();
    private GetPersonWithHealthCareInformationIn healthCareRequest = new GetPersonWithHealthCareInformationIn();

    private List<Person> persons = Lists.newArrayList();
    private List<Record> sikredeRecords = Lists.newArrayList();
    private List<Record> yderRecords = Lists.newArrayList();

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
    }

    @Before
    public void setupDatabase() throws SQLException
    {
        String sqlSchema = RecordMySQLTableGenerator.createSqlSchema(SikredeRecordSpecs.ENTRY_RECORD_SPEC);
        session.beginTransaction();
        Connection connection = session.connection();
        Statement statement = connection.createStatement();
        statement.executeUpdate("DROP TABLE IF EXISTS " + SikredeRecordSpecs.ENTRY_RECORD_SPEC.getTable());
        statement.executeUpdate(sqlSchema);
    }


    @Test
    public void expiredIdCardShouldFail() throws IOException {
        String expiredRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://www.sdsd.dk/dgws/2010/08\" xmlns:ns1=\"http://www.sdsd.dk/dgws/2012/06\" xmlns:ns2=\"http://vaccinationsregister.dk/schemas/2010/07/01\">\n" +
                "   <soapenv:Header>\n" +
                "      <Security xmlns=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n" +
                "         <Timestamp xmlns=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">\n" +
                "            <Created>2013-01-16T13:48:56+01:00</Created>\n" +
                "         </Timestamp>\n" +
                "<saml:Assertion xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" IssueInstant=\"2013-01-16T12:43:58Z\" Version=\"2.0\" id=\"IDCard\"><saml:Issuer>TESTSTS</saml:Issuer><saml:Subject><saml:NameID Format=\"medcom:cvrnumber\">19343634</saml:NameID><saml:SubjectConfirmation><saml:ConfirmationMethod>urn:oasis:names:tc:SAML:2.0:cm:holder-of-key</saml:ConfirmationMethod><saml:SubjectConfirmationData><ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:KeyName>OCESSignature</ds:KeyName></ds:KeyInfo></saml:SubjectConfirmationData></saml:SubjectConfirmation></saml:Subject><saml:Conditions NotBefore=\"2013-01-16T12:43:58Z\" NotOnOrAfter=\"2013-01-17T12:43:58Z\"/><saml:AttributeStatement id=\"IDCardData\"><saml:Attribute Name=\"sosi:IDCardID\"><saml:AttributeValue>nECTDy5k+1xP1LyDE/RSxA==</saml:AttributeValue></saml:Attribute><saml:Attribute Name=\"sosi:IDCardVersion\"><saml:AttributeValue>1.0.1</saml:AttributeValue></saml:Attribute><saml:Attribute Name=\"sosi:IDCardType\"><saml:AttributeValue>system</saml:AttributeValue></saml:Attribute><saml:Attribute Name=\"sosi:AuthenticationLevel\"><saml:AttributeValue>3</saml:AttributeValue></saml:Attribute><saml:Attribute Name=\"sosi:OCESCertHash\"><saml:AttributeValue>VMLCDk1Rl7KVcK6avFaq8lhhNuc=</saml:AttributeValue></saml:Attribute></saml:AttributeStatement><saml:AttributeStatement id=\"SystemLog\"><saml:Attribute Name=\"medcom:ITSystemName\"><saml:AttributeValue>SOSITEST</saml:AttributeValue></saml:Attribute><saml:Attribute Name=\"medcom:CareProviderID\" NameFormat=\"medcom:cvrnumber\"><saml:AttributeValue>19343634</saml:AttributeValue></saml:Attribute><saml:Attribute Name=\"medcom:CareProviderName\"><saml:AttributeValue>test</saml:AttributeValue></saml:Attribute></saml:AttributeStatement><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" id=\"OCESSignature\"><ds:SignedInfo><ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/><ds:Reference URI=\"#IDCard\"><ds:Transforms><ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/><ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/></ds:Transforms><ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/><ds:DigestValue>llK4jhnQy2Wp5m1h8ApIlfoVPzQ=</ds:DigestValue></ds:Reference></ds:SignedInfo><ds:SignatureValue>GJw2rDm4LNm5Rx7shJdeXIW1otf3hwn52Mu5qu7DosS7Y/kZ6Vz/g3wT7XlNFYWP4lH8rmkfWLMVBp4pZvwNUwKLL//o1o0ZOTqYewBojm3ew/PykVNCQoHFlkwLQ9tjsjr3SpwRGXYMohWpmwbNIOcFLFx0o7hxTkT0cQlBX9g=</ds:SignatureValue><ds:KeyInfo><ds:X509Data><ds:X509Certificate>MIIFBjCCBG+gAwIBAgIEQDhHTzANBgkqhkiG9w0BAQUFADA/MQswCQYDVQQGEwJESzEMMAoGA1UEChMDVERDMSIwIAYDVQQDExlUREMgT0NFUyBTeXN0ZW10ZXN0IENBIElJMB4XDTEyMTExMjA4NTQzM1oXDTE0MTExMjA5MjQzM1owgYMxCzAJBgNVBAYTAkRLMSgwJgYDVQQKEx9EYW5za2UgUmVnaW9uZXIgLy8gQ1ZSOjU1ODMyMjE4MUowIQYDVQQDExpEYW5za2UgUmVnaW9uZXIgLSBTT1NJIFNUUzAlBgNVBAUTHkNWUjo1NTgzMjIxOC1VSUQ6MTE2MzQ0NzM2ODYyNzCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAgafDW9G/4E+AX55A5Ai3pEPhEX7eC1qSX4e5KsQpa3yvpJnCgsUNRwohlljREpCxiPHVgCpydSAwRUslXioir4rJL5nfIK6kfZVLXDN0nsfblGrQeLcjkCCj2X4sxD5uDHmvf7gaPFDt6Vjda2M5yqTwrifdt9KEtDwxz6VE9JMCAwEAAaOCAsgwggLEMA4GA1UdDwEB/wQEAwIDuDArBgNVHRAEJDAigA8yMDEyMTExMjA4NTQzM1qBDzIwMTQxMTEyMDkyNDMzWjBGBggrBgEFBQcBAQQ6MDgwNgYIKwYBBQUHMAGGKmh0dHA6Ly90ZXN0Lm9jc3AuY2VydGlmaWthdC5kay9vY3NwL3N0YXR1czCCAQMGA1UdIASB+zCB+DCB9QYJKQEBAQEBAQEDMIHnMC8GCCsGAQUFBwIBFiNodHRwOi8vd3d3LmNlcnRpZmlrYXQuZGsvcmVwb3NpdG9yeTCBswYIKwYBBQUHAgIwgaYwChYDVERDMAMCAQEagZdUREMgVGVzdCBDZXJ0aWZpa2F0ZXIgZnJhIGRlbm5lIENBIHVkc3RlZGVzIHVuZGVyIE9JRCAxLjEuMS4xLjEuMS4xLjEuMS4zLiBUREMgVGVzdCBDZXJ0aWZpY2F0ZXMgZnJvbSB0aGlzIENBIGFyZSBpc3N1ZWQgdW5kZXIgT0lEIDEuMS4xLjEuMS4xLjEuMS4xLjMuMBcGCWCGSAGG+EIBDQQKFghvcmdhbldlYjAdBgNVHREEFjAUgRJkcmlmdHZhZ3RAZGFuaWQuZGswgZcGA1UdHwSBjzCBjDBXoFWgU6RRME8xCzAJBgNVBAYTAkRLMQwwCgYDVQQKEwNUREMxIjAgBgNVBAMTGVREQyBPQ0VTIFN5c3RlbXRlc3QgQ0EgSUkxDjAMBgNVBAMTBUNSTDM2MDGgL6AthitodHRwOi8vdGVzdC5jcmwub2Nlcy5jZXJ0aWZpa2F0LmRrL29jZXMuY3JsMB8GA1UdIwQYMBaAFByYCUcaTDi5EMUEKVvx9E6Aasx+MB0GA1UdDgQWBBS/1KpRYuZlgVkjZVDSyNjPlzavnTAJBgNVHRMEAjAAMBkGCSqGSIb2fQdBAAQMMAobBFY3LjEDAgOoMA0GCSqGSIb3DQEBBQUAA4GBAA+JHmRfW6w4xTTcd7/Z0RXh9VXkNAvcmUKlKFtcVlUNSFGE4NtASzbhnzisXueEQEWqRSuDi2zUjAUpvR9yPDj+Wa+wccasK6hzzHxNPZADtdGFmSGryWQilG8BW2UeAecRkKuEpY3Kf7FTAQouBbfWh9onqSPBbGhfAG25NE6u</ds:X509Certificate></ds:X509Data></ds:KeyInfo></ds:Signature></saml:Assertion>      </Security>\n" +
                "      <Header xmlns=\"http://www.medcom.dk/dgws/2006/04/dgws-1.0.xsd\">\n" +
                "         <SecurityLevel>3</SecurityLevel>\n" +
                "         <TimeOut>1440</TimeOut>\n" +
                "         <Linking>\n" +
                "            <FlowID>18298898b9f142af9f938e57df897127</FlowID>\n" +
                "            <MessageID>af879c6ebb5a4a9bb5c5fb2622f294f1</MessageID>\n" +
                "         </Linking>\n" +
                "         <FlowStatus>flow_running</FlowStatus>\n" +
                "         <Priority>RUTINE</Priority>\n" +
                "         <RequireNonRepudiationReceipt>no</RequireNonRepudiationReceipt>\n" +
                "      </Header>\n" +
                "   </soapenv:Header>\n" +
                "   <soapenv:Body>\n" +
                "<sces:getPersonInformationIn xmlns:sces=\"http://rep.oio.dk/medcom.sundcom.dk/xml/wsdl/2007/06/28/\"><cpr:PersonCivilRegistrationIdentifier xmlns:cpr=\"http://rep.oio.dk/cpr.dk/xml/schemas/core/2005/03/18/\">0101010005</cpr:PersonCivilRegistrationIdentifier></sces:getPersonInformationIn>   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        String endpoint = this.getWebAppEnvironment().getBaseUrl() + "/service/DetGodeCPROpslag";
        String reply = RequestHelper.sendRequest(endpoint, "", expiredRequest, false);
        assertTrue(reply.contains("IDCard expired."));
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

        Record yderRecord1 = Factories.createYderRecord("1234");
        yderRecords.add(yderRecord1);
        Record yderRecord2 = Factories.createYderRecord("4321");
        yderRecords.add(yderRecord2);
        
        Record sikredeRecord1 = Factories.createSikredeRecordFor(person1, yderRecord1, "1", new DateTime(2011, 10, 10, 0, 0));
        sikredeRecords.add(sikredeRecord1);
        Record sikredeRecord2 = Factories.createSikredeRecordFor(person2, yderRecord2, "2", new DateTime(2011, 10, 10, 0, 0));
        sikredeRecords.add(sikredeRecord2);
        
        // Having multiple persons ensures that we are selecting the
        // right one.

        healthCareRequest.setPersonCivilRegistrationIdentifier(person1.getCpr());

        sendHealthCareRequest();

        assertThat(healthCareResponse.getPersonWithHealthCareInformationStructure().getPersonInformationStructure().getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonGivenName(), is(person1.getFornavn()));
        assertThat(healthCareResponse.getPersonWithHealthCareInformationStructure().getPersonHealthCareInformationStructure().getAssociatedGeneralPractitionerStructure().getAssociatedGeneralPractitionerIdentifier().intValue(), is(1234));
        assertThat(healthCareResponse.getPersonWithHealthCareInformationStructure().getPersonHealthCareInformationStructure().getAssociatedGeneralPractitionerStructure().getPostCodeIdentifier(), is("8000"));
        assertEquals(PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_1, healthCareResponse.getPersonWithHealthCareInformationStructure().getPersonHealthCareInformationStructure().getPersonPublicHealthInsurance().getPublicHealthInsuranceGroupIdentifier());
    }

    @Test
    public void requestPersonWithFullHealthcareInformation() throws Exception
    {
        Person person1 = Factories.createPerson();
        persons.add(person1);
        Person person2 = Factories.createPerson();
        persons.add(person1);
        
        Record yderRecord1 = Factories.createYderRecord("1234");
        yderRecords.add(yderRecord1);
        Record yderRecord2 = Factories.createYderRecord("4321");
        yderRecords.add(yderRecord2);
        
        Record sikredeRecord1 = Factories.createSikredeRecordFor(person1, yderRecord1, "2", new DateTime(2011, 10, 10, 0, 0));
        sikredeRecords.add(sikredeRecord1);
        Record sikredeRecord2 = Factories.createSikredeRecordFor(person2, yderRecord2, "1", new DateTime(2011, 10, 10, 0, 0));
        sikredeRecords.add(sikredeRecord2);
        
        // Having multiple persons ensures that we are selecting the
        // right one.
        
        healthCareRequest.setPersonCivilRegistrationIdentifier(person1.getCpr());
        
        sendHealthCareRequest();
        
        assertThat(healthCareResponse.getPersonWithHealthCareInformationStructure().getPersonInformationStructure().getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonGivenName(), is(person1.getFornavn()));
        assertThat(healthCareResponse.getPersonWithHealthCareInformationStructure().getPersonHealthCareInformationStructure().getAssociatedGeneralPractitionerStructure().getAssociatedGeneralPractitionerIdentifier().intValue(), is(1234));
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
        session.connection().createStatement().executeUpdate("DELETE FROM " + SikredeRecordSpecs.ENTRY_RECORD_SPEC.getTable());
        session.connection().createStatement().executeUpdate("DELETE FROM Yderregister");

        for (Person person : persons) session.persist(person);

        t.commit();
                
        for (Record sikredeRecord: sikredeRecords)
        {
            // RecordPersister should be injected
            RecordPersister recordPersister = new RecordPersister(connectionProvider, Instant.now());
            recordPersister.persist(sikredeRecord, SikredeRecordSpecs.ENTRY_RECORD_SPEC);
        }
        
        for (Record yderRecord: yderRecords)
        {
            // RecordPersister should be injected
            RecordPersister recordPersister = new RecordPersister(connectionProvider, Instant.now());
            recordPersister.persist(yderRecord, YderregisterRecordSpecs.YDER_RECORD_TYPE);
        }

        SecurityWrapper securityHeaders = DGWSHeaderUtil.getVocesTrustedSecurityWrapper(RANDOM_CVR, "foo", "bar");
        healthCareResponse = client.getPersonWithHealthCareInformation(securityHeaders.getSecurity(), securityHeaders.getMedcomHeader(), healthCareRequest);
    }
}
