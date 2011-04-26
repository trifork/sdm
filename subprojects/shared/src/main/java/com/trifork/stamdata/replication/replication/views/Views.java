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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import com.trifork.stamdata.replication.replication.views.cpr.BarnRelation;
import com.trifork.stamdata.replication.replication.views.cpr.Civilstand;
import com.trifork.stamdata.replication.replication.views.cpr.Foedselsregistreringsoplysninger;
import com.trifork.stamdata.replication.replication.views.cpr.Folkekirkeoplysninger;
import com.trifork.stamdata.replication.replication.views.cpr.ForaeldremyndighedsRelation;
import com.trifork.stamdata.replication.replication.views.cpr.Haendelse;
import com.trifork.stamdata.replication.replication.views.cpr.KommunaleForhold;
import com.trifork.stamdata.replication.replication.views.cpr.MorOgFarOplysninger;
import com.trifork.stamdata.replication.replication.views.cpr.Person;
import com.trifork.stamdata.replication.replication.views.cpr.Statsborgerskab;
import com.trifork.stamdata.replication.replication.views.cpr.Udrejseoplysninger;
import com.trifork.stamdata.replication.replication.views.cpr.UmyndiggoerelseVaergeRelation;
import com.trifork.stamdata.replication.replication.views.cpr.Valgoplysninger;


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
		return new HashSet<Class<?>>() {{
			add(BarnRelation.class);
			add(Civilstand.class);
			add(Foedselsregistreringsoplysninger.class);
			add(Folkekirkeoplysninger.class);
			add(ForaeldremyndighedsRelation.class);
			add(Haendelse.class);
			add(KommunaleForhold.class);
			add(MorOgFarOplysninger.class);
			add(Person.class);
			add(Statsborgerskab.class);
			add(Udrejseoplysninger.class);
			add(UmyndiggoerelseVaergeRelation.class);
			add(Valgoplysninger.class);
		}};
	}
}
