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

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import com.sun.xml.fastinfoset.stax.factory.StAXOutputFactory;
import com.trifork.stamdata.views.View;
import com.trifork.stamdata.views.ViewXmlHelper;
import com.trifork.stamdata.views.cpr.BarnRelation;
import com.trifork.stamdata.views.cpr.Beskyttelse;
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
import com.trifork.stamdata.views.usagelog.AggregateUsageLogEntry;
import com.trifork.stamdata.views.usagelog.UsageLogEntry;

public class Main {
	
	@SuppressWarnings("serial")
	private static final List<Class<? extends View>> views = new ArrayList<Class<? extends View>>() {{
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
		add(AggregateUsageLogEntry.class);
		add(Beskyttelse.class);
	}};
	
	private static List<String> viewNames = new ArrayList<String>() {{
		for(Class<?> view : views) {
			add(view.getSimpleName());
		}
	}};

	public static class ParameterDescriptor {
		public ParameterDescriptor(String description,
				String defaultValue, String[] options) {
			super();
			this.description = description;
			this.defaultValue = defaultValue;
			this.options = options;
		}
		public String description;
		public String defaultValue;
		public String[] options;
	}
	
	public static Map<String,ParameterDescriptor> parameterDescriptors = new HashMap<String, ParameterDescriptor>() {{
		put("stamdata.client.truststore", new ParameterDescriptor("Truststore som anvendes af klienten", "classpath:/truststore.jks", null));
		put("stamdata.client.truststore.password", new ParameterDescriptor("Kodeord til truststore", "Test1234", null));
		put("stamdata.client.keystore", new ParameterDescriptor("Keystore som anvendes af klienten", "classpath:/keystore.jks", null));
		put("stamdata.client.keystore.password", new ParameterDescriptor("Kodeord til keystore", "Test1234", null));
		put("stamdata.client.security", new ParameterDescriptor("Sikkerhed", "1", new String[] {"ssl", "dgws", "none" }));
		put("stamdata.client.view", new ParameterDescriptor("View", "1", viewNames.toArray(new String[] {})));
		put("stamdata.client.url", new ParameterDescriptor("Server URL", "https://stage.kombit.netic.dk/replication", null));
		put("stamdata.client.starttag", new ParameterDescriptor("Start tag", "", null));
		put("stamdata.client.pagesize", new ParameterDescriptor("Records pr. side", "5000", null));
		put("stamdata.client.outputfile", new ParameterDescriptor("Fil til output", null, null));
	}};
	
	public static String getParameter(String key) {
		ParameterDescriptor descriptor = parameterDescriptors.get(key);
		String sysProp = System.getProperty(key);
		if(sysProp != null) {
			if(descriptor.options == null) {
				return sysProp;
			}
			else {
				
				return Integer.toString(Arrays.asList(descriptor.options).indexOf(sysProp) + 1);
			}
		}
		if(descriptor.options != null) {
			System.out.println("Options are " + Arrays.toString(descriptor.options) + " property are " + sysProp);
			for(int i = 0; i < descriptor.options.length; ++i) {
				System.out.println((i+1) +". " + descriptor.options[i]);
			}
		}
		String chosen = ask(key, descriptor.description, descriptor.defaultValue);
		return chosen;
	}
	
	public static void main(String... args) throws Exception {
		String serverAndPort = getParameter("stamdata.client.url");
		SecurityMethod security = toSecurity(getParameter("stamdata.client.security"));
		int selectedViewIndex = Integer.parseInt(getParameter("stamdata.client.view")) - 1;
		Class<? extends View> selectedView = views.get(selectedViewIndex);
		
		String startTag = getParameter("stamdata.client.starttag");
		
		RegistryClient client = new RegistryClient(serverAndPort + "/stamdata/", security);
		int pageSize = Integer.parseInt(getParameter("stamdata.client.pagesize"));
		String fileName = getParameter("stamdata.client.outputfile");
		if(fileName == null || fileName.trim().isEmpty()) {
			fetch(client, selectedView, startTag, pageSize);
		}
		else {
			FileOutputStream fos = new FileOutputStream(fileName);
			outputEntities(fos, client, selectedView, startTag != null && startTag.trim().isEmpty() ? null : startTag , pageSize, false);
		}
	}
	public static <T extends View> void outputEntities(OutputStream outputStream, RegistryClient client, Class<T> viewClass, String startTag, int pageSize, boolean useFastInfoset) throws Exception {
		ViewXmlHelper viewXmlHelper = new ViewXmlHelper(viewClass);
		XMLStreamWriter writer;
		
		writer = (useFastInfoset)
			? StAXOutputFactory.newInstance().createXMLStreamWriter(outputStream, "UTF-8")
			: XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, "UTF-8");
		
		Iterator<EntityRevision<T>> iterator = client.update(viewClass, startTag, pageSize);
			
		writer.writeStartDocument("utf-8", "1.0");
		String lastTag = null;
		int writtenRecords = 0;
		
		writer.writeStartElement("common", "records",  "http://trifork.com/-/stamdata/3.0/common");
		writer.writeNamespace("common", "http://trifork.com/-/stamdata/3.0/common");
		writer.writeNamespace("sd", viewXmlHelper.getNamespace(viewClass.newInstance()));
		Marshaller marshaller = viewXmlHelper.createMarshaller(viewClass);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		while(iterator.hasNext()) {
			EntityRevision<T> entityRevision = iterator.next();
			marshaller.marshal(entityRevision.getEntity(), writer);
			writtenRecords++;
			lastTag = entityRevision.getId();
		}

		System.out.println("last entity tag: " + lastTag);
		System.out.println("number of entities: " + writtenRecords);

		writer.writeEndDocument();
	}

	private static SecurityMethod toSecurity(String s) {
		int response = Integer.parseInt(s);
		return SecurityMethod.values()[response - 1];
	}

	private static String ask(String sysProp, String question, String defaultValue) {
		String response = System.console().readLine("(%s) %s [%s] ", sysProp, question, defaultValue);
		return response.isEmpty() ? defaultValue : response;
	}

	private static void fetch(RegistryClient client, Class<?> selectedView, String startTag, int pageSize) throws Exception {
		client.updateAndPrintStatistics(selectedView, startTag.isEmpty() ? null : startTag, pageSize);
	}
}
