package dk.trifork.sdm.importer.autorisationsregister;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import dk.trifork.sdm.importer.autorisationsregister.model.Autorisation;
import dk.trifork.sdm.importer.autorisationsregister.model.Autorisationsregisterudtraek;


public class AutParserTest {

	public static File valid;
	public static File invalid;

	@Before
	public void setUp() {

		valid = FileUtils.toFile(getClass().getClassLoader().getResource("data/aut/valid/20090915AutDK.csv"));
		invalid = FileUtils.toFile(getClass().getClassLoader().getResource("data/aut/invalid/20090915AutDK.csv"));
	}

	@Test
	public void testParse() throws IOException {

		Autorisationsregisterudtraek auts = AutorisationsregisterParser.parse(valid, Calendar.getInstance());
		assertEquals(4, auts.getEntities().size());
		Autorisation a = auts.getEntityById("0013H");
		assertNotNull(a);
		assertEquals("0101280063", a.getCpr());
		assertEquals("Tage Søgaard", a.getFornavn());
	}

	@Test(expected = Exception.class)
	public void testInvalid() throws IOException {

		AutorisationsregisterParser.parse(invalid, Calendar.getInstance());
	}
}
