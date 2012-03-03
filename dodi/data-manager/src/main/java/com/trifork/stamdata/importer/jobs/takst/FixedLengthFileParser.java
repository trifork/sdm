/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */


package com.trifork.stamdata.importer.jobs.takst;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import org.apache.log4j.Logger;


public class FixedLengthFileParser
{
	public static final String FILE_ENCODING = "CP865";

	protected static final Logger logger = Logger.getLogger(FixedLengthFileParser.class);

	private final File[] input;

	public FixedLengthFileParser(File[] input)
	{
		this.input = input;
	}

	public <T extends TakstEntity> List<T> parse(FixedLengthParserConfiguration<T> configuration, Class<T> type) throws Exception
	{
		logger.debug("Parsing file=" + configuration.getFilename());
		
		File file = TakstParser.getFileByName(configuration.getFilename(), input);
		LineIterator lines = FileUtils.lineIterator(file, FILE_ENCODING);

		List<T> results = Lists.newArrayList();

		int count = 0;
		
		while (lines.hasNext())
		{
		    count++;
		    
			String line = lines.nextLine();

			if (line.trim().length() == 0)
			{
				continue;
			}

			T entity;

			try
			{
				entity = type.newInstance();
			}
			catch (Exception e)
			{
				throw new RuntimeException("Entities should have excatly one zero argument constructor.", e);
			}

			for (int fieldNo = 0; fieldNo < configuration.getNumberOfFields(); fieldNo++)
			{
				if (configuration.getLength(fieldNo) > 0)
				{
					int offset = configuration.getOffset(fieldNo);
					int length = configuration.getLength(fieldNo);

					String value = line.substring(offset, offset + length);
					value = StringUtils.trimToNull(value);
					
					configuration.setFieldValue(entity, fieldNo, value);
				}
			}

			results.add(entity);
		}
		
		logger.debug("Number of lines in file. lineCount="+count+" file="+configuration.getFilename()+" resultCount=" + results.size());

		return results;
	}
}
