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
package com.trifork.stamdata.sla;

import dk.sdsd.nsp.slalog.api.SLALogConfig;
import dk.sdsd.nsp.slalog.api.SLALogConfigException;
import dk.sdsd.nsp.slalog.api.SLALogItem;
import dk.sdsd.nsp.slalog.ws.SLALoggingServletFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public class SDMSLALoggingFilter extends SLALoggingServletFilter {
    private static final Logger log = Logger.getLogger(SDMSLALoggingFilter.class);
    private SLALogConfig slaLogInstance;
    private String shortAppName;

    public void init(FilterConfig filterConfig) throws ServletException {
        String appName = filterConfig.getInitParameter("appName");
        shortAppName = filterConfig.getInitParameter("shortAppName");

        try {
            slaLogInstance = new SLALogConfig(appName, shortAppName);
        } catch (SLALogConfigException e) {
            log.error("Could not configure SLA logging for application " + appName, e);
        }
    }

    protected SLALogItem createLogItem(HttpServletRequest servletRequest) {
        String soapActionHeader = servletRequest.getHeader("SOAPAction");
        String soapAction = (soapActionHeader != null) ? StringUtils.strip(soapActionHeader, "\"") : "";
        String trimmedRequestURI = StringUtils.strip(servletRequest.getRequestURI(), "/");
        String[] uriParts = StringUtils.split(trimmedRequestURI, "/");
        String operation = uriParts[uriParts.length-1];
        String logPoint = shortAppName + "." + operation;

        SLALogItem logItem = null;
        if (slaLogInstance != null) {
            try {
                logItem = slaLogInstance.getSLALogger().createLogItem(logPoint, shortAppName + "." + operation);
                logItem.setClientIP(servletRequest);
                logItem.setSourceSOAPAction(soapAction);
                logItem.setSourceOperation(operation);
                logItem.setSourceEndpoint(servletRequest.getRequestURL().toString());
            } catch (Exception e) {
                log.error("Could not instantiate SLA log item", e);
            }
        } // error situation causing slaLogInstance to be null has already been error-logged in init, so just ignore silently
        return logItem;
    }

}
