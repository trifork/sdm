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

package com.trifork.stamdata.importer.parsers.sks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.parsers.exceptions.FileParseException;
import com.trifork.stamdata.importer.parsers.sks.model.Organisation;
import com.trifork.stamdata.importer.parsers.sks.model.Organisation.Organisationstype;
import com.trifork.stamdata.importer.util.DateUtils;

import java.text.SimpleDateFormat;


public class OrganisationParser
{
	private Logger logger = LoggerFactory.getLogger(getClass());

	private String line;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public OrganisationParser(String readLine)
	{
		this.line = readLine;
	}

	public Organisation getOrganisation()
	{
		try
		{
			if (line.length() < 188)
			{
				logger.warn("Ignoring old format SKS afd line. Length: " + line.length() + " < 188. Line: " + line);
				return null;
			}
			char action = line.charAt(187);
			if (action == ' ')
			{
				logger.warn("Action/operationskode cannot be derived from line - This must be an old record -> Ignoring");
				return null;
			}
			else if (action == '2')
			{
				logger.warn("Received an SKS entry with operationskode = 2 (delete). Ignoring as PEM does.");
				return null;
			}
			else if (action == '1' || action == '3')
			{
				// logger.debug("Action is 1: create or 3: update. Handled the same way.");
				Organisation org = null;
				String type = line.substring(0, 3);

				if (type.equals("afd") || type.equals("shg") || type.equals("sgh"))
				{
					org = new Organisation((type.equals("afd")) ? Organisationstype.Afdeling : Organisationstype.Sygehus);
					org.setNummer(line.substring(3, 23).trim());
					// logger.debug("nummer: " + afd.getNummer());
					org.setValidFrom(DateUtils.toCalendar(sdf.parse(line.substring(23, 31))));
					org.setValidTo(DateUtils.toCalendar(sdf.parse(line.substring(39, 47))));
					org.setNavn(line.substring(47, 167).trim());
				}
				else
				{
					logger.warn("Received an SKS entry with no 'afd' or 'shg' prefixing SKS. Ignoring line:" + line);
				}

				return org;
			}
			else
			{
				String message = "Unkown operation code: " + action;
				throw new FileParseException(message);
			}
		}
		catch (Exception e)
		{
			logger.error("Caught exception while parsing afd line: '" + line + "'", e);
			return null;
		}
	}
}
