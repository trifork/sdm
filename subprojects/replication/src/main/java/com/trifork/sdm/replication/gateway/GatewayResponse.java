package com.trifork.sdm.replication.gateway;


import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;


public class GatewayResponse
{
	protected String url;


	public GatewayResponse(String url)
	{
		this.url = url;
	}


	public String getUrl()
	{
		return url;
	}


	public Element serialize() throws IOException
	{
		Element root;

		try
		{
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			root = doc.createElement("GatewayResponseStructure");
			doc.appendChild(root);

			Element url = doc.createElement("url");
			url.setTextContent(this.url);
			root.appendChild(url);
		}
		catch (Exception e)
		{
			throw new IOException(e);
		}

		return root;
	}


	public static GatewayResponse deserialize(Element xml)
	{
		GatewayResponse response = null;

		for (Node node = xml.getFirstChild(); node != null; node = node.getNextSibling())
		{
			String elementName = node.getNodeName();

			if ("url".equals(elementName))
			{
				response = new GatewayResponse(node.getTextContent());
			}
		}

		return response;
	}
}
