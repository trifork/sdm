package dk.trifork.sdm.importer.autorisationsregister;

import dk.trifork.sdm.dao.AuditingPersister;
import dk.trifork.sdm.model.CompleteDataset;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class AutImporterTest {

	public File valid;
	private AutImporter importer;

	@Before
	public void Setup() {

		valid = FileUtils.toFile(getClass().getClassLoader().getResource("data/aut/valid/20090915AutDK.csv"));
		importer = new AutImporter();
	}

	@Test
	public void testAreRequiredInputFilesPresent() throws IOException {

		List<File> files = new ArrayList<File>();

		// empty set

		assertFalse(importer.checkRequiredFiles(files));
		files.add(new File("blabla.nowayamigo"));

		// wrong file name and empty file

		assertFalse(importer.checkRequiredFiles(files));
		files.add(valid);

		// one bad and one good file.

		assertFalse(importer.checkRequiredFiles(files));
		files = new ArrayList<File>();
		files.add(valid);

		// one good file

		assertTrue(importer.checkRequiredFiles(files));
		files.add(valid);

		// two good files

		assertTrue(importer.checkRequiredFiles(files));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testImport() throws Exception {

		List<File> files = new ArrayList<File>();
		files.add(valid);
		AuditingPersister daoMock = mock(AuditingPersister.class);
		importer.doImport(files, daoMock);
		verify(daoMock).persistCompleteDataset(any(CompleteDataset.class));
	}

	@Test
	public void testGetDateFromFileName() {

		AutImporter importer = new AutImporter();
		Date date = importer.getDateFromInputFileName("19761110sgfdgfg").getTime();
		assertEquals("19761110", new SimpleDateFormat("yyyyMMdd").format(date));
	}
}
