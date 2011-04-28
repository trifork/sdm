// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// Contributor(s): Contributors are attributed in the source code
// where applicable.
// 
// The Original Code is "Stamdata".
// 
// The Initial Developer of the Original Code is Trifork Public A/S.
// 
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
// 
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.client;

import java.util.ArrayList;
import java.util.List;

import com.trifork.stamdata.views.cpr.BarnRelation;
import com.trifork.stamdata.views.cpr.Civilstand;
import com.trifork.stamdata.views.cpr.Foedselsregistreringsoplysninger;
import com.trifork.stamdata.views.cpr.Folkekirkeoplysninger;
import com.trifork.stamdata.views.cpr.ForaeldremyndighedsRelation;
import com.trifork.stamdata.views.cpr.Haendelse;
import com.trifork.stamdata.views.cpr.KommunaleForhold;
import com.trifork.stamdata.views.cpr.MorOgFaroplysninger;
import com.trifork.stamdata.views.cpr.Person;
import com.trifork.stamdata.views.cpr.Statsborgerskab;
import com.trifork.stamdata.views.cpr.Udrejseoplysninger;
import com.trifork.stamdata.views.cpr.UmyndiggoerelseVaergeRelation;
import com.trifork.stamdata.views.cpr.Valgoplysninger;
import com.trifork.stamdata.views.usagelog.UsageLogEntry;

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
		add(MorOgFaroplysninger.class);
		add(Statsborgerskab.class);
		add(Udrejseoplysninger.class);
		add(UmyndiggoerelseVaergeRelation.class);
		add(Valgoplysninger.class);
		add(UsageLogEntry.class);
	}};
	
	public static void main(String... args) throws Exception {
		String serverAndPort = ask("Server and port", "http://localhost:8080/replication");
		Security security = toSecurity(ask("Security (none/dgws/ssl)", "none").toLowerCase());;

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

	private static Security toSecurity(String s) {
		if (s.equals("ssl")) {
			return Security.ssl;
		} else if (s.equals("dgws")) {
			return Security.dgws;
		}
		return Security.none;
	}

	private static String ask(String question, String defaultValue) {
		String response = System.console().readLine("%s [%s] ", question, defaultValue);
		return response.isEmpty() ? defaultValue : response;
	}

	private static void fetch(RegistryClient client, Class<?> selectedView, String startTag) throws Exception {
		client.updateAndPrintStatistics(selectedView, startTag.isEmpty() ? null : startTag, 5000);
	}
}
