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

package com.trifork.stamdata.importer.sks;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;

import com.trifork.stamdata.importer.sks.SksImporter;


public class SksImporterTest {

	public static File SHAKCompleate = new File("./test/data/sks/SHAKCOMPLETE.TXT");
	public static File wrong = new File("./test/data/sks/SHAKCOMPLETE.XML");

	@Test
	public void testAreRequiredInputFilesPresent() {

		SksImporter importer = new SksImporter();
		ArrayList<File> files = new ArrayList<File>();

		assertFalse(importer.checkRequiredFiles(files));

		files.add(SHAKCompleate);

		assertTrue(importer.checkRequiredFiles(files));

		files.remove(SHAKCompleate);
		files.add(wrong);

		assertFalse(importer.checkRequiredFiles(files));
	}
}
