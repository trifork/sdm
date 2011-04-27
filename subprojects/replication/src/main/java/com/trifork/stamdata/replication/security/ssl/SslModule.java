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

package com.trifork.stamdata.replication.security.ssl;

import static org.slf4j.LoggerFactory.getLogger;

import org.openoces.ooapi.environment.Environments;
import org.openoces.ooapi.environment.Environments.Environment;
import org.slf4j.Logger;

import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.replication.security.SecurityManager;

public class SslModule extends ServletModule {
	private static final Logger logger = getLogger(SslModule.class);
	private final boolean test;

	public SslModule(boolean test) {
		this.test = test;
	}

	@Override
	protected void configureServlets() {
		if (test) {
			logger.warn("Using OCES test environment");
			Environments.setEnvironments(
					Environment.OCESI_DANID_ENV_DEVELOPMENT,
					Environment.OCESI_DANID_ENV_SYSTEMTEST,
					Environment.OCESII_DANID_ENV_DEVELOPMENT,
					Environment.OCESII_DANID_ENV_DEVELOPMENTTEST,
					Environment.OCESII_DANID_ENV_EXTERNALTEST,
					Environment.OCESII_DANID_ENV_IGTEST,
					Environment.OCESII_DANID_ENV_INTERNALTEST,
					Environment.OCESII_DANID_ENV_OPERATIONSTEST,
					Environment.OCESII_DANID_ENV_PREPROD);
		} else {
			Environments.setEnvironments(
					Environment.OCESI_DANID_ENV_PROD,
					Environment.OCESII_DANID_ENV_PROD);
		}
		bind(SecurityManager.class).to(SslSecurityManager.class);
	}
}
