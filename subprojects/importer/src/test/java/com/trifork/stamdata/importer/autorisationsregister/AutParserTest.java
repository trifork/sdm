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

package com.trifork.stamdata.importer.autorisationsregister;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.importer.autorisationsregister.AutorisationsregisterParser;
import com.trifork.stamdata.importer.autorisationsregister.model.Autorisation;
import com.trifork.stamdata.importer.autorisationsregister.model.Autorisationsregisterudtraek;



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
