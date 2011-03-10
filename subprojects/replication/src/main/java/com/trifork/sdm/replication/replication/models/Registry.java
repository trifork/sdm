package com.trifork.sdm.replication.replication.models;

import java.util.*;

import javax.persistence.Entity;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.*;

public class Registry implements Iterable<Class<? extends Record>>
{
	private static final Map<String, Class<? extends Record>> registry = configure();


	public Iterator<Class<? extends Record>> iterator()
	{
		return registry.values().iterator();
	}


	@SuppressWarnings("unchecked")
	protected static Map<String, Class<? extends Record>> configure()
	{
		String PACKAGE = Registry.class.getPackage().getName();

		Reflections reflector = new Reflections(new ConfigurationBuilder().filterInputsBy(new FilterBuilder.Include(FilterBuilder.prefix(PACKAGE))).setUrls(ClasspathHelper.getUrlsForPackagePrefix(PACKAGE)).setScanners(new TypeAnnotationsScanner()));

		Set<Class<?>> classes = reflector.getTypesAnnotatedWith(Entity.class);

		Map<String, Class<? extends Record>> registry = new HashMap<String, Class<? extends Record>>(classes.size());

		for (Class<?> entity : classes)
		{
			Entity annotation = entity.getAnnotation(Entity.class);
			registry.put(annotation.name(), (Class<? extends Record>) entity);
		}

		return registry;
	}
}
