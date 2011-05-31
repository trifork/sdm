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

package com.trifork.stamdata.replication.replication;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.bind.JAXBException;

import com.google.inject.TypeLiteral;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.replication.replication.annotations.Registry;
import com.trifork.stamdata.views.View;
import com.trifork.stamdata.views.ViewPath;
import com.trifork.stamdata.views.Views;


public class RegistryModule extends ServletModule {

	@SuppressWarnings("unchecked")
	@Override
	protected final void configureServlets() {

		// DISCOVER ALL VIEW CLASSES
		//
		// For speed only the model package will be searched.
		// Because the war can be deployed in many ways we may
		// have to search in several places.

		Set<Class<?>> views = Views.findAllViews();

		// MAP VIEWS TO THEIR PATHS
		//
		// Map the view classes to their respective registry/view/version.
		// Bind the map to the Map<String, Class> annotated with @Registry.
		//
		// A tree map is used so the entries are lexically sorted.

		Map<String, Class<? extends View>> registry = new TreeMap<String, Class<? extends View>>();

		for (Class<?> entity : views)
		{
			ViewPath annotation = entity.getAnnotation(ViewPath.class);
			registry.put(annotation.value(), (Class<? extends View>) entity);
		}

		bind(new TypeLiteral<Map<String, Class<? extends View>>>() {}).annotatedWith(Registry.class).toInstance(registry);
		
		// BIND THE FEED WRITER
		//
		// We need a JAXB context that can marshal all
		// the entities to XML.

		bind(AtomFeedWriter.class);
		
		try {
			bind(ViewXmlHelper.class).toInstance(new ViewXmlHelper(views));
		}
		catch (JAXBException e) {
			addError(e);
		}

		// SERVE THE REGISTRIES
		//
		// All requests default to Stamdata default to this servlet.

		serve("/stamdata/*").with(RegistryServlet.class);
	}
}
