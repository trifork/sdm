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

package com.trifork.stamdata.importer.jobs.sor;

import com.trifork.stamdata.importer.jobs.sor.model.*;
import com.trifork.stamdata.importer.jobs.sor.xmlmodel.SpecialityMapper;
import com.trifork.stamdata.importer.jobs.sor.xmlmodel.UnitTypeMapper;
import com.trifork.stamdata.importer.util.DateUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

import static org.junit.Assert.*;


public class SorParserIntegrationTest
{
	public static File fullSor;
	public static File fullSor2;

	public File getFile(String path)
	{
		return FileUtils.toFile(getClass().getClassLoader().getResource(path));
	}

	@Before
	public void setUp()
	{
		fullSor = getFile("data/sor/SOR_FULL.xml");
		fullSor2 = getFile("data/sor/SOR_FULL2.xml");
	}

	@Test
	public void testFullTest() throws Exception
	{
		SORDataSets dataSets = SORImporter.parse(fullSor);

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
    }

    @Test
    public void TestFull2Tests() throws Exception
    {
		SORDataSets dataSets = SORImporter.parse(fullSor2);

        Collection<Praksis> praksis = dataSets.getPraksisDS().getEntities();
        Collection<Yder> yder = dataSets.getYderDS().getEntities();
        Collection<Sygehus> sygehus = dataSets.getSygehusDS().getEntities();
        Collection<SygehusAfdeling> sygehusAfdeling = dataSets.getSygehusAfdelingDS().getEntities();
        Collection<Apotek> apotek = dataSets.getApotekDS().getEntities();

		assertEquals(3159, praksis.size());
		assertEquals(5456, yder.size());
		assertEquals(475, sygehus.size());
		assertEquals(2922, sygehusAfdeling.size());
		assertEquals(329, apotek.size());
	}
}
