package dk.trifork.sdm.importer;

import java.util.Calendar;


public interface FileImporterControlledIntervals extends FileImporter {

	public Calendar getNextImportExpectedBefore(Calendar lastImport);
}
