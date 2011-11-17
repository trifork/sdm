package com.trifork.stamdata.importer.config;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.net.URL;

@Deprecated
public class ConfigurationLoader
{
    private static CompositeConfiguration loadConfiguration()
    {
        // TODO: Use ConfigurationLoader instead.

        try
        {
            // Override the build-in configuration 'config.properties' with
            // the one found in 'stamdata-data-manager.properties'.
            //
            // Composite configurations always return the first version of a
            // property
            // that is added to it. Therefore we load the defaults last.

            CompositeConfiguration configuration = new CompositeConfiguration();

            URL deploymentConfigurationFile = getClass().getClassLoader().getResource(DEPLOYMENT_CONFIG_FILE);

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
