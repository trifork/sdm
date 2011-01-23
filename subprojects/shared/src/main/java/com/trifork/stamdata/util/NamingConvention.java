package com.trifork.stamdata.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


public class NamingConvention {

	public static String getXMLTypeName(Class<? extends Record> entity) {

		String name = getResourceName(entity);
		return decapitalize(name);
	}


	public static String getXMLElementName(Method method) {

		String name = getColumnName(method);
		return decapitalize(name);
	}


	private static String decapitalize(String name) {

		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}


	public static String getResourceName(Class<?> entity) {

		String name;

		Entity annotation = entity.getAnnotation(Entity.class);

		if (annotation != null && !annotation.name().isEmpty()) {
			name = annotation.name();
		}
		else {
			name = entity.getSimpleName();
		}

		return name;
	}


	public static String getIdColumnName(Class<? extends Record> entity) {

		return getTableName(entity) + "PID";
	}


	public static String getColumnName(Method method) {

		assert method != null;
		assert method.getName().startsWith("get");

		Column annotation = method.getAnnotation(Column.class);

		String name;

		if (annotation != null && !annotation.name().isEmpty()) {

			name = annotation.name();
		}
		else {
			name = method.getName();
			name = name.substring(3);
		}

		return name;
	}


	public static String getTableName(Class<?> entitySet) {

		String name;

		Table annotation = entitySet.getAnnotation(Table.class);

		if (annotation != null && !annotation.name().isEmpty()) {
			name = annotation.name();
		}
		else {
			name = entitySet.getSimpleName();
		}

		return name;
	}


	public static List<Method> getColumns(Class<? extends Record> type) {

		List<Method> columns = new ArrayList<Method>();

		for (Method method : type.getMethods()) {

			// Compile the list of columns.

			if (method.isAnnotationPresent(Column.class)) {

				columns.add(method);
			}
		}

		return columns;
	}


	public static List<Method> getColumns(Class<? extends Record> type, int version) {

		List<Method> columns = new ArrayList<Method>();

		for (Method method : type.getMethods()) {

			// Output the property to this version if the version
			// is specified in the list of supported versions or
			// the list of versions is empty.

			boolean enabledForVersion = false;

			Versioned versioned = method.getAnnotation(Versioned.class);

			if (versioned != null && versioned.value().length != 0)

				for (int i : versioned.value()) {

					if (i == version) {
						enabledForVersion = true;
						break;
					}
				}
			else {

				// Enable it is the column is not versioned.

				enabledForVersion = true;
			}

			// Compile the list of columns.

			if (method.isAnnotationPresent(Column.class) && enabledForVersion) {

				columns.add(method);
			}
		}

		return columns;
	}


	public static String getPIDName(Class<? extends Record> entitySet) {

		return getTableName(entitySet) + "PID";
	}
}
