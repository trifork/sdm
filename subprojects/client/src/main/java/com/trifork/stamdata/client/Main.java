package com.trifork.stamdata.client;

import java.util.Iterator;

import org.apache.commons.lang.time.StopWatch;

import com.trifork.stamdata.replication.replication.views.cpr.Person;

public class Main {
	public static void main(String... args) throws Exception {
		String serverAndPort = ask("Server and port", "http://localhost:8080/replication/stamdata/");
		boolean security = ask("Security enabled?", "n").toLowerCase().equals("y");

		RegistryClient client = new RegistryClient(serverAndPort, security);
		fetch(client);
	}

	private static String ask(String question, String defaultValue) {
		String response = System.console().readLine("%s [%s] ", question, defaultValue);
		return response.isEmpty() ? defaultValue : response;
	}

	private static void fetch(RegistryClient client) throws Exception {
		Iterator<EntityRevision<Person>> revisions = client.update(Person.class, null, 5000);

		int recordCount = 0;

		StopWatch timer = new StopWatch();
		timer.start();

		while (revisions.hasNext()) {
			recordCount++;
			EntityRevision<Person> revision = revisions.next();
			printRevision(revision);
		}

		timer.stop();

		printStatistics(recordCount, timer);
	}

	private static void printRevision(EntityRevision<?> revision) {
		System.out.println(revision.getId() + ": " + revision.getEntity());
	}

	private static void printStatistics(int i, StopWatch timer) {
		System.out.println();
		System.out.println("Time used: " + timer.getTime() / 1000. + " sec.");
		System.out.println("Record count: " + i);
	}

}
