package dk.trifork.sdm.jobspooler;


public interface Job {
	public void run() throws JobException;
}
