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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CprAbbsResponseParser 
{

    public List<String> extractCprNumbers(String soapResponse) throws CprAbbsException
    {
        int start = soapResponse.indexOf("<?xml");
        if(start == -1) {
            throw new CprAbbsException("Invalid message body on call to CPR Abbs");
        }
        String soapResponseWithoutHeader = soapResponse.substring(start);
        return extractCprNumbersWithoutHeaders(soapResponseWithoutHeader);
    }
    
    public List<String> extractCprNumbersWithoutHeaders(String soapResponse) throws CprAbbsException
    {
        try 
        {
            Document document = createDomTree(soapResponse);
            NodeList nodeList = extractChangedCprsNodes(document);
            return convertNodeListToCprStrings(nodeList);
        } 
        catch (Exception e) 
        {
            throw new CprAbbsException(e);
        }
    }

    private Document createDomTree(String soapResponse) throws ParserConfigurationException, SAXException, IOException 
    {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
        InputSource inputSource = new InputSource();
        inputSource.setCharacterStream(new StringReader(soapResponse));
        return documentBuilder.parse(inputSource);
    }
    
    private NodeList extractChangedCprsNodes(Document document) throws XPathExpressionException 
    {
        XPath xpath = XPathFactory.newInstance().newXPath();
        NamespaceContext context = new NamespaceContextMap(
                "soap", "http://schemas.xmlsoap.org/soap/envelope/",
                "cprabbs", "http://nsi.dk/cprabbs/2011/10");
        xpath.setNamespaceContext(context);
        XPathExpression pathExpression = xpath.compile("//soap:Envelope/soap:Body/cprabbs:CprAbbsResponse/cprabbs:changedCprs");
        return (NodeList) pathExpression.evaluate(document, XPathConstants.NODESET);
    }

    private List<String> convertNodeListToCprStrings(NodeList nodeList) 
    {
        List<String> result = new ArrayList<String>();
        for(int i = 0; i < nodeList.getLength(); i++)
        {
            Node node = nodeList.item(i);
            result.add(node.getTextContent());
        }
        return result;
    }
}
