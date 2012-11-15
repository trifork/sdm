/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */
package com.trifork.stamdata;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.trifork.stamdata.Preconditions.checkNotNull;

public final class ConfigurationLoader {
    private static final Logger logger = Logger.getLogger(ConfigurationLoader.class);

    protected ConfigurationLoader() {
    }

    public static Properties loadForName(String componentName) {
        checkNotNull(componentName, "componentName");

        // LOAD CONFIGURATION FILES

        ClassLoader classLoader = ConfigurationLoader.class.getClassLoader();

        InputStream buildInConfig = classLoader.getResourceAsStream("config.properties");
        InputStream testConfig = classLoader.getResourceAsStream("test-config.properties");
        InputStream deploymentConfig = classLoader.getResourceAsStream(componentName + ".properties");

        final Properties config = new Properties();

        try {
            config.load(buildInConfig);
            logger.debug("Configuration file 'config.properties' loaded");
            buildInConfig.close();

            if (testConfig != null) {
                logger.info("Test Configuration file 'test-config.properties' found.");

                config.load(testConfig);
            }

            if (deploymentConfig != null) {
                logger.info("Configuration file '"+componentName+".properties' found.");

                config.load(deploymentConfig);
            } else {
                logger.warn("Could not find "+componentName+".properties. Using default configuration.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Problem loading the component's configuration files. component=" + componentName, e);
        } finally {
            IOUtils.closeQuietly(buildInConfig);
            IOUtils.closeQuietly(testConfig);
            IOUtils.closeQuietly(deploymentConfig);
        }

        return config;
    }
}
