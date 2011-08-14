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

import java.io.File;
import java.text.SimpleDateFormat;

import org.apache.commons.io.*;
import org.joda.time.Period;
import org.slf4j.*;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.trifork.stamdata.importer.jobs.FileParserJob;
import com.trifork.stamdata.importer.jobs.sks.Institution.InstitutionType;
import com.trifork.stamdata.importer.persistence.*;


/**
 * Parser for the SKS register.
 * 
 * SKS is an acronym for 'Sundhedsvæsenets KlassifikationsSystem'.
 */
public class SKSParser implements FileParserJob
{
	private static final Logger logger = LoggerFactory.getLogger(SKSParser.class);

	private static final int SKS_NUMBER_END_INDEX = 23;
	private static final int SKS_NUMBER_START_INDEX = 3;
	private static final int NAME_END_INDEX = 167;
	private static final int NAME_START_INDEX = 47;
	private static final String DATE_FORMAT = "yyyyMMdd";
	private static final int ENTRY_TYPE_START_INDEX = 0;
	private static final int ENTRY_TYPE_END_INDEX = 3;

	private static final char CREATE_OPERATION_CODE = '1';
	private static final char DELETE_OPERATION_CODE = '2';
	private static final char UPDATE_OPERATION_CODE = '3';

	private static final String HOSPITAL_TYPE = "sgh";
	private static final String HOSPITAL_DEPARTMENT_TYPE = "afd";

	private static final int OPERATION_CODE_INDEX = 187;
	private static final int NEW_FORMAT_LINE_LENGTH = 188;

	private static final String FILE_ENCODING = "ISO8859-15";

	private static final String JOB_IDENTIFIER = "sks_parser";

	private final Period maxTimeGap;

	@Inject
	SKSParser(@Named(JOB_IDENTIFIER + "." + MAX_TIME_GAP) String maxTimeGap)
	{
		this.maxTimeGap = Period.minutes(Integer.parseInt(maxTimeGap));
	}

	@Override
	public String getIdentifier()
	{
		return "sks";
	}

	@Override
	public String getHumanName()
	{
		return "SKS Parser";
	}

	@Override
	public Period getMaxTimeGap()
	{
		return maxTimeGap;
	}

	@Override
	public boolean checkFileSet(File[] input)
	{
		boolean present = false;

		for (File file : input)
		{
			if (file.getName().toUpperCase().endsWith(".TXT"))
			{
				present = true;
			}
		}

		return present;
	}

	@Override
	public void run(File[] files, Persister persister) throws Exception
	{
		for (File file : files)
		{
			if (file.getName().toUpperCase().endsWith(".TXT"))
			{
				Dataset<Institution> dataset = new Dataset<Institution>(Institution.class);

				LineIterator lines = FileUtils.lineIterator(file, FILE_ENCODING);
				while (lines.hasNext())
				{
					String line = lines.nextLine();

					Institution institution = parseLine(line);

					if (institution != null)
					{
						dataset.addEntity(institution);
					}
					else
					{
						logger.debug("Line ignored. line_content={}", line);
					}
				}

				persister.persist(dataset);

				logger.info("SKS file parsed. num_records={}, file={}", dataset.getEntities().size(), file.getAbsolutePath());
			}
			else
			{
				logger.warn("Ignoring file, which doen't match *.TXT. file={}", file.getAbsolutePath());
			}
		}
	}

	public Institution parseLine(String line) throws Exception
	{
		if (line.length() < NEW_FORMAT_LINE_LENGTH)
		{
			logger.debug("Ignoring old format SKS afd line. line_content={}", line);
			return null;
		}

		char code = line.charAt(OPERATION_CODE_INDEX);

		if (code == ' ')
		{
			logger.debug("Operation code cannot be derived from line. Must be an old record. The line is ignored.");
			return null;
		}
		else if (code == DELETE_OPERATION_CODE)
		{
			// TODO: This should be discussed with Lakeside.

			logger.warn("Received an SKS entry with action code (2) (DELETE). Ignoring as PEM does.");
			return null;
		}
		else if (code == CREATE_OPERATION_CODE || code == UPDATE_OPERATION_CODE)
		{
			// Create and Update are handled the same way.

			String entryType = line.substring(ENTRY_TYPE_START_INDEX, ENTRY_TYPE_END_INDEX);

			if (!entryType.equals(HOSPITAL_DEPARTMENT_TYPE) && !entryType.equals(HOSPITAL_TYPE))
			{
				throw new Exception("Received an SKS entry with no 'afd' or 'shg' prefixing SKS. line_content=" + line);
			}

			InstitutionType type = (entryType.equals(HOSPITAL_DEPARTMENT_TYPE)) ? InstitutionType.HOSPITAL_DEPARTMENT : InstitutionType.HOSPITAL;

			Institution institution = new Institution(type);
			institution.setNummer(line.substring(SKS_NUMBER_START_INDEX, SKS_NUMBER_END_INDEX).trim());

			SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

			institution.setValidFrom(dateFormat.parse(line.substring(23, 31)));
			institution.setValidTo(dateFormat.parse(line.substring(39, 47)));

			institution.setNavn(line.substring(NAME_START_INDEX, NAME_END_INDEX).trim());

			return institution;
		}
		else
		{
			throw new Exception("SKS parser encountered an unkown operation code. code=" + code);
		}
	}
}
