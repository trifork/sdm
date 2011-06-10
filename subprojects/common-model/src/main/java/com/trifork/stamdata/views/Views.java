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

package com.trifork.stamdata.views;

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

	private static final Pattern URI_REGEX = Pattern.compile("stamdata://(.+)");

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

		Matcher matcher = URI_REGEX.matcher(stamdataURI);
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
