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

package com.trifork.stamdata;

import java.util.Collection;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.config.Configuration;
import com.trifork.stamdata.spooler.SpoolerManager;
import com.trifork.stamdata.webinterface.DatabaseStatus;
import com.trifork.stamdata.webinterface.ImporterServlet;


public class ApplicationContextListener extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {

		Collection<Module> modules = Lists.newArrayList();
		
		modules.add(new ServletModule() {
			@Override
			protected void configureServlets() {
			
				serve("/status").with(ImporterServlet.class);
				
				bind(ProjectInfo.class).in(Scopes.SINGLETON);
				bind(SpoolerManager.class).in(Scopes.SINGLETON);
				bind(DatabaseStatus.class).in(Scopes.SINGLETON);
				
				bindConstant().annotatedWith(Names.named("RootDir")).to(Configuration.getString("spooler.rootdir"));
			}
		});
		
		return Guice.createInjector(modules);
	}
}
