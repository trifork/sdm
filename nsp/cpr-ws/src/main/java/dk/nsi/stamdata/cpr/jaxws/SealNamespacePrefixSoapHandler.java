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
package dk.nsi.stamdata.cpr.jaxws;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class SealNamespacePrefixSoapHandler implements SOAPHandler<SOAPMessageContext>
{
	private Map<String, String> prefixMap = SealNamespacePrefixMapper.prefixMap;

	@Override
	public Set<QName> getHeaders()
	{
		return null;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context)
	{
		try
		{
			SOAPHeader soapHeader = context.getMessage().getSOAPHeader();

			for (Map.Entry<String, String> prefixMapEntry : prefixMap.entrySet())
			{
				soapHeader.addNamespaceDeclaration(prefixMapEntry.getValue(), prefixMapEntry.getKey());
			}

			Iterator<?> iterator = soapHeader.examineAllHeaderElements();
			
			while (iterator.hasNext())
			{
				Node element = (Node) iterator.next();
				changePrefix(element.getChildNodes());
			}
		}
		catch (SOAPException e)
		{
			throw new RuntimeException(e);
		}

		return true;
	}

	private void changePrefix(NodeList elementsToProcess)
	{
		for (int i = 0; i < elementsToProcess.getLength(); i++)
		{
			Node node = elementsToProcess.item(i);
			String nodeNS = node.getNamespaceURI();
			if (prefixMap.containsKey(nodeNS))
			{
				node.setPrefix(prefixMap.get(nodeNS));
			}

			changePrefix(node.getChildNodes());
		}
	}

	@Override
	public boolean handleFault(SOAPMessageContext context)
	{
		return true;
	}

	@Override
	public void close(MessageContext context)
	{
	}
}
