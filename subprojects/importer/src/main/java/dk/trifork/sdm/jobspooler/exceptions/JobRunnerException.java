package dk.trifork.sdm.jobspooler.exceptions;

public class JobRunnerException extends Exception {
	
	private static final long serialVersionUID = 4805896456574735730L;

	public JobRunnerException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public JobRunnerException(String message) {
		super(message);
	}
}
