package dk.trifork.sdm.importer.exceptions;

public class FileImporterException extends Exception {
	
	public FileImporterException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public FileImporterException(String message) {
		super(message);
	}
}
