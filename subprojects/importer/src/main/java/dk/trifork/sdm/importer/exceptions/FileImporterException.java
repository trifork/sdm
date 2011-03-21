package dk.trifork.sdm.importer.exceptions;

public class FileImporterException extends Exception {

	private static final long serialVersionUID = -1565028705535915764L;

	public FileImporterException(String message) {

		super(message);
	}

	public FileImporterException(String message, Throwable cause) {

		super(message, cause);
	}
}
