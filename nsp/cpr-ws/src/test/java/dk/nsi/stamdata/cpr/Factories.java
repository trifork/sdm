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

import com.trifork.stamdata.models.cpr.Person;
import com.trifork.stamdata.models.sikrede.SikredeYderRelation;
import com.trifork.stamdata.models.sikrede.Yderregister;

import org.joda.time.DateTime;

import java.util.Date;

public class Factories
{
	public static final Date TWO_DAYS_AGO = DateTime.now().minusDays(2).toDate();
	public static final Date YESTERDAY = DateTime.now().minusDays(1).toDate();
	public static final Date TOMORROW = DateTime.now().plusDays(1).toDate();

	public static Person createPersonWithoutAddressProtection()
	{
		Person person = new Person();
		
		person.setGaeldendeCPR("0102852469");
		
		person.setFornavn("Peter");
		person.setMellemnavn("Sigurd");
		person.setEfternavn("Andersen");
		person.setNavnTilAdressering("Peter,Andersen");
		
		person.setCpr("0204953569");
		
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
		relation.setCpr("0204953569");
		relation.setYdernummer(1234);
		relation.setGruppeKodeIkraftDato(YESTERDAY);
		relation.setGruppekodeRegistreringDato(TWO_DAYS_AGO);
		relation.setId("dddsdsdssddssd");
		relation.setSikringsgruppeKode('2');
		relation.setType("C");
		relation.setYdernummerIkraftDato(YESTERDAY);
		relation.setYdernummerRegistreringDato(TWO_DAYS_AGO);
		relation.setModifiedDate(TWO_DAYS_AGO);
		relation.setCreatedDate(TWO_DAYS_AGO);
		relation.setValidFrom(YESTERDAY);
		relation.setValidTo(TOMORROW);
		return relation;
	}
	
	public static Yderregister createYderregister()
	{
		Yderregister yderregister = new Yderregister();
		
		yderregister.setBynavn("Århus");
		yderregister.setEmail("test@example.com");
		yderregister.setNavn("Klinikken");
		yderregister.setNummer(1234);
		yderregister.setPostnummer("8000");
		yderregister.setTelefon("12345678");
		yderregister.setVejnavn("Margrethepladsen 44, 8000 Århus");

		yderregister.setModifiedDate(TWO_DAYS_AGO);
		yderregister.setCreatedDate(TWO_DAYS_AGO);
		yderregister.setValidFrom(YESTERDAY);
		yderregister.setValidTo(TOMORROW);

		return yderregister;
	}

	public static Person createPersonWithAddressProtection() {
		Person person = createPersonWithoutAddressProtection();

		person.setNavnebeskyttelsestartdato(YESTERDAY);
		person.setNavnebeskyttelseslettedato(TOMORROW);

		return person;
	}
}
