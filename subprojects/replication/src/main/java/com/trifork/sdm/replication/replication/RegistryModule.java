package com.trifork.sdm.replication.replication;

import java.util.*;

import javax.persistence.Entity;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.*;

import com.google.inject.TypeLiteral;
import com.google.inject.servlet.ServletModule;
import com.trifork.sdm.replication.replication.annotations.Registry;
import com.trifork.sdm.replication.replication.models.Record;

public class RegistryModule extends ServletModule {

	@Override
	@SuppressWarnings("unchecked")
	protected void configureServlets() {

		// DISCOVER ALL ENTITY CLASSES
		//
		// For speed only the model package will be searched.

		String MODEL_PACKAGE = Record.class.getPackage().getName();
		Reflections reflector = new Reflections(new ConfigurationBuilder().filterInputsBy(new FilterBuilder.Include(FilterBuilder.prefix(MODEL_PACKAGE))).setUrls(ClasspathHelper.getUrlsForPackagePrefix(MODEL_PACKAGE)).setScanners(new TypeAnnotationsScanner()));
		Set<Class<?>> classes = reflector.getTypesAnnotatedWith(Entity.class);

		// MAP ENTITIES TO THEIR NAMES
		//
		// Map the entity classes to their respective authority/name/version.
		// Bind the map to the Map<String, Class> annotated with @Registry.

		Map<String, Class<? extends Record>> registry = new HashMap<String, Class<? extends Record>>(classes.size());

		for (Class<?> entity : classes) {
			Entity annotation = entity.getAnnotation(Entity.class);
			registry.put(annotation.name(), (Class<? extends Record>) entity);
		}

		bind(new TypeLiteral<Map<String, Class<? extends Record>>>() {}).annotatedWith(Registry.class).toInstance(registry);

		// SERVE THE REGISTRIES
		//
		// All requests default to Stamdata default to this servlet.

		serve("/stamdata/*").with(RegistryServlet.class);
	}
}
