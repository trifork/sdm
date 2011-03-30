package com.trifork.stamdata.replication.replication;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.replication.replication.annotations.Registry;
import com.trifork.stamdata.replication.replication.annotations.ViewPath;
import com.trifork.stamdata.replication.replication.views.View;


public class RegistryModule extends ServletModule {

	private JAXBContext context;

	@Override
	@SuppressWarnings("unchecked")
	protected void configureServlets() {

		// DISCOVER ALL VIEW CLASSES
		//
		// For speed only the model package will be searched.

		Reflections reflector = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.getUrlsForCurrentClasspath()).setScanners(new TypeAnnotationsScanner()));
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
			context = JAXBContext.newInstance(classes.toArray(new Class[0]), null);
		}
		catch (JAXBException e) {
			addError(e);
		}

		// SERVE THE REGISTRIES
		//
		// All requests default to Stamdata default to this servlet.

		serve("/stamdata/*").with(RegistryServlet.class);
	}

	@Provides
	@Registry
	public Marshaller provideEntityMarshaller() throws JAXBException {

		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

		return marshaller;
	}
}
