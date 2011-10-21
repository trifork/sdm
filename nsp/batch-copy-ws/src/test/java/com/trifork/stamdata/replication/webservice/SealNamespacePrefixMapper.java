package com.trifork.stamdata.replication.webservice;

import java.util.HashMap;
import java.util.Map;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

/**
 * This class is used for deserializing DGWS headers. DGWS require that the
 * namespaces saml and ds be present.
 * 
 */
public class SealNamespacePrefixMapper extends NamespacePrefixMapper
{
	static final Map<String, String> PREFIX_MAP = new HashMap<String, String>();

	static
	{
		// Essential for seal validation.
		
        PREFIX_MAP.put("urn:oasis:names:tc:SAML:2.0:assertion", "saml");
		PREFIX_MAP.put("http://www.w3.org/2000/09/xmldsig#", "ds");

		// Non essential but makes the output pretty.
		
		PREFIX_MAP.put("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse");
		PREFIX_MAP.put("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsu");
		PREFIX_MAP.put("http://www.medcom.dk/dgws/2006/04/dgws-1.0.xsd", "medcom");
	}

	@Override
	public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requiredPrefix)
	{
		if (PREFIX_MAP.containsKey(namespaceUri))
		{
			return PREFIX_MAP.get(namespaceUri);
		}
		
		return suggestion;
	}
}
