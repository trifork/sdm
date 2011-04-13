package com.trifork.stamdata.client;

import com.trifork.stamdata.replication.replication.views.cpr.Person;

public class Main {
	public static void main(String... args) throws Exception {
		String serverAndPort = ask("Server and port", "http://localhost:8080");
		boolean security = ask("Security enabled?", "n").toLowerCase().equals("y");

		RegistryClient client = new RegistryClient(serverAndPort + "/replication/stamdata/", security);
		fetch(client);
	}

	private static String ask(String question, String defaultValue) {
		String response = System.console().readLine("%s [%s] ", question, defaultValue);
		return response.isEmpty() ? defaultValue : response;
	}

	private static void fetch(RegistryClient client) throws Exception {
		client.updateAndPrintStatistics(Person.class, null, 5000);
	}
}
