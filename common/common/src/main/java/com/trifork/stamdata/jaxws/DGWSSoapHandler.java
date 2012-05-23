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
