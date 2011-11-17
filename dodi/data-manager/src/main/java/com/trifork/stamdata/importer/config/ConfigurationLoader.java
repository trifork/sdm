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
package com.trifork.stamdata.importer.config;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

@Deprecated
public class ConfigurationLoader
{
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationLoader.class);
    
    public static final String BUILDIN_CONFIG_FILE = "config.properties";
    public static final String DEPLOYMENT_CONFIG_FILE = "stamdata-data-manager.properties";

    public static CompositeConfiguration loadConfiguration()
    {
        // TODO: Use ConfigurationLoader class in stamdata-common instead.

        try
        {
            // Override the build-in configuration 'config.properties' with
            // the one found in 'stamdata-data-manager.properties'.
            //
            // Composite configurations always return the first version of a
            // property
            // that is added to it. Therefore we load the defaults last.

            CompositeConfiguration configuration = new CompositeConfiguration();

            URL deploymentConfigurationFile = ConfigurationLoader.class.getClassLoader().getResource(DEPLOYMENT_CONFIG_FILE);

            if (deploymentConfigurationFile != null)
            {
                configuration.addConfiguration(new PropertiesConfiguration(deploymentConfigurationFile));
                logger.info("Configuration file '{}' loaded.", deploymentConfigurationFile);
            }
            else
            {
                logger.warn("Configuration file '{}' could not be found. Using default configuration.", DEPLOYMENT_CONFIG_FILE);
            }

            // Add any missing properties from the defaults.

            configuration.addConfiguration(new PropertiesConfiguration(BUILDIN_CONFIG_FILE));

            return configuration;

        }
        catch (Exception e)
        {
            throw new RuntimeException("The application could not be started do to a configuration error.", e);
        }
    }
}
