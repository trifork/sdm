package com.trifork.sdm.importer.importers.yderregister;


import java.io.IOException;
import java.text.SimpleDateFormat;

import org.junit.Test;


public class YderregisterParserTest
{

	private static final String PATH = "testdata/yderregister/";

	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");


	@Test
	public void testParse() throws Exception
	{
		/*
		YderregisterDatasets yderReg = new YderregisterParser().parseYderregister(Arrays.asList(TestHelper.getFile(PATH + "initial/").listFiles()));

		assertEquals(12, yderReg.getYderregisterDS().getEntities().size());
		Yderregister y = yderReg.getYderregisterDS().getRecordById("4219");
		assertNotNull(y);
		assertEquals("Yders Navn", "Jørgen Vagn Nielsen", y.getNavn());
		assertEquals("Yders Vejnavn", "Store Kongensgade 96,4.", y.getVejnavn());
		assertEquals("Yders Telefon", "33112299", y.getTelefon());
		assertEquals("Yders Postnummer", "1264", y.getPostnummer());
		assertEquals("Yders Bynavn", "København K", y.getBynavn());
		assertEquals("Yders Amtsnummer", 84, y.getAmtNummer());
		assertEquals("Yders Email", "klinik@33112299.dk", y.getEmail());
		assertEquals("Yders www", "www.plib.dk", y.getWww());
		assertEquals("Yders startdato", "1978-07-01", df.format(y.getValidFrom().getTime()));
		assertEquals("Yders slutdato", "2999-12-31", df.format(y.getValidTo().getTime()));

		assertEquals(21, yderReg.getYderregisterPersonDS().getEntities().size());
		YderregisterPerson yp = yderReg.getYderregisterPersonDS().getRecordById("15458-1234567893");
		assertEquals("Personens Ydernummer", "15458", yp.getNummer());
		assertEquals("Personens CPR", "1234567893", yp.getCpr());
		assertEquals("Personens rollekode", new Long("15"), yp.getPersonrolleKode());
		assertEquals("Personens rolletekst", "Praksisreservelæge", yp.getPersonrolleTxt());
		assertEquals("Personens startdato", "2010-04-01", df.format(yp.getValidFrom().getTime()));
		assertEquals("Personens slutdato", "2010-09-30", df.format(yp.getValidTo().getTime()));

		assertNotNull(yp);
	*/
	}


	@Test
	public void testInvalid() throws IOException
	{
		/*
		try
		{

			YderregisterDatasets yderReg = new YderregisterParser().parseYderregister(Arrays.asList(TestHelper.getFile(PATH + "invalid/").listFiles()));

			yderReg.getYderregisterDS();

			fail();
		}
		catch (Exception e)
		{
		}
		*/
	}

}
