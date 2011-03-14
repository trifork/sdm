package com.trifork.stamdata.replication.replication.views;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.Entity;

/**
 * Convinience methods for working with views.
 * 
 * @author Thomas Børlum (thb@trifork.com)
 */
public final class Views {

	private static final Pattern uriRegex = Pattern.compile("stamdata://(.+)");

	/**
	 * Checks that the view has be configured correctly.
	 * 
	 * All views must be annotated with {@link Entity}. All views must have a
	 * name specified by {@link Entity#name()}.
	 */
	public static void checkViewIntegrity(Class<? extends View> viewClass) {

		checkNotNull(viewClass);

		Entity annotation = viewClass.getAnnotation(Entity.class);
		checkNotNull(annotation);
		checkNotNull(annotation.name());

		// TODO: Check XML annotations.
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
