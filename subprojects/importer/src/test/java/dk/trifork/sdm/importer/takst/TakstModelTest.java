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

package dk.trifork.sdm.importer.takst;

import dk.trifork.sdm.importer.takst.model.ATCKoderOgTekst;
import dk.trifork.sdm.importer.takst.model.Takst;
import dk.trifork.sdm.importer.takst.model.TakstDataset;
import dk.trifork.sdm.util.DateUtils;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;


public class TakstModelTest extends TestCase {

	@Test
	public void testManyToMany() throws Exception {

		Calendar from = DateUtils.toCalendar(2000, 1, 1);
		Calendar to = DateUtils.toCalendar(2000, 15, 1);
		Takst takst = new Takst(from, to);
		TakstDataset<ATCKoderOgTekst> atckoder = new TakstDataset<ATCKoderOgTekst>(takst, new ArrayList<ATCKoderOgTekst>(), ATCKoderOgTekst.class);
		takst.addDataset(atckoder);
	}
}
