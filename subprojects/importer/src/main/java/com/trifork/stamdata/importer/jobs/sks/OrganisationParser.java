package com.trifork.stamdata.importer.jobs.sks;


import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.jobs.FileParseException;
import com.trifork.stamdata.registre.sks.Organisation;


public class OrganisationParser
{
	private static final Logger logger = LoggerFactory.getLogger(OrganisationParser.class);

	private String line;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");


	public OrganisationParser(String readLine)
	{

		this.line = readLine;
	}


	public Organisation getOrganisation()
	{

		// TODO: Magic numbers.

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

				Organisation organization = null;

				String type = line.substring(0, 3);

				if (type.equals("afd") || type.equals("shg") || type.equals("sgh"))
				{
					organization = new Organisation((type.equals("afd")) ? Organisation.OrgatizationType.Department
							: Organisation.OrgatizationType.Hospital);

					organization.setNummer(line.substring(3, 23).trim());
					organization.setValidFrom(sdf.parse(line.substring(23, 31)));
					organization.setValidTo(sdf.parse(line.substring(39, 47)));
					organization.setNavn(line.substring(47, 167).trim());
				}
				else
				{
					logger.warn("Received an SKS entry with no 'afd' or 'shg' prefixing SKS. Ignoring line:" + line);
				}

				return organization;
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
