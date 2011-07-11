// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
//
// Contributor(s): Contributors are attributed in the source code
// where applicable.
//
// The Original Code is "Stamdata".
//
// The Initial Developer of the Original Code is Trifork Public A/S.
//
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
//
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.importer.jobs.yderregister;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.text.SimpleDateFormat;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.trifork.stamdata.importer.jobs.yderregister.model.Yderregister;
import com.trifork.stamdata.importer.jobs.yderregister.model.YderregisterDatasets;
import com.trifork.stamdata.importer.jobs.yderregister.model.YderregisterPerson;


public class YderregisterParserTest
{
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	@Test
	public void testParse() throws Exception
	{
		File dir = FileUtils.toFile(getClass().getClassLoader().getResource("data/yderregister/initial/"));

		YderregisterDatasets yderReg = new YderregisterParser().parseYderregister(dir.listFiles());

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

	@Test(expected = Exception.class)
	public void testInvalid() throws Exception
	{
		File dir = FileUtils.toFile(getClass().getClassLoader().getResource("data/yderregister/invalid/"));
		new YderregisterParser().parseYderregister(dir.listFiles());
	}
}
