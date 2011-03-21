package dk.trifork.sdm.importer.yderregister;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import dk.trifork.sdm.importer.exceptions.FileParseException;
import dk.trifork.sdm.importer.yderregister.model.Yderregister;
import dk.trifork.sdm.importer.yderregister.model.YderregisterDatasets;
import dk.trifork.sdm.importer.yderregister.model.YderregisterPerson;


public class YderregisterParserTest {

	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	@Test
	public void testParse() throws Exception {

		File dir = FileUtils.toFile(getClass().getClassLoader().getResource("data/yderregister/initial/"));

		YderregisterDatasets yderReg = new YderregisterParser().parseYderregister(Arrays.asList(dir.listFiles()));

		assertEquals(12, yderReg.getYderregisterDS().getEntities().size());
		Yderregister y = yderReg.getYderregisterDS().getEntityById("4219");
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
		YderregisterPerson yp = yderReg.getYderregisterPersonDS().getEntityById("15458-1234567893");
		assertEquals("Personens Ydernummer", "15458", yp.getNummer());
		assertEquals("Personens CPR", "1234567893", yp.getCpr());
		assertEquals("Personens rollekode", new Long("15"), yp.getPersonrolleKode());
		assertEquals("Personens rolletekst", "Praksisreservelæge", yp.getPersonrolleTxt());
		assertEquals("Personens startdato", "2010-04-01", df.format(yp.getValidFrom().getTime()));
		assertEquals("Personens slutdato", "2010-09-30", df.format(yp.getValidTo().getTime()));

		assertNotNull(yp);
	}

	@Test(expected = FileParseException.class)
	public void testInvalid() throws IOException, FileParseException {

		File dir = FileUtils.toFile(getClass().getClassLoader().getResource("data/yderregister/invalid/"));
		new YderregisterParser().parseYderregister(Arrays.asList(dir.listFiles()));
	}
}
