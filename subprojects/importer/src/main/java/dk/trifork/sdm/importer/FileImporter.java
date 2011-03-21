package dk.trifork.sdm.importer;

import dk.trifork.sdm.importer.exceptions.FileImporterException;

import java.io.File;
import java.util.List;


public interface FileImporter {

	public boolean checkRequiredFiles(List<File> files);

	public void run(List<File> files) throws FileImporterException;
}
