package com.trifork.stamdata.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.trifork.stamdata.replication.replication.views.Views;

public class Main {
	public static void main(String... args) throws Exception {
		List<Class<?>> views = new ArrayList<Class<?>>(Views.findAllViews());
		Collections.sort(views, new Comparator<Class<?>>() {
			@Override
			public int compare(Class<?> class1, Class<?> class2) {
				return class1.getName().compareTo(class2.getName());
			}});

		String serverAndPort = ask("Server and port", "http://localhost:8080/replication");
		boolean security = ask("Security enabled?", "n").toLowerCase().equals("y");

		for (int i=0; i<views.size(); i++) {
			Class<?> viewClass = views.get(i);
			System.out.println((i+1) + ": " + viewClass.getSimpleName());
		}
		int selectedViewIndex = Integer.parseInt(ask("Type", "1")) - 1;
		Class<?> selectedView = views.get(selectedViewIndex);
		
		String startTag = ask("Start-tag", "");
		
		RegistryClient client = new RegistryClient(serverAndPort + "/stamdata/", security);
		fetch(client, selectedView, startTag);
	}

	private static String ask(String question, String defaultValue) {
		String response = System.console().readLine("%s [%s] ", question, defaultValue);
		return response.isEmpty() ? defaultValue : response;
	}

	private static void fetch(RegistryClient client, Class<?> selectedView, String startTag) throws Exception {
		client.updateAndPrintStatistics(selectedView, startTag.isEmpty() ? null : startTag, 5000);
	}
}
