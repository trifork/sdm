package dk.nsi.stamdata.cpr.integrationtest.dgws;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


class SealNamespacePrefixSoapHandler implements SOAPHandler<SOAPMessageContext>
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
