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

package com.trifork.stamdata.importer.parsers.takst;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;


public class FixedLengthFileParser
{
	private static final String FILE_ENCODING = "CP865";

	protected static final Logger logger = LoggerFactory.getLogger(FixedLengthFileParser.class);

	private final File[] input;

	public FixedLengthFileParser(File[] input)
	{
		this.input = input;
	}

	public <T extends TakstEntity> List<T> parse(FixedLengthParserConfiguration<T> configuration, Class<T> type) throws IOException
	{
		File file = TakstParser.getFileByName(configuration.getFilename() + ".txt", input);
		LineIterator lines = FileUtils.lineIterator(file, FILE_ENCODING);

		List<T> results = Lists.newArrayList();

		while (lines.hasNext())
		{
			String line = lines.nextLine();

			if (line.trim().length() == 0)
			{
				// TODO: Does this ever happen?
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

					String value = line.substring(offset, offset + length).trim();

					if (!"".equals(value))
					{
						configuration.setFieldValue(entity, fieldNo, value);
					}
				}
			}

			results.add(entity);
		}

		return results;
	}
}
