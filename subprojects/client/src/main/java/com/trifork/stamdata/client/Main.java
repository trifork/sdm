package com.trifork.stamdata.client;

import java.util.ArrayList;
import java.util.List;

import com.trifork.stamdata.replication.replication.views.cpr.BarnRelation;
import com.trifork.stamdata.replication.replication.views.cpr.Civilstand;
import com.trifork.stamdata.replication.replication.views.cpr.Foedselsregistreringsoplysninger;
import com.trifork.stamdata.replication.replication.views.cpr.Folkekirkeoplysninger;
import com.trifork.stamdata.replication.replication.views.cpr.ForaeldremyndighedsRelation;
import com.trifork.stamdata.replication.replication.views.cpr.Haendelse;
import com.trifork.stamdata.replication.replication.views.cpr.KommunaleForhold;
import com.trifork.stamdata.replication.replication.views.cpr.MorOgFarOplysninger;
import com.trifork.stamdata.replication.replication.views.cpr.Person;
import com.trifork.stamdata.replication.replication.views.cpr.Statsborgerskab;
import com.trifork.stamdata.replication.replication.views.cpr.Udrejseoplysninger;
import com.trifork.stamdata.replication.replication.views.cpr.UmyndiggoerelseVaergeRelation;
import com.trifork.stamdata.replication.replication.views.cpr.Valgoplysninger;
import com.trifork.stamdata.replication.replication.views.usagelog.UsageLogEntry;

public class Main {
	@SuppressWarnings("serial")
	private static final List<Class<?>> views = new ArrayList<Class<?>>() {{
		add(Person.class);
		add(BarnRelation.class);
		add(Civilstand.class);
		add(Foedselsregistreringsoplysninger.class);
		add(Folkekirkeoplysninger.class);
		add(ForaeldremyndighedsRelation.class);
		add(Haendelse.class);
		add(KommunaleForhold.class);
		add(MorOgFarOplysninger.class);
		add(Statsborgerskab.class);
		add(Udrejseoplysninger.class);
		add(UmyndiggoerelseVaergeRelation.class);
		add(Valgoplysninger.class);
		add(UsageLogEntry.class);
	}};
	
	public static void main(String... args) throws Exception {
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
