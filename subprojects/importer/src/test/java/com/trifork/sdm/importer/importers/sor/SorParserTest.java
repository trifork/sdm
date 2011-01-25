package com.trifork.sdm.importer.importers.sor;


import java.io.File;

import org.junit.Test;

import com.trifork.sdm.importer.TestHelper;


public class SorParserTest
{

	public static File onePraksis = TestHelper.getFile("testdata/sor/ONE_PRAKSIS.xml");
	public static File oneSygehus = TestHelper.getFile("testdata/sor/ONE_SYGEHUS.xml");
	public static File oneApotek = TestHelper.getFile("testdata/sor/ONE_APOTEK.xml");
	public static File fullSor = TestHelper.getFile("testdata/sor/SOR_FULL.xml");
	public static File fullSor2 = TestHelper.getFile("testdata/sor/SOR_FULL2.xml");


	@Test
	public void testSinglePraksis() throws Exception
	{
		/*
		SORDataSets dataSets = SORParser.parse(onePraksis);

		Collection<Praksis> praksis = dataSets.getPraksisDS().getEntities();
		Collection<Yder> yder = dataSets.getYderDS().getEntities();

		assertEquals(1, praksis.size());
		assertEquals(1, yder.size());

		Praksis p = praksis.iterator().next();

		assertEquals("Michael Filtenborg & co", p.getNavn());
		assertEquals(new Long(8331000016009L), p.getSorNummer());
		assertEquals(new Long(1084L), p.getRegionCode());
		assertEquals(SOREventHandler.toDate("1999-03-25"), p.getValidFrom());
		assertEquals(DateUtils.FOREVER, p.getValidTo());
		assertEquals(new Long(5790000141227L), p.getEanLokationsnummer());

		Yder y = yder.iterator().next();

		assertEquals("19062", y.getNummer());
		assertEquals(new Long(8341000016002L), y.getSorNummer());
		assertEquals(new Long(8331000016009L), y.getPraksisSorNummer());

		assertEquals("Michael Filtenborg", y.getNavn());
		assertEquals("Vesterbrogade 21", y.getVejnavn());
		assertEquals("3250", y.getPostnummer());
		assertEquals("Gilleleje", y.getBynavn());
		assertEquals("48300204", y.getTelefon());
		assertEquals(new Long(5790000141227L), y.getEanLokationsnummer());

		assertEquals(new Long(408443003L), y.getHovedSpecialeKode());
		assertEquals(SpecialityMapper.kodeToString(408443003L), y.getHovedSpecialeTekst());

		assertEquals(SOREventHandler.toDate("1999-03-25"), y.getValidFrom());
		assertEquals(DateUtils.FOREVER, y.getValidTo());
		*/
	}


	@Test
	public void testSingleSygehus() throws Exception
	{
		/*
		SORDataSets dataSets = SORParser.parse(oneSygehus);

		Collection<Sygehus> sygehus = dataSets.getSygehusDS().getEntities();
		Collection<SygehusAfdeling> afdeling = dataSets.getSygehusAfdelingDS().getEntities();

		assertEquals(1, sygehus.size());

		Sygehus s = sygehus.iterator().next();

		assertNull(s.getEanLokationsnummer());
		assertEquals("2529", s.getNummer());
		assertEquals(new Long(347811000016004L), s.getSorNummer());

		assertEquals("Roskilde Øjenklinik", s.getNavn());
		assertEquals("Hestetorvet 8", s.getVejnavn());
		assertEquals("4000", s.getPostnummer());
		assertEquals("Roskilde", s.getBynavn());
		assertEquals("46361266", s.getTelefon());
		assertEquals("www.roskildeojenklinik.dk", s.getWww());
		assertEquals("J.Thulesen@dadlnet.dk", s.getEmail());

		assertEquals(SOREventHandler.toDate("2009-10-08"), s.getValidFrom());
		assertEquals(FOREVER, s.getValidTo());

		assertEquals(2, afdeling.size());

		SygehusAfdeling sa = dataSets.getSygehusAfdelingDS().getRecordById(347821000016008L);
		assertNotNull(sa);
		assertNull(sa.getEanLokationsnummer());
		assertEquals("252901", sa.getNummer());
		assertEquals(new Long(347821000016008L), sa.getSorNummer());
		assertEquals(new Long(347811000016004L), sa.getSygehusSorNummer());
		assertNull(sa.getOverAfdelingSorNummer());
		assertEquals(new Long(347811000016004L), sa.getUnderlagtSygehusSorNummer());

		assertEquals("Roskilde Øjenklinik, afdeling", sa.getNavn());
		assertEquals("Hestetorvet 8", sa.getVejnavn());
		assertEquals("4000", sa.getPostnummer());
		assertEquals("Roskilde", sa.getBynavn());
		assertEquals("46361266", sa.getTelefon());
		assertEquals("www.roskildeojenklinik.dk", sa.getWww());
		assertEquals("J.Thulesen@dadlnet.dk", sa.getEmail());

		assertEquals(new Long(550811000005108L), sa.getAfdelingTypeKode());
		assertEquals(UnitTypeMapper.kodeToString(new Long(550811000005108L)), sa.getAfdelingTypeTekst());
		assertEquals(SOREventHandler.toDate("2009-10-07"), sa.getValidFrom());
		assertEquals(FOREVER, sa.getValidTo());

		sa = dataSets.getSygehusAfdelingDS().getRecordById(347831000016005L);
		assertNotNull(sa);
		assertNull(sa.getEanLokationsnummer());
		assertEquals("2529010", sa.getNummer());
		assertEquals(new Long(347831000016005L), sa.getSorNummer());
		assertNull(sa.getSygehusSorNummer());
		assertEquals(new Long(347821000016008L), sa.getOverAfdelingSorNummer());
		assertEquals(new Long(347811000016004L), sa.getUnderlagtSygehusSorNummer());

		assertEquals("Roskilde Øjenklinik, beh. afsnit", sa.getNavn());
		assertEquals("Hestetorvet 8", sa.getVejnavn());
		assertEquals("4000", sa.getPostnummer());
		assertEquals("Roskilde", sa.getBynavn());
		assertEquals("46361266", sa.getTelefon());
		assertEquals("www.roskildeojenklinik.dk", sa.getWww());
		assertEquals("J.Thulesen@dadlnet.dk", sa.getEmail());

		assertEquals(new Long(550851000005109L), sa.getAfdelingTypeKode());
		assertEquals(UnitTypeMapper.kodeToString(new Long(550851000005109L)), sa.getAfdelingTypeTekst());
		assertEquals(new Long(394594003L), sa.getHovedSpecialeKode());
		assertEquals(SpecialityMapper.kodeToString(394594003L), sa.getHovedSpecialeTekst());

		assertEquals(SOREventHandler.toDate("2009-10-08"), sa.getValidFrom());
		assertEquals(FOREVER, sa.getValidTo());
		*/
	}


	@Test
	public void testSingleApotek() throws Exception
	{
		/*
		SORDataSets dataSets = SORParser.parse(oneApotek);

		Collection<Apotek> apotek = dataSets.getApotekDS().getEntities();

		assertEquals(1, apotek.size());

		Apotek a = apotek.iterator().next();
		assertEquals(new Long(5790000173624L), a.getEanLokationsnummer());
		assertEquals(new Long(362L), a.getApotekNummer());
		assertEquals(new Long(1L), a.getFilialNummer());

		assertEquals("Værløse Apotek", a.getNavn());
		assertEquals("Bymidten 13", a.getVejnavn());
		assertEquals("3500", a.getPostnummer());
		assertEquals("Værløse", a.getBynavn());
		assertEquals("42482209", a.getTelefon());
		assertNull(a.getWww());
		assertNull(a.getEmail());

		assertEquals(SOREventHandler.toDate("1995-02-20"), a.getValidFrom());
		assertEquals(FOREVER, a.getValidTo());
		*/
	}


	@Test
	public void testFullTest() throws Exception
	{
		/*
		SORDataSets dataSets = SORParser.parse(fullSor);

		Collection<Praksis> praksis = dataSets.getPraksisDS().getEntities();
		Collection<Yder> yder = dataSets.getYderDS().getEntities();
		Collection<Sygehus> sygehus = dataSets.getSygehusDS().getEntities();
		Collection<SygehusAfdeling> sygehusAfdeling = dataSets.getSygehusAfdelingDS().getEntities();
		Collection<Apotek> apotek = dataSets.getApotekDS().getEntities();

		assertEquals(3148, praksis.size());
		assertEquals(5434, yder.size());
		assertEquals(469, sygehus.size());
		assertEquals(2890, sygehusAfdeling.size());
		assertEquals(328, apotek.size());

		dataSets = SORParser.parse(fullSor2);

		praksis = dataSets.getPraksisDS().getEntities();
		yder = dataSets.getYderDS().getEntities();
		sygehus = dataSets.getSygehusDS().getEntities();
		sygehusAfdeling = dataSets.getSygehusAfdelingDS().getEntities();
		apotek = dataSets.getApotekDS().getEntities();

		assertEquals(3159, praksis.size());
		assertEquals(5456, yder.size());
		assertEquals(475, sygehus.size());
		assertEquals(2922, sygehusAfdeling.size());
		assertEquals(329, apotek.size());
		*/
	}
}
