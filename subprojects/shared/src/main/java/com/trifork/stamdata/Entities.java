package com.trifork.stamdata;


import java.lang.reflect.Method;
import java.util.*;

import javax.persistence.*;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.*;


public class Entities
{
	public static String getXMLTypeName(Class<? extends Record> entity)
	{
		String name;

		XmlName annotation = entity.getAnnotation(XmlName.class);

		if (annotation == null)
		{
			name = getName(entity);
		}
		else
		{
			name = annotation.value();
		}

		return decapitalize(name);
	}


	public static String getXMLElementName(Method method)
	{
		String name;

		XmlName annotation = method.getAnnotation(XmlName.class);

		if (annotation == null)
		{
			name = getColumnName(method);
		}
		else
		{
			name = annotation.value();
		}

		return decapitalize(name);
	}


	private static String decapitalize(String name)
	{
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}


	public static String getName(Class<?> entity)
	{
		String name;

		Entity annotation = entity.getAnnotation(Entity.class);

		if (annotation != null && !annotation.name().isEmpty())
		{
			name = annotation.name();
		}
		else
		{
			name = entity.getSimpleName();
		}

		return name;
	}


	public static String getIdColumnName(Class<? extends Record> entity)
	{
		return getTableName(entity) + "PID";
	}


	public static String getColumnName(Method method)
	{
		assert method != null;
		assert method.getName().startsWith("get");

		Column annotation = method.getAnnotation(Column.class);

		String name;

		if (annotation != null && !annotation.name().isEmpty())
		{

			name = annotation.name();
		}
		else
		{
			name = method.getName();
			name = name.substring(3);
		}

		return name;
	}


	public static String getTableName(Class<?> entitySet)
	{
		String name;

		Table annotation = entitySet.getAnnotation(Table.class);

		if (annotation != null && !annotation.name().isEmpty())
		{
			name = annotation.name();
		}
		else
		{
			name = entitySet.getSimpleName();
		}

		return name;
	}


	public static List<Method> getColumns(Class<? extends Record> type)
	{
		List<Method> columns = new ArrayList<Method>();

		for (Method method : type.getMethods())
		{
			// Compile the list of columns.

			if (method.isAnnotationPresent(Column.class))
			{

				columns.add(method);
			}
		}

		return columns;
	}


	public static List<Method> getColumns(Class<? extends Record> type, int version)
	{
		List<Method> columns = new ArrayList<Method>();

		for (Method method : type.getMethods())
		{
			// Output the property to this version if the version
			// is specified in the list of supported versions or
			// the list of versions is empty.

			boolean enabledForVersion = false;

			Versioned versioned = method.getAnnotation(Versioned.class);

			if (versioned != null && versioned.value().length != 0)

				for (int i : versioned.value())
				{

					if (i == version)
					{
						enabledForVersion = true;
						break;
					}
				}
			else
			{
				// Enable it is the column is not versioned.

				enabledForVersion = true;
			}

			// Compile the list of columns.

			if (method.isAnnotationPresent(Column.class) && enabledForVersion)
			{
				columns.add(method);
			}
		}

		return columns;
	}


	public static String getPIDName(Class<? extends Record> entitySet)
	{
		return getTableName(entitySet) + "PID";
	}


	@SuppressWarnings("unchecked")
	public static Set<Class<? extends Record>> all()
	{
		if (EntityHelper.resourceTypes == null)
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
	
			EntityHelper.resourceTypes = resources;
		}
	
		return EntityHelper.resourceTypes;
	}
}
