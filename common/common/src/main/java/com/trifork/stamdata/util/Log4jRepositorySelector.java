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

package com.trifork.stamdata.util;

import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RepositorySelector;
import org.apache.log4j.spi.RootLogger;
import org.apache.log4j.xml.DOMConfigurator;
import org.w3c.dom.Document;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * JBoss6 really makes it hard to do application specific logging using your own log4j configuration.
 *
 * However using a custom repositoryselector might do the trick
 * https://community.jboss.org/wiki/Log4jRepositorySelector
 *
 *
 * User: frj
 * Date: 3/1/12
 * Time: 11:13 AM
 *
 * @Author frj
 */
public class Log4jRepositorySelector implements RepositorySelector {

    private static boolean initialized = false;
    private static Object guard = LogManager.getRootLogger();
    private static Map repositories = new HashMap();
    private static LoggerRepository defaultRepository;

    public static synchronized void init(ServletConfig servletConfig, String log4jConfigurationPath)
            throws ServletException {
        init(servletConfig.getServletContext(), log4jConfigurationPath);
    }

    public static synchronized void init(ServletContext servletContext, String log4jConfigurationPath)
            throws ServletException {
        if (!initialized) // set the global RepositorySelector
        {
            defaultRepository = LogManager.getLoggerRepository();
            RepositorySelector theSelector = new Log4jRepositorySelector();
            LogManager.setRepositorySelector(theSelector, guard);
            initialized = true;
        }

        Hierarchy hierarchy = new Hierarchy(new RootLogger(Level.DEBUG));
        loadLog4JConfig(log4jConfigurationPath, servletContext, hierarchy);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        repositories.put(loader, hierarchy);
    }

    public static synchronized void removeFromRepository() {
        repositories.remove(Thread.currentThread().getContextClassLoader());
    }

    // load log4j.xml from WEB-INF
    private static void loadLog4JConfig(String log4FilePath, ServletContext context,
                                        Hierarchy hierarchy) throws ServletException {
        try {

            File log4jConfig = new File(log4FilePath);
            if (log4jConfig.exists() && log4jConfig.canRead()) {
                if (!log4FilePath.endsWith(".xml")) {
                    PropertyConfigurator propertyConfigurator = new PropertyConfigurator();
                    propertyConfigurator.doConfigure(log4FilePath, hierarchy);
                } else {
                    DOMConfigurator domConfigurator = new DOMConfigurator();
                    domConfigurator.doConfigure(log4FilePath,  hierarchy);
                }
                
            } else {

                String log4jFileName = log4FilePath.substring(log4FilePath.lastIndexOf("/") + 1);


                InputStream log4jConfigIS = getLog4jConfigInputStream(context, log4jFileName);

                if (log4jConfigIS != null) {
                    System.out.println("INFO: Configuring Log4J for module '"+context.getServletContextName()+"' from configuration file '"+log4jFileName+"' loaded from classpath. (To override default config, place a file with the same name in ${jboss.server.config.url})");

                    if (!log4FilePath.endsWith(".xml")) {
                        Properties properties = new Properties();
                        properties.load(log4jConfigIS);
                        PropertyConfigurator propertyConfigurator = new PropertyConfigurator();
                        propertyConfigurator.doConfigure(properties, hierarchy);
                    } else {
                        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(log4jConfigIS);
                        DOMConfigurator conf = new DOMConfigurator();
                        conf.doConfigure(doc.getDocumentElement(), hierarchy);
                    }

                } else {
                    System.err.println("ERROR: Failed loading '"+ log4jFileName +"' from path '"+log4FilePath+"' and from classpath. Log4J will not be configured for servlet module " + context.getServletContextName());
                }


            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private static InputStream getLog4jConfigInputStream(ServletContext context, String log4jFileName) {
        InputStream log4jConfigIS = context.getResourceAsStream("/" + log4jFileName);
        if (log4jConfigIS == null) {
            log4jConfigIS = context.getResourceAsStream("/WEB-INF/classes/" + log4jFileName);
        }
        if (log4jConfigIS == null) {
            log4jConfigIS = context.getClass().getClassLoader().getResourceAsStream(log4jFileName);
        }
        return log4jConfigIS;
    }


    private Log4jRepositorySelector() {
    }

    @Override
    public LoggerRepository getLoggerRepository() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        LoggerRepository repository = (LoggerRepository) repositories.get(loader);
        if (repository == null) {
            return defaultRepository;
        } else {
            return repository;
        }
    }
}
