package com.trifork.stamdata;


import static java.lang.String.format;

import java.lang.reflect.Method;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;


public class EntityHelper
{
	public static Method getIdMethod(Class<? extends Record> entity)
	{
		Method id = null;

		for (Method method : entity.getMethods())
		{
			if (method.isAnnotationPresent(Id.class))
			{
				id = method;
			}
		}

		assert id != null : format("Entity %s does not have a @Id method.", entity.getSimpleName());

		return id;
	}


	public static Method getMethodForColumnName(Class<? extends Record> entitySet, String name)
	{
		for (Method method : NamingConvention.getColumns(entitySet))
		{
			if (name.equals(NamingConvention.getColumnName(method)))
			{
				return method;
			}
		}

		return null;
	}


	public static boolean isDateColumn(Class<? extends Record> entitySet, String name) throws SecurityException, NoSuchMethodException
	{
		return getMethodForColumnName(entitySet, name).getReturnType().equals(Date.class);
	}


	public static boolean isLongColumn(Class<? extends Record> entitySet, String name) throws SecurityException, NoSuchMethodException
	{
		return getMethodForColumnName(entitySet, name).getReturnType().equals(Long.class) || getMethodForColumnName(entitySet, name).getReturnType().equals(long.class);
	}


	public static boolean isBooleanColumn(Class<? extends Record> entitySet, String name) throws SecurityException, NoSuchMethodException
	{
		return getMethodForColumnName(entitySet, name).getReturnType().equals(Boolean.class) || getMethodForColumnName(entitySet, name).getReturnType().equals(boolean.class);
	}


	public static boolean isIntegerColumn(Class<? extends Record> entitySet, String name) throws SecurityException, NoSuchMethodException
	{
		return getMethodForColumnName(entitySet, name).getReturnType().equals(Integer.class) || getMethodForColumnName(entitySet, name).getReturnType().equals(int.class);
	}


	public static boolean isStringColumn(Class<? extends Record> entitySet, String name) throws SecurityException, NoSuchMethodException
	{
		return getMethodForColumnName(entitySet, name).getReturnType().equals(String.class);
	}


	private static Set<Class<? extends Record>> resourceTypes = null;


	@SuppressWarnings("unchecked")
	public static Set<Class<? extends Record>> getAllResources()
	{
		if (resourceTypes == null)
		{
			// Find all entities and serve them as resources.

			final String INCLUDE_PACKAGE = com.trifork.stamdata.Record.class.getPackage().getName();

			// TODO: Include doseringsforslag.

			// Right now we don't have an importer for doeringsforslag
			// so they cannot be replicated.

			final String EXCLUDE_PACKAGE = com.trifork.stamdata.registre.doseringsforslag.Drug.class.getPackage().getName();

			Reflections reflector = new Reflections(new ConfigurationBuilder()
				.setUrls(ClasspathHelper.getUrlsForPackagePrefix(INCLUDE_PACKAGE))
				.filterInputsBy(new FilterBuilder()
					.include(FilterBuilder.prefix(INCLUDE_PACKAGE))
					.exclude(FilterBuilder.prefix(EXCLUDE_PACKAGE)))
				.setScanners(new TypeAnnotationsScanner()));

			// Serve all entities by deferring their URLs and using their
			// annotations.

			Set<Class<?>> entities = reflector.getTypesAnnotatedWith(Entity.class);

			Set<Class<? extends Record>> resources = new HashSet<Class<? extends Record>>();

			for (Class<?> entity : entities)
			{
				resources.add((Class<? extends Record>) entity);
			}

			resourceTypes = resources;
		}

		return resourceTypes;
	}


	public static Class<? extends Record> getResourceByName(String name)
	{
		Class<? extends Record> type = null;

		for (Class<? extends Record> resourceType : getAllResources())
		{
			if (NamingConvention.getResourceName(resourceType).equals(name))
			{
				type = resourceType;
				break;
			}
		}

		return type;
	}
}
