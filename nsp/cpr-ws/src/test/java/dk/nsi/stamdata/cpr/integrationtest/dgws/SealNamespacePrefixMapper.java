/**
 * The MIT License
 *
 * Original work sponsored and donated by National Board of e-Health (NSI), Denmark
 * (http://www.nsi.dk)
 *
 * Copyright (C) 2011 National Board of e-Health (NSI), Denmark (http://www.nsi.dk)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dk.nsi.stamdata.cpr.integrationtest.dgws;

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
	public static Map<String, String> prefixMap = new HashMap<String, String>();

	static
	{
		// Essential for seal validation.
		
		prefixMap.put("urn:oasis:names:tc:SAML:2.0:assertion", "saml");
		prefixMap.put("http://www.w3.org/2000/09/xmldsig#", "ds");

		// Non essential but makes the output pretty.
		
		prefixMap.put("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse");
		prefixMap.put("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsu");
		prefixMap.put("http://www.medcom.dk/dgws/2006/04/dgws-1.0.xsd", "medcom");
	}

	@Override
	public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requiredPrefix)
	{
		if (prefixMap.containsKey(namespaceUri))
		{
			return prefixMap.get(namespaceUri);
		}
		
		return suggestion;
	}
}
