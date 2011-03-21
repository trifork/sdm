package dk.trifork.sdm.importer.exceptions;

public class FileParseException extends FileImporterException {

	private static final long serialVersionUID = -4956915985637502728L;

	public FileParseException(String message) {

		super(message);
	}

	public FileParseException(String message, Throwable cause) {

		super(message, cause);
	}
}
