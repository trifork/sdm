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
package dk.nsi.stamdata.cpr.pvit.proxy;

public class SimpleSoapBuilder 
{
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
    private static final String SOAP_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";
    
    public String createSoapMessage(String header, String body)
    {
        StringBuilder builder = new StringBuilder();
        
        builder.append(XML_HEADER);
        
        builder.append("<soap:Envelope xmlns:soap=\"" + SOAP_NAMESPACE + "\">");
        
        builder.append("<soap:Header>");
        builder.append(header);
        builder.append("</soap:Header>");
        
        builder.append("<soap:Body>");
        builder.append(body);
        builder.append("</soap:Body>");
        
        builder.append("</soap:Envelope>");
        
        return builder.toString();
    }
}
