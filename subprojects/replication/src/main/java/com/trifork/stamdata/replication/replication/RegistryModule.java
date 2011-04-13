// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package com.trifork.stamdata.replication.replication;

import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.bind.JAXBException;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.google.inject.TypeLiteral;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.replication.ApplicationContextListener;
import com.trifork.stamdata.replication.replication.annotations.Registry;
import com.trifork.stamdata.replication.replication.annotations.ViewPath;
import com.trifork.stamdata.replication.replication.views.View;


public class RegistryModule extends ServletModule {

	@SuppressWarnings("unchecked")
	@Override
	protected final void configureServlets() {

		// DISCOVER ALL VIEW CLASSES
		//
		// For speed only the model package will be searched.
		// Because the war can be deployed in many ways we may
		// have to search in several places.

		URL searchPath = ClasspathHelper.getUrlForWebInfClasses(getServletContext());
		Reflections reflector;
		
		if (searchPath != null) {
			reflector = new Reflections(new ConfigurationBuilder()
				.setUrls(searchPath)
				.setScanners(new TypeAnnotationsScanner()));
		}
		else {
			reflector = new Reflections(new ConfigurationBuilder()
				.setUrls(ClasspathHelper.getUrlForName(ApplicationContextListener.class))
				.setScanners(new TypeAnnotationsScanner()));
		}
		
		Set<Class<?>> classes = reflector.getTypesAnnotatedWith(ViewPath.class);

		// MAP VIEWS TO THEIR PATHS
		//
		// Map the view classes to their respective registry/view/version.
		// Bind the map to the Map<String, Class> annotated with @Registry.
		//
		// A tree map is used so the entries are lexically sorted.

		Map<String, Class<? extends View>> registry = new TreeMap<String, Class<? extends View>>();

		for (Class<?> entity : classes) {
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
			bind(ViewXmlHelper.class).toInstance(new ViewXmlHelper(classes));
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
