package dk.trifork.sdm.jobspooler;

public class JobException extends Exception {

	private static final long serialVersionUID = 4805896456574735730L;

	public JobException(String message, Throwable cause) {

		super(message, cause);
	}

	public JobException(String message) {

		super(message);
	}
}
