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
package dk.nsi.stamdata.replication.introspection;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import dk.nsi.stamdata.replication.exceptions.ConfigurationException;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class IntrospectionConfig {

    private static final Logger logger = Logger.getLogger(IntrospectionConfig.class);
    private static final String CONFIG_PROP_NAME = "introspection.config";

    private Set<RegisterConfig> registers = new HashSet<RegisterConfig>();

    public static class RegisterConfig {
        public final String name;
        public Map<String, String> dataTypeTableMapping = new HashMap<String, String>();

        public RegisterConfig(String name) {
            this.name = name;
        }
    }

    @Inject
    public IntrospectionConfig(@Named(CONFIG_PROP_NAME) String configName) {
        JsonNode configRoot;
        logger.info("Loading introspection config from: " + configName);
        configRoot = loadJsonConfig(configName);
        loadConfigFromJson(configRoot);
    }

    public Set<RegisterConfig> getRegisters() {
        return registers;
    }

    /**
     * Load the table settings from a root json node
     * @param configRoot config root node
     */
    private void loadConfigFromJson(JsonNode configRoot) {
        JsonNode registerNameNodes = configRoot.get("registers");
        Iterator<String> registerNames = registerNameNodes.getFieldNames();
        while (registerNames.hasNext()) {
            String registerName = registerNames.next();
            RegisterConfig registerConfig = new RegisterConfig(registerName);

            JsonNode registerNode = registerNameNodes.path(registerName);
            Iterator<String> datatypeNodes = registerNode.getFieldNames();
            while (datatypeNodes.hasNext()) {
                String datatype = datatypeNodes.next();
                String table = registerNode.findValue(datatype).asText();
                registerConfig.dataTypeTableMapping.put(datatype, table);
            }
            registers.add(registerConfig);
        }
    }

    /**
     * Load a json configuration file from either classpath or filesystem
     * @param configName name of file to load prefixed with either classpath: or file:
     * @return root json node
     */
    private JsonNode loadJsonConfig(String configName) {
        JsonNode configRoot;
        try {
            ObjectMapper m = new ObjectMapper();
            if (configName.contains("classpath:")) {
                String filename = configName.substring("classpath:".length());
                InputStream configResource = IntrospectionConfig.class.getClassLoader().getResourceAsStream(filename);
                configRoot = m.readTree(configResource);
            } else if (configName.contains("file:")) {
                String filename = configName.substring("file:".length());
                configRoot = m.readTree(new File(filename));
            } else {
                throw new ConfigurationException(CONFIG_PROP_NAME + " property does not contain classpath: or file:");
            }
        } catch (IOException e) {
            throw new ConfigurationException(CONFIG_PROP_NAME + " error while parsing config file", e);
        }
        return configRoot;
    }

}
