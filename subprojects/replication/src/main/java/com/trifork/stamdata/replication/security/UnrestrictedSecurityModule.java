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

package com.trifork.stamdata.replication.security;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.google.inject.servlet.ServletModule;

public class UnrestrictedSecurityModule extends ServletModule {
	private static final Logger logger = getLogger(UnrestrictedSecurityModule.class);

	@Override
	protected void configureServlets() {
		logger.warn("Security is disabled!");
		bind(SecurityManager.class).to(UnrestrictedSecurityManager.class);
	}
}
