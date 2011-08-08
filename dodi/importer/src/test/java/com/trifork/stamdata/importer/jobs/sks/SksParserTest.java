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

package com.trifork.stamdata.importer.jobs.sks;


import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.importer.jobs.sks.SksParser;
import com.trifork.stamdata.importer.persistence.Dataset;
import com.trifork.stamdata.importer.util.DateUtils;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class SksParserTest {

	public File SHAKCompleate;

	@Before
	public void setUp() {

		SHAKCompleate = FileUtils.toFile(getClass().getClassLoader().getResource("data/sks/SHAKCOMPLETE.TXT"));
	}

	@Test
	public void testParseSHAKCompleate() throws Throwable {

		Dataset<Organisation> org = SksParser.parseOrganisationer(SHAKCompleate);
		assertEquals(9717, org.getEntities().size());
		List<Organisation> afd600714X = org.getEntitiesById("600714X");
		assertEquals(2, afd600714X.size()); // two versions of this id exist
		assertEquals(DateUtils.toDate(2008, 10, 1), afd600714X.get(0).getValidFrom());
		assertEquals(DateUtils.toDate(2008, 11, 30), afd600714X.get(0).getValidTo());
		assertEquals("Ortopædkirurgisk skadeklinik, Middelfart", afd600714X.get(0).getNavn());

		assertEquals(DateUtils.toDate(2008, 12, 1), afd600714X.get(1).getValidFrom());
		assertEquals(DateUtils.toDate(2500, 1, 1), afd600714X.get(1).getValidTo());
		assertEquals("Skadeklinik, Middelfart", afd600714X.get(1).getNavn());

		List<Organisation> sgh4212 = org.getEntitiesById("4212");
		assertEquals(2, sgh4212.size()); // two versions of this id exist
		assertEquals(DateUtils.toDate(1999, 01, 1), sgh4212.get(0).getValidFrom());
		assertEquals(DateUtils.toDate(2008, 11, 30), sgh4212.get(0).getValidTo());
		assertEquals("Sygehus Fyn", sgh4212.get(0).getNavn());

		assertEquals(DateUtils.toDate(2008, 12, 1), sgh4212.get(1).getValidFrom());
		assertEquals(DateUtils.toDate(2500, 1, 1), sgh4212.get(1).getValidTo());
		assertEquals("OUH Svendborg Sygehus", sgh4212.get(1).getNavn());
	}

}
