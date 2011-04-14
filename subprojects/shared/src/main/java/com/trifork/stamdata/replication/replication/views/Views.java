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

package com.trifork.stamdata.replication.replication.views;

import static com.trifork.stamdata.Preconditions.checkArgument;
import static com.trifork.stamdata.Preconditions.checkNotNull;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.vfs.JBoss6UrlType;
import org.reflections.vfs.Vfs;


/**
 * Convenience methods for working with views.
 *
 * @author Thomas Børlum (thb@trifork.com)
 */
public final class Views {

	private static final Pattern uriRegex = Pattern.compile("stamdata://(.+)");

	/**
	 * Checks that the view has be configured correctly.
	 *
	 * All views must be annotated with {@link Entity}.
	 * All views must be annotated with {@link ViewPath}.
	 * All views must be annotated with {@link XmlRootElement}.
	 */
	public static void checkViewIntegrity(Class<? extends View> viewClass) {

		checkNotNull(viewClass);

		checkArgument(viewClass.isAnnotationPresent(Entity.class));
		checkArgument(viewClass.isAnnotationPresent(XmlRootElement.class));
		checkArgument(viewClass.isAnnotationPresent(ViewPath.class));
	}

	public static String convertStamdataUriToViewName(String stamdataURI) {

		checkNotNull(stamdataURI);

		Matcher matcher = uriRegex.matcher(stamdataURI);
		return matcher.find() ? matcher.group(1) : null;
	}

	public static String getViewPath(Class<? extends View> viewClass) {

		checkViewIntegrity(viewClass);
		return viewClass.getAnnotation(ViewPath.class).value();
	}

	public static Set<Class<?>> findAllViews() {

		Vfs.addDefaultURLTypes(new JBoss6UrlType());

		Reflections reflector = new Reflections(new ConfigurationBuilder()
			.setUrls(ClasspathHelper.getUrlForName(Views.class))
			.setScanners(new TypeAnnotationsScanner()));

		return reflector.getTypesAnnotatedWith(ViewPath.class);
	}
}
