package com.trifork.sdm.replication.gateway;


import javax.xml.parsers.*;

import org.w3c.dom.*;

import com.trifork.stamdata.EntityHelper;


public class GatewayRequest
{
	private static final String FORMAT = "format";
	private static final String PAGE_SIZE = "pageSize";
	private static final String HISTORY_ID = "offset";
	private static final String VERSION = "version";
	private static final String ENTITY = "entity";

	public Integer version;
	public String entity;
	public String historyId;
	public Integer pageSize;
	public String format;


	public Class<?> getEntityType()
	{
		return EntityHelper.getEntityByName(entity);
	}


	public Element serialize() throws ParserConfigurationException
	{
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.newDocument();

		Element root = doc.createElement("GatewayRequestStructure");
		doc.appendChild(root);

		Element entity = doc.createElement(ENTITY);
		entity.setTextContent(this.entity);
		root.appendChild(entity);

		Element version = doc.createElement(VERSION);
		version.setTextContent(Integer.toString(this.version));
		root.appendChild(version);

		if (historyId != null)
		{
			Element historyId = doc.createElement(HISTORY_ID);
			historyId.setTextContent(this.historyId);
			root.appendChild(historyId);
		}

		if (pageSize != null)
		{
			Element pageSize = doc.createElement(PAGE_SIZE);
			pageSize.setTextContent(Integer.toString(this.pageSize));
			root.appendChild(pageSize);
		}

		if (format != null)
		{
			Element format = doc.createElement(FORMAT);
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

			if (ENTITY.equals(elementName))
			{
				request.entity = node.getTextContent();
			}
			else if (VERSION.equals(elementName))
			{
				request.version = Integer.parseInt(node.getTextContent());
			}
			else if (HISTORY_ID.equals(elementName))
			{
				request.historyId = node.getTextContent();
			}
			else if (PAGE_SIZE.equals(elementName))
			{
				request.pageSize = Integer.parseInt(node.getTextContent());
			}
			else if (FORMAT.equals(elementName))
			{
				request.format = node.getTextContent();
			}
		}

		return request;
	}
}
