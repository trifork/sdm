package dk.trifork.sdm.importer.sks;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;


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
