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
package dk.nsi.stamdata.cpr;

import com.trifork.stamdata.specs.SikredeRecordSpecs;
import com.trifork.stamdata.specs.YderregisterRecordSpecs;

import dk.nsi.stamdata.cpr.models.Person;
import dk.nsi.stamdata.cpr.models.SikredeYderRelation;
import dk.nsi.stamdata.cpr.models.Yderregister;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.trifork.stamdata.persistence.Record;
import com.trifork.stamdata.persistence.RecordBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


public class Factories
{
    private static Random random = new Random();
    
    public static final Date YEAR_2000 = new DateTime(2000, 1, 1, 0, 0, 0).toDate();
    public static final Date YEAR_1999 = new DateTime(1999, 1, 1, 0, 0, 0).toDate();
    
    public static final Date TWO_DAYS_AGO = DateTime.now().minusDays(2).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).toDate();
    public static final Date YESTERDAY = DateTime.now().minusDays(1).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).toDate();
    public static final Date TOMORROW = DateTime.now().plusDays(1).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).toDate();
    public static final Date IN_TWO_DAYS = DateTime.now().plusDays(2).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).toDate();

    
    public static String generateRandomCPR()
    {
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, random.nextInt(98) + 1);
        cal.set(Calendar.MONTH, random.nextInt(11));
        cal.set(Calendar.DAY_OF_YEAR, random.nextInt(364) + 1);
        int no = random.nextInt(9999);

        return new SimpleDateFormat("ddMMyy").format(cal.getTime()) + String.format("%04d", no);
    }


    public static Person createPersonWithCPR(String cpr)
    {
        Person person = createPerson();
        person.setCpr(cpr);
        return person;
    }


    public static Person createPersonWithBirthday(Date birthday)
    {
        Person person = createPerson();
        person.setFoedselsdato(birthday);
        return person;
    }


    public static Person createPerson()
    {
        Person person = new Person();

        person.setFornavn("Peter");
        person.setMellemnavn("Sigurd");
        person.setEfternavn("Andersen");
        person.setNavnTilAdressering("Peter,Andersen");

        person.setCpr(generateRandomCPR());
        
        // Yes, they are not identical.
        person.setGaeldendeCPR(generateRandomCPR());

        person.setKoen("M");

        person.setFoedselsdato(TWO_DAYS_AGO);

        person.setCoNavn("Søren Petersen");

        person.setKommuneKode("0461");
        person.setVejKode("0234");
        person.setHusnummer("10");
        person.setBygningsnummer("A");
        person.setLokalitet("Birkely");
        person.setVejnavn("Ørstedgade");
        person.setVejnavnTilAdressering("Østergd.");
        person.setEtage("12");
        person.setSideDoerNummer("tv");
        person.setFoedselsdatoMarkering(false);
        person.setStatus("01");
        person.setStatusDato(YESTERDAY);

        person.setPostnummer("6666");
        person.setPostdistrikt("Überwald");

        person.setNavnebeskyttelsestartdato(null);
        person.setNavnebeskyttelseslettedato(null);

        person.setModifiedDate(TWO_DAYS_AGO);
        person.setCreatedDate(TWO_DAYS_AGO);
        person.setValidFrom(YESTERDAY);
        person.setValidTo(TOMORROW);

        return person;
    }


    public static SikredeYderRelation createSikredeYderRelation()
    {
        SikredeYderRelation relation = new SikredeYderRelation();
        relation.setCpr(generateRandomCPR());
        relation.setYdernummer(1234);
        relation.setGruppeKodeIkraftDato(YESTERDAY);
        relation.setGruppekodeRegistreringDato(TWO_DAYS_AGO);
        relation.setSikringsgruppeKode('2');
        relation.setType("C");
        relation.setId(relation.getCpr() + "-" + relation.getType());
        relation.setYdernummerIkraftDato(YESTERDAY);
        relation.setYdernummerRegistreringDato(TWO_DAYS_AGO);
        relation.setModifiedDate(TWO_DAYS_AGO);
        relation.setCreatedDate(TWO_DAYS_AGO);
        relation.setValidFrom(YESTERDAY);
        relation.setValidTo(TOMORROW);
        return relation;
    }


    public static SikredeYderRelation createSikredeYderRelationFor(Person person, Yderregister register)
    {
        SikredeYderRelation relation = createSikredeYderRelation();
        
        relation.setCpr(person.getCpr());
        relation.setYdernummer(register.getNummer());
        relation.setId(relation.getCpr() + "-" + relation.getType());
        
        return relation;
    }


    public static Yderregister createYderregister()
    {
        Yderregister yderregister = new Yderregister();

        yderregister.setBynavn("Århus");
        yderregister.setEmail("test@example.com");
        yderregister.setNavn("Klinikken");
        yderregister.setNummer(random.nextInt(9999));
        yderregister.setPostnummer("8000");
        yderregister.setTelefon("12345678");
        yderregister.setVejnavn("Margrethepladsen 44, 8000 Århus");

        yderregister.setModifiedDate(TWO_DAYS_AGO);
        yderregister.setCreatedDate(TWO_DAYS_AGO);
        yderregister.setValidFrom(YESTERDAY);
        yderregister.setValidTo(TOMORROW);

        return yderregister;
    }


    public static Person createPersonWithAddressProtection()
    {
        Person person = createPerson();

        person.setNavnebeskyttelsestartdato(YESTERDAY);
        person.setNavnebeskyttelseslettedato(TOMORROW);

        return person;
    }


    public static Record createSikredeRecordFor(Person person, Record yderRecord, String groupCode, DateTime ikraftDato)
    {
        RecordBuilder builder = new RecordBuilder(SikredeRecordSpecs.ENTRY_RECORD_SPEC);
        builder
            .field("CPRnr", person.getCpr())
            .field("SYdernr", yderRecord.get("YdernrYder"))
            .field("SSikrGrpKode", groupCode)
            .field("SIkraftDatoGrp", sikredeRecordDateString(ikraftDato));
        return builder.addDummyFieldsAndBuild();
    }
    
    public static Record createYderRecord(String ydernummer)
    {
        RecordBuilder builder = new RecordBuilder(YderregisterRecordSpecs.YDER_RECORD_TYPE)
                                    .field("YdernrYder", ydernummer)
                                    .field("PrakBetegn", "Klinikken")
                                    .field("PostdistYder", "Århus")
                                    .field("EmailYder", "test@example.com")
                                    .field("PostnrYder", "8000")
                                    .field("AdrYder", "Margrethepladsen 44, 8000 Århus")
                                    .field("HvdTlf", "12345678");
        return builder.addDummyFieldsAndBuild();
    }
    
    private static String sikredeRecordDateString(DateTime date) 
    {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
        return formatter.print(date);
    }
}
