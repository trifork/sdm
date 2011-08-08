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

package com.trifork.stamdata.importer.jobs.sks;

import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.jobs.sks.Organisation.Organisationstype;


public class OrganisationParser
{
	private static final Logger logger = LoggerFactory.getLogger(OrganisationParser.class);
	
	private static final char CREATE_OPERATION_CODE = '1';
	private static final char DELETE_OPERATION_CODE = '2';
	private static final char UPDATE_OPERATION_CODE = '3';
	
	private String line;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public OrganisationParser(String readLine)
	{
		this.line = readLine;
	}

	public Organisation getOrganisation() throws Exception
	{
		if (line.length() < 188)
		{
			if (logger.isDebugEnabled()) logger.debug("Ignoring old format SKS afd line. Length: " + line.length() + " < 188. Line: " + line);
			return null;
		}
		
		char action = line.charAt(187);
		
		if (action == ' ')
		{
			logger.debug("Operation code cannot be derived from line. Must be an old record. The line is ignored.");
			return null;
		}
		else if (action == DELETE_OPERATION_CODE)
		{
			logger.warn("Received an SKS entry with operationskode = 2 (delete). Ignoring as PEM does.");
			return null;
		}
		else if (action == CREATE_OPERATION_CODE || action == UPDATE_OPERATION_CODE)
		{
			// Action 1: create or 3: update. Handled the same way.");
			Organisation org = null;
			String type = line.substring(0, 3);

			if (type.equals("afd") || type.equals("shg") || type.equals("sgh"))
			{
				org = new Organisation((type.equals("afd")) ? Organisationstype.Afdeling : Organisationstype.Sygehus);
				org.setNummer(line.substring(3, 23).trim());
				
				org.setValidFrom(sdf.parse(line.substring(23, 31)));
				org.setValidTo(sdf.parse(line.substring(39, 47)));
				
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
			throw new Exception("Unkown operation code: " + action);
		}
	}
}
