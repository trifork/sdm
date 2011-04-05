// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package com.trifork.stamdata.client;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Node;

// TODO: This class servers no special purpose. Move the functions.

class SoapHelper {

	public static String send(String urlString, Node node) throws Exception {

		URL url = new URL(urlString);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// Prepare for SOAP

		connection.setRequestMethod("POST");
		connection.setRequestProperty("SOAPAction", "\"\"");
		connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8;");

		// Send the request XML.

		connection.setDoOutput(true);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new DOMSource(node), new StreamResult(connection.getOutputStream()));

		// Read the response.

		InputStream inputStream;

		if (connection.getResponseCode() < 400) {
			inputStream = connection.getInputStream();
		}
		else {
			inputStream = connection.getErrorStream();
		}

		String response = IOUtils.toString(inputStream);

		return response;
	}
}
