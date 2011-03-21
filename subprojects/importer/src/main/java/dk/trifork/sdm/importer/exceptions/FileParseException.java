package dk.trifork.sdm.importer.exceptions;

public class FileParseException extends FileImporterException {



	public FileParseException(String message, Throwable cause) {
		super(message, cause);
	}

    public FileParseException(String message) {
        super(message);
    }
}
