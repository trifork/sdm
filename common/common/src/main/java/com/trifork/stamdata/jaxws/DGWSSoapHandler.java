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
package com.trifork.stamdata.jaxws;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dk.sosi.seal.model.constants.FlowStatusValues;

public class DGWSSoapHandler implements SOAPHandler<SOAPMessageContext> {

    @Override
    public boolean handleMessage(SOAPMessageContext ctx) {
        try {
            Boolean isOutbound = (Boolean)ctx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            
            if(isOutbound.booleanValue()) {
                SOAPHeader soapHeader = ctx.getMessage().getSOAPHeader();

                NodeList nodeList = soapHeader.getElementsByTagNameNS("http://www.medcom.dk/dgws/2006/04/dgws-1.0.xsd", "Header");
                Node medcomHeader = nodeList.item(0);

                Element flowStatus = medcomHeader.getOwnerDocument().createElementNS("http://www.medcom.dk/dgws/2006/04/dgws-1.0.xsd", "FlowStatus");
                flowStatus.setTextContent(FlowStatusValues.FLOW_FINALIZED_SUCCESFULLY);
                
                NodeList linking = soapHeader.getElementsByTagNameNS("http://www.medcom.dk/dgws/2006/04/dgws-1.0.xsd", "Linking");
                Node nextSibling = ((Element)linking.item(0)).getNextSibling();
                if(nextSibling == null) {
                    medcomHeader.appendChild(flowStatus);
                } else {
                    medcomHeader.insertBefore(flowStatus, nextSibling);
                }
            }
            
        } catch (SOAPException e) {
            return false;
        }
        return true;
    }
    
    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    @Override
    public void close(MessageContext ctx) {
    }

    @Override
    public boolean handleFault(SOAPMessageContext ctx) {
        return true;
    }
    
}
