package com.trifork.stamdata.replication.replication.views;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import com.trifork.stamdata.replication.replication.annotations.ViewPath;

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

	// TODO: Javadoc
	public static String convertStamdataUriToViewName(String stamdataURI) {

		checkNotNull(stamdataURI);		
		
		Matcher matcher = uriRegex.matcher(stamdataURI);
		return matcher.find() ? matcher.group(1) : null;
	}
	
	public static String getViewName(Class<? extends View> viewClass) {

		checkViewIntegrity(viewClass);
		return viewClass.getAnnotation(Entity.class).name();
	}
}
