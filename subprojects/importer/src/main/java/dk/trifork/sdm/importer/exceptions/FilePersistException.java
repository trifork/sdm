package dk.trifork.sdm.importer.exceptions;

public class FilePersistException extends FileImporterException {

	private static final long serialVersionUID = -1593682792018765867L;

	public FilePersistException(String message) {

		super(message);
	}

	public FilePersistException(String message, Throwable cause) {

		super(message, cause);
	}

}
