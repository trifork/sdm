// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
//
// Contributor(s): Contributors are attributed in the source code
// where applicable.
//
// The Original Code is "Stamdata".
//
// The Initial Developer of the Original Code is Trifork Public A/S.
//
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
//
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.importer.parsers.sor;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.parsers.exceptions.FileParseException;



public class SORParser {

	private static Logger logger = LoggerFactory.getLogger(SORParser.class);

	public static SORDataSets parse(File file) throws FileParseException {

		SORDataSets dataSets = new SORDataSets();
		SOREventHandler handler = new SOREventHandler(dataSets);
		SAXParserFactory factory = SAXParserFactory.newInstance();

		try {
			SAXParser parser = factory.newSAXParser();

			if (file.getName().toUpperCase().endsWith("XML")) {
				parser.parse(file, handler);
			}
			else {
				logger.warn("Can only parse files with extension 'XML'! Ignoring: " + file.getAbsolutePath());
			}
		}
		catch (Exception e) {

			String errorMessage = "Error parsing data from file: " + file.getAbsolutePath();
			logger.error(errorMessage, e);
			throw new FileParseException(errorMessage, e);
		}

		return dataSets;
	}
}
