package com.trifork.stamdata.lookup.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.classic.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.trifork.stamdata.replication.DatabaseHelper;
import com.trifork.stamdata.util.DateUtils;
import com.trifork.stamdata.views.cpr.BarnRelation;
import com.trifork.stamdata.views.cpr.Beskyttelse;
import com.trifork.stamdata.views.cpr.Foedselsregistreringsoplysninger;
import com.trifork.stamdata.views.cpr.Folkekirkeoplysninger;
import com.trifork.stamdata.views.cpr.ForaeldremyndighedsRelation;
import com.trifork.stamdata.views.cpr.MorOgFaroplysninger;
import com.trifork.stamdata.views.cpr.Person;
import com.trifork.stamdata.views.cpr.Statsborgerskab;
import com.trifork.stamdata.views.cpr.Udrejseoplysninger;
import com.trifork.stamdata.views.cpr.UmyndiggoerelseVaergeRelation;

public class PersonDaoTest {
	private static DatabaseHelper db;
	private Session session;
	private PersonDao dao;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		db = new DatabaseHelper("lookup", Person.class,
				Folkekirkeoplysninger.class, Statsborgerskab.class,
				Foedselsregistreringsoplysninger.class,
				Udrejseoplysninger.class, UmyndiggoerelseVaergeRelation.class,
				BarnRelation.class, MorOgFaroplysninger.class,
				ForaeldremyndighedsRelation.class, Beskyttelse.class);
		Session session = db.openSession();
		session.createQuery("delete from Person").executeUpdate();
		session.close();
	}

	@Before
	public void before() {
		session = db.openSession();
		session.getTransaction().begin();
		dao = new PersonDao(session);
	}
	
	@After
	public void after() {
		session.getTransaction().rollback();
		session.close();
	}
	
	@Test
	public void givesCurrentPersonDataWithContentsFromUnderlyingView() {
		session.save(person(at(2005, Calendar.JANUARY, 5), "1020304050"));
		
		CurrentPersonData person = dao.get("1020304050");
		assertEquals("1020304050", person.getCprNumber());
		assertEquals(at(2005, Calendar.JANUARY, 5), person.getValidFrom());
	}

	@Test
	public void getsFolkekirkeOplysninger() {
        session.save(person("1020304050"));
		session.save(folkekirkeoplysninger("M"));
		CurrentPersonData person = dao.get("1020304050");
		assertTrue(person.getMedlemAfFolkekirken());
	}

    @Test
	public void getsStatsborgerskab() {
        session.save(person("1020304050"));
		session.save(statsborgerskab("1234"));
		CurrentPersonData person = dao.get("1020304050");
		assertEquals("1234", person.getStatsborgerskab());
	}
	
	@Test
	public void getsFoedselsregistreringsoplysninger() {
        session.save(person("1020304050"));
		session.save(foedselsregistreringsoplysninger("1234", "foedselsTekst"));
		CurrentPersonData person = dao.get("1020304050");
		assertEquals("1234", person.getFoedselsregistreringsstedkode());
		assertEquals("foedselsTekst", person.getFoedselsregistreringstekst());
	}
	
	@Test
	public void getsUdrejseoplysninger() {
        session.save(person("1020304050"));
		session.save(udrejseoplysninger());
		CurrentPersonData person = dao.get("1020304050");
		assertEquals("1234", person.getUdrejseoplysninger().udrejseLandekode);
		assertEquals("line1", person.getUdrejseoplysninger().udlandsadresse1);
	}

	@Test
	public void getsNewestPersonRecordIfNoRecordIsInTheFuture() {
		session.save(person(at(2005, Calendar.JANUARY, 5), "1020304050"));
		session.save(person(at(2010, Calendar.FEBRUARY, 10), "1020304050"));
		
		CurrentPersonData person = dao.get("1020304050");
		assertEquals(at(2010, Calendar.FEBRUARY, 10), person.getValidFrom());
	}
	
	@Test
	public void getsCurrentPersonRecordIfRecordsInTheFuture() {
		session.save(person(at(2005, Calendar.JANUARY, 5), "1020304050"));
		session.save(person(at(2010, Calendar.FEBRUARY, 10), "1020304050"));
		session.save(person(at(2110, Calendar.FEBRUARY, 10), "1020304050"));
		
		CurrentPersonData person = dao.get("1020304050");
		assertEquals(at(2010, Calendar.FEBRUARY, 10), person.getValidFrom());
	}
	
	@Test
	public void getsCurrentUmyndiggoerelseVaergeRelation() {
		session.save(umyndiggoerelseVaergeRelation("1020304050", "5040302010", DateUtils.PAST.getTime(), at(2005, Calendar.JUNE, 15)));
        session.save(umyndiggoerelseVaergeRelation("1020304050", "6050403020", at(2005, Calendar.JUNE, 15), DateUtils.FUTURE.getTime()));

        session.save(person("1020304050"));
        CurrentPersonData person = dao.get("1020304050");
		assertEquals("6050403020", person.getVaerge().relationCpr);
	}
	
	@Test
	public void getsCurrentVaergemaal() {
        session.save(person("1020304050"));
		session.save(umyndiggoerelseVaergeRelation("7060504030", "1020304050", DateUtils.PAST.getTime(), at(2005, Calendar.JUNE, 15)));
		session.save(umyndiggoerelseVaergeRelation("9080706050", "1020304050", at(2005, Calendar.JUNE, 15), DateUtils.FUTURE.getTime()));
		session.save(umyndiggoerelseVaergeRelation("7060504030", "1020304050", at(2005, Calendar.JUNE, 15), DateUtils.FUTURE.getTime()));
		session.save(umyndiggoerelseVaergeRelation("8070605040", "1020304050", at(2100, Calendar.JUNE, 15), DateUtils.FUTURE.getTime()));
		
		CurrentPersonData person = dao.get("1020304050");
		assertEquals(2, person.getVaergemaal().size());
		Set<String> vaergemaalscpr = new TreeSet<String>();
		for (UmyndiggoerelseVaergeRelation relation : person.getVaergemaal()) {
			vaergemaalscpr.add(relation.getCpr());
		}
		Set<String> expectedVaergemaalscpr = new TreeSet<String>();
		expectedVaergemaalscpr.add("9080706050");
		expectedVaergemaalscpr.add("7060504030");
		assertEquals(expectedVaergemaalscpr, vaergemaalscpr);
	}
	
	@Test
	public void getsBoern() {
        String cpr = "7060504030";
        session.save(barnRelation(cpr, "1020304050"));
		session.save(barnRelation(cpr, "1020304051"));
		session.save(barnRelation(cpr, "1020304052"));
        session.save(person("1020304050"));

        session.save(person(cpr));
		CurrentPersonData person = dao.get(cpr);
		List<String> expected = Arrays.asList("1020304050", "1020304051", "1020304052");
		List<String> result = person.getBoernCpr();
		Collections.sort(result);
		assertEquals(expected, result);
	}
	
	@Test
	public void getsParents() {
        String cpr = "7060504030";
        session.save(morOgFaroplysninger(cpr, "1020304050", "M"));
		session.save(morOgFaroplysninger(cpr, "1020304051", "F"));
        session.save(person(cpr));
		CurrentPersonData person = dao.get(cpr);
		MorOgFaroplysninger morOplysninger = person.getMorOplysninger();
		assertNotNull(morOplysninger);
		assertEquals("1020304050", morOplysninger.foraeldercpr);
		MorOgFaroplysninger farOplysninger = person.getFarOplysninger();
		assertNotNull(farOplysninger);
		assertEquals("1020304051", farOplysninger.foraeldercpr);
	}
	
	@Test
	public void getsForaeldremyndighedsIndehavere() {
        String cpr = "12345678";
        session.save(foraeldremyndighedsRelation(cpr, "0003", null)); // mor
		session.save(foraeldremyndighedsRelation(cpr, "0005", "11111111")); // anden indehaver 1
        session.save(person(cpr));
		CurrentPersonData person = dao.get(cpr);
		assertEquals(2, person.getForaeldreMyndighedsIndehavere().size());
	}
	
	@Test
	public void getsForaeldremyndighedsBoern() {
		session.save(foraeldremyndighedsRelation("12345678", "0003", null)); // cpr for foraelder er null hvis foraelder er registreret i CPR.
        String cpr = "11111111";
        session.save(foraeldremyndighedsRelation("12345679", "0005", cpr));
		session.save(barnRelation(cpr, "12345678"));
        session.save(person(cpr));
		CurrentPersonData person = dao.get(cpr);
		assertEquals(2, person.getForaeldreMyndighedBoern().size());
	}
	
	@Test
	public void getsBeskyttelser() {
        String cpr = "1234567890";
        session.save(beskyttelse(cpr, "0001"));
		session.save(beskyttelse(cpr, "0002"));
		session.save(beskyttelse(cpr, "0003"));
        session.save(person(cpr));
		CurrentPersonData person = dao.get(cpr);
		assertEquals(3, person.getBeskyttelser().size());
	}

	private Beskyttelse beskyttelse(String cpr, String beskyttelsestype) {
		Beskyttelse beskyttelse = new Beskyttelse();
		beskyttelse.id = cpr + "-" + beskyttelsestype;
		beskyttelse.beskyttelsestype = beskyttelsestype;
		beskyttelse.setCpr(cpr);
		beskyttelse.setValidFrom(at(2005, Calendar.JUNE, 15));
		beskyttelse.setCreatedBy("AHJ");
		beskyttelse.setModifiedBy("AHJ");
		beskyttelse.setCreatedDate(new Date());
		beskyttelse.setModifiedDate(new Date());
		return beskyttelse;
	}
	
	private ForaeldremyndighedsRelation foraeldremyndighedsRelation(String cpr, String kode, String indehaverCpr) {
		ForaeldremyndighedsRelation foraeldremyndighedsRelation = new ForaeldremyndighedsRelation();
		foraeldremyndighedsRelation.id = cpr + "-" + kode;
		foraeldremyndighedsRelation.setCpr(cpr);
		foraeldremyndighedsRelation.typeKode = kode;
		foraeldremyndighedsRelation.typeTekst = kode;
		foraeldremyndighedsRelation.relationCpr = indehaverCpr;
		foraeldremyndighedsRelation.setValidFrom(at(2005, Calendar.JUNE, 15));
		foraeldremyndighedsRelation.setCreatedBy("AHJ");
		foraeldremyndighedsRelation.setModifiedBy("AHJ");
		foraeldremyndighedsRelation.setCreatedDate(new Date());
		foraeldremyndighedsRelation.setModifiedDate(new Date());
		return foraeldremyndighedsRelation;
	}
	
	private BarnRelation barnRelation(String cpr, String barnCpr) {
		BarnRelation barnRelation = new BarnRelation();
		barnRelation.id = cpr + "-" + barnCpr;
		barnRelation.setCpr(cpr);
		barnRelation.barnCPR = barnCpr;
		barnRelation.setValidFrom(at(2005, Calendar.JUNE, 15));
		barnRelation.setCreatedBy("AHJ");
		barnRelation.setModifiedBy("AHJ");
		barnRelation.setCreatedDate(new Date());
		barnRelation.setModifiedDate(new Date());
		return barnRelation;
	}
	
	private MorOgFaroplysninger morOgFaroplysninger(String cpr, String foraelderCpr, String foraelderkode) {
		MorOgFaroplysninger morOgFarOplysninger = new MorOgFaroplysninger();
		morOgFarOplysninger.setCpr(cpr);
		morOgFarOplysninger.foraeldercpr = foraelderCpr;
		morOgFarOplysninger.id = cpr + "-"  + foraelderkode;
		morOgFarOplysninger.foraelderkode = foraelderkode;
		morOgFarOplysninger.setValidFrom(at(2005, Calendar.JUNE, 15));
		morOgFarOplysninger.setCreatedBy("AHJ");
		morOgFarOplysninger.setModifiedBy("AHJ");
		morOgFarOplysninger.setCreatedDate(new Date());
		morOgFarOplysninger.setModifiedDate(new Date());
		return morOgFarOplysninger;
		
	}

	@Test
	public void pastAndFutureNotModified() {
		Folkekirkeoplysninger fo = folkekirkeoplysninger("M");
		fo.setValidFrom(DateUtils.PAST.getTime());
		fo.setValidTo(DateUtils.FUTURE.getTime());
		session.save(fo);
		session.flush();
		session.clear();
		Folkekirkeoplysninger retrieved = (Folkekirkeoplysninger) session.get(Folkekirkeoplysninger.class, fo.getRecordID());
		assertNull(retrieved.getValidFrom());
		
		assertNull(retrieved.getValidTo());
	}
	
	private UmyndiggoerelseVaergeRelation umyndiggoerelseVaergeRelation(String cpr, String vaergeCpr, Date validFrom, Date validTo) {
		UmyndiggoerelseVaergeRelation result = new UmyndiggoerelseVaergeRelation();
		result.typeKode = "001";
		result.typeTekst = "Hello world";
		result.id = cpr + "-" + result.typeKode;
		result.setCpr(cpr);
		result.relationCpr = vaergeCpr;
		result.setValidFrom(validFrom);
		result.setValidTo(validTo);
		result.setCreatedBy("AHJ");
		result.setModifiedBy("AHJ");
		result.setCreatedDate(new Date());
		result.setModifiedDate(new Date());
		return result;
	}

    private Person person (String cpr) {
        return person(at(2000,1,1), cpr);
    }

    private Person person(Date validFrom, String cpr) {
		Person result = new Person();
		result.setCpr(cpr);
		result.koen = "M";
		result.foedselsdato = at(1975, Calendar.MAY, 12);
		result.modifiedBy = "AHJ";
		result.createdBy = "AHJ";
		result.setValidFrom(validFrom);
		result.setModifiedDate(validFrom);
		result.setCreatedDate(at(2005, Calendar.JANUARY, 5));
		return result;
	}
	
	private Statsborgerskab statsborgerskab(String landekode) {
		Statsborgerskab result = new Statsborgerskab();
		result.setCpr("1020304050");
		result.landekode = landekode;
		result.modifiedBy = "AHJ";
		result.createdBy = "AHJ";
		result.setValidFrom(at(2005, Calendar.JANUARY, 5));
		result.setModifiedDate(result.getValidFrom());
		result.setCreatedDate(result.getValidFrom());
		result.statsborgerskabstartdatoUsikkerhedsmarkering = "";
		return result;
	}
	
	private Folkekirkeoplysninger folkekirkeoplysninger(String kode) {
		Folkekirkeoplysninger result = new Folkekirkeoplysninger();
		result.setCpr( "1020304050");
		result.forholdsKode = kode;
		result.modifiedBy = "AHJ";
		result.createdBy = "AHJ";
		result.setValidFrom(at(2005, Calendar.JANUARY, 5));
		result.setModifiedDate(result.getValidFrom());
		result.setCreatedDate(at(2005, Calendar.JANUARY, 5));
		return result;
	}
	
	private Foedselsregistreringsoplysninger foedselsregistreringsoplysninger(String kode, String tekst) {
		Foedselsregistreringsoplysninger result = new Foedselsregistreringsoplysninger();
		result.setCpr("1020304050");
		result.foedselsregistreringsstedkode = kode;
		result.foedselsregistreringstekst = tekst;
		result.modifiedBy = "AHJ";
		result.createdBy = "AHJ";
		result.setValidFrom(at(2005, Calendar.JANUARY, 5));
		result.setModifiedDate(result.getValidFrom());
		result.setCreatedDate(at(2005, Calendar.JANUARY, 5));
		return result;
	}
	
	private Udrejseoplysninger udrejseoplysninger() {
		Udrejseoplysninger result = new Udrejseoplysninger();
		result.setCpr("1020304050");
		result.udrejseLandekode = "1234";
		result.udrejsedatoUsikkerhedsmarkering = "";
		result.udrejsedato = new Date();
		result.udlandsadresse1 = "line1";
		result.udlandsadresse2 = "line2";
		result.udlandsadresse3 = "line3";
		result.udlandsadresse4 = "line4";
		result.udlandsadresse5 = "line5";
		result.modifiedBy = "AHJ";
		result.createdBy = "AHJ";
		result.setValidFrom(at(2005, Calendar.JANUARY, 5));
		result.setModifiedDate(result.getValidFrom());
		result.setCreatedDate(result.getValidFrom());
		return result;
	}

	private Date at(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		return calendar.getTime();
	}
}
