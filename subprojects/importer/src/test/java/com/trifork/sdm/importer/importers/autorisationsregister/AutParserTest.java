package com.trifork.sdm.importer.importers.autorisationsregister;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.BeforeClass;
import org.junit.Test;


public class AutParserTest
{
	private static File valid;
	private static File invalid;


	@BeforeClass
	public static void setup() throws URISyntaxException
	{
		/*
		valid = TestHelper.getFile("testdata/aut/valid/20090915AutDK.csv");
		invalid = TestHelper.getFile("testdata/aut/invalid/20090915AutDK.csv");
		*/
	}


	@Test
	public void testParse() throws IOException
	{
		/*
		AutorisationsregisterParser parser = new AutorisationsregisterParser();
		Autorisationsregisterudtraek auts = parser.parse(valid, new Date());

		assertEquals(4, auts.getEntities().size());
		Autorisation a = auts.getRecordById("0013H");

		assertNotNull(a);
		assertEquals("0101280063", a.getCpr());
		assertEquals("Tage Søgaard", a.getFornavn());
		*/
	}


	@Test
	public void testInvalid() throws IOException
	{
		/*
		try
		{
			AutorisationsregisterParser parser = new AutorisationsregisterParser();
			Autorisationsregisterudtraek auts = parser.parse(invalid, new Date());
			auts.getEntities();
			fail();
		}
		catch (Exception e)
		{
		}
		*/
	}

}
