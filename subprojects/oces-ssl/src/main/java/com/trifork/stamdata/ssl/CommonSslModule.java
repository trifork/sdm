package com.trifork.stamdata.ssl;

import org.openoces.ooapi.environment.Environments;
import org.openoces.ooapi.environment.Environments.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.ServletModule;

public class CommonSslModule extends ServletModule {
	private final boolean test;
	private static final Logger logger = LoggerFactory.getLogger(CommonSslModule.class);
	private final String sslTerminationMethod;

	public CommonSslModule(boolean test, String sslTerminationMethod) {
		this.test = test;
		this.sslTerminationMethod = sslTerminationMethod;
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
		if("container".equals(sslTerminationMethod)) {
			bind(CertificateExtractor.class).to(ContainerSslTerminatedCertificateExtractor.class);
		}
		else if("zeusLoadBalancer".equals(sslTerminationMethod)) {
			bind(CertificateExtractor.class).to(ZeusSslTerminatedCertificateExtractor.class);
		}
		else {
			addError("Illegal configuration parameter for security.ssl.termination.method: {}", sslTerminationMethod);
		}
	}
}
