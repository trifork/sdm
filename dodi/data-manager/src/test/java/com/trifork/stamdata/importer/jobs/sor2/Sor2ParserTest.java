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


package com.trifork.stamdata.importer.jobs.sor2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import javax.inject.Provider;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.trifork.stamdata.importer.FileParserIntegrationTest;
import com.trifork.stamdata.importer.jobs.sor.sor2.SORFullEventHandler;
import com.trifork.stamdata.importer.jobs.sor.sor2.SORXmlParser;
import com.trifork.stamdata.importer.jobs.yderregister.YderregisterSaxEventHandler;
import com.trifork.stamdata.persistence.RecordPersister;
import com.trifork.stamdata.specs.SorFullRecordSpecs;

public class Sor2ParserTest extends FileParserIntegrationTest
{

	public Sor2ParserTest()
	{
		super("sor");
    }

	@Test
	public void testParser1() throws Exception
	{
        File fileSet1 = getDirectory("data/sor");

        placeInInbox(fileSet1, true);
         
        assertThat(isLocked(), is(false));

//        assertRecordCount("Yderregister", 58);
//        assertRecordCount("YderregisterPerson", 54);
		/*long timeStart = System.currentTimeMillis();
		parser.process(inbox1, persister);
		
		long timeEnd = System.currentTimeMillis();
		System.out.println("full1 file took: " + (timeEnd-timeStart) + " ms");
		
		timeStart = System.currentTimeMillis();
		parser.process(inbox2, persister);
		timeEnd = System.currentTimeMillis();
		System.out.println("full2 file took: " + (timeEnd-timeStart) + " ms");*/
	}

}
