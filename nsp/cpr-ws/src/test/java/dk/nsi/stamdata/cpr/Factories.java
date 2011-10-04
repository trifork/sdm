package dk.nsi.stamdata.cpr;

import com.trifork.stamdata.models.cpr.Person;
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
		
		person.setGaeldendeCPR("2345678901");
		
		person.setFornavn("Peter");
		person.setMellemnavn("Sigurd");
		person.setEfternavn("Andersen");
		person.setNavnTilAdressering("Peter,Andersen");
		
		person.setCpr("1234567890");
		
		person.setKoen("M");
		
		person.setFoedselsdato(TWO_DAYS_AGO);
		
		person.setCoNavn("Søren Petersen");
		
		person.setKommuneKode("123");
		person.setVejKode("234");
		person.setHusnummer("10");
		person.setBygningsnummer("A");
		person.setLokalitet("Birkely");
		person.setVejnavn("Ørstedgade");
		person.setVejnavnTilAdressering("Østergd.");
		person.setEtage("12");
		person.setSideDoerNummer("tv");
		person.setFoedselsdatoMarkering(false);
		person.setStatus("02");
		person.setStatusDato(YESTERDAY);
		
		person.setPostnummer("6666");
		person.setPostdistrikt("Überwald");
		
		person.setNavnebeskyttelsestartdato(null);
		person.setNavnebeskyttelseslettedato(null);

		return person;
	}

	public static Person createPersonWithAddressProtection() {
		Person person = createPersonWithoutAddressProtection();

		person.setNavnebeskyttelsestartdato(YESTERDAY);
		person.setNavnebeskyttelseslettedato(TOMORROW);

		return person;
	}
}
