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
