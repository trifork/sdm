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


package com.trifork.stamdata.importer.jobs.yderregister;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import com.trifork.stamdata.importer.jobs.yderregister.model.Yderregister;
import com.trifork.stamdata.importer.jobs.yderregister.model.YderregisterDatasets;
import com.trifork.stamdata.importer.jobs.yderregister.model.YderregisterPerson;
import com.trifork.stamdata.importer.util.DateUtils;


public class YderregisterParserTest
{
	@Test
	public void canImportACorrectFileSet() throws Exception
	{
		File dir = FileUtils.toFile(getClass().getClassLoader().getResource("data/yderregister/initial/"));

		YderregisterDatasets yderReg = new YderregisterParser().parseYderregister(dir.listFiles());

		// Entries

		assertEquals(12, yderReg.getYderregisterDS().getEntities().size());
        Yderregister praksis = yderReg.getYderregisterDS().getEntityById("4219");
		
		assertThat(praksis.getNavn(), is("Jørgen Vagn Nielsen"));
		assertThat(praksis.getVejnavn(), is("Store Kongensgade 96,4."));
		assertThat(praksis.getTelefon(), is("33112299"));
		assertThat(praksis.getPostnummer(), is("1264"));
		assertThat(praksis.getBynavn(), is("København K"));
		assertThat(praksis.getAmtNummer(), is(84));
		assertThat(praksis.getEmail(), is("klinik@33112299.dk"));
		assertThat(praksis.getWww(), is("www.plib.dk"));
		assertThat(praksis.getValidFrom(), is(date("1978-07-01")));
		assertThat(praksis.getValidTo(), is(DateUtils.THE_END_OF_TIME));
		
		// Persons
		
		assertThat(yderReg.getYderregisterPersonDS().getEntities().size(), is(21));
		YderregisterPerson person = yderReg.getYderregisterPersonDS().getEntityById("15458-1234567893");
		
		assertThat(person.getNummer(), is("15458"));
		assertThat(person.getCpr(), is("1234567893"));
		assertThat(person.getPersonrolleKode(), is("1A"));
		assertThat(person.getPersonrolleTxt(), is("Praksisreservelæge"));
		assertThat(person.getValidFrom(), is(date("2010-04-01")));
		assertThat(person.getValidTo(), is(date("2010-09-30")));
	}

	@Test(expected = Exception.class)
	public void shouldFailIfPresentedWithAnInvalidFileSet() throws Exception
	{
		File dir = FileUtils.toFile(getClass().getClassLoader().getResource("data/yderregister/invalid/"));
		new YderregisterParser().parseYderregister(dir.listFiles());
	}
	
	private Date date(String dateString)
	{
	    return ISODateTimeFormat.date().parseDateTime(dateString).toDate();
	}
}
