package com.trifork.sdm.replication.gateway;


import javax.xml.parsers.*;

import org.w3c.dom.*;

import com.trifork.stamdata.EntityHelper;


public class GatewayRequest
{
	public Integer version = null;
	public String resource = null;
	public String since = null;
	public Integer rows = null;
	public String format = null;


	public Class<?> getResourceType()
	{
		return EntityHelper.getResourceByName(resource);
	}


	public Element serialize() throws ParserConfigurationException
	{
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.newDocument();

		Element root = doc.createElement("GatewayRequestStructure");
		doc.appendChild(root);

		Element resource = doc.createElement("resource");
		resource.setTextContent(this.resource);
		root.appendChild(resource);

		Element version = doc.createElement("version");
		version.setTextContent(Integer.toString(this.version));
		root.appendChild(version);

		if (this.since != null)
		{
			Element offset = doc.createElement("offset");
			offset.setTextContent(this.since);
			root.appendChild(offset);
		}

		if (this.rows != -1)
		{
			Element rows = doc.createElement("rows");
			rows.setTextContent(Integer.toString(this.rows));
			root.appendChild(rows);
		}

		if (this.format != null)
		{
			Element format = doc.createElement("format");
			format.setTextContent(this.format);
			root.appendChild(format);
		}

		return root;
	}


	public static GatewayRequest deserialize(Element xml)
	{

		GatewayRequest request = new GatewayRequest();

		for (Node node = xml.getFirstChild(); node != null; node = node.getNextSibling())
		{

			String elementName = node.getNodeName();

			if ("resource".equals(elementName))
			{
				request.resource = node.getTextContent();
			}
			else if ("version".equals(elementName))
			{
				request.version = Integer.parseInt(node.getTextContent());
			}
			else if ("offset".equals(elementName))
			{
				request.since = node.getTextContent();
			}
			else if ("rows".equals(elementName))
			{
				request.rows = Integer.parseInt(node.getTextContent());
			}
			else if ("format".equals(elementName))
			{
				request.format = node.getTextContent();
			}
		}

		return request;
	}
}
