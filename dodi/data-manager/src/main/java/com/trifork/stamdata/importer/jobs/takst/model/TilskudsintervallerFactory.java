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


package com.trifork.stamdata.importer.jobs.takst.model;

import org.apache.commons.lang.math.NumberUtils;

import com.trifork.stamdata.importer.jobs.takst.FixedLengthParserConfiguration;



public class TilskudsintervallerFactory implements FixedLengthParserConfiguration<Tilskudsintervaller>
{
	@Override
	public String getFilename()
	{
		return "lms23.txt";
	}

	@Override
	public int getLength(int fieldNo)
	{
		switch (fieldNo)
		{
		case 0:
			return 2;
		case 1:
			return 1;
		case 2:
			return 8;
		case 3:
			return 8;
		case 4:
			return 5;
		default:
			return -1;
		}
	}

	@Override
	public int getNumberOfFields()
	{
		return 5;
	}

	@Override
	public int getOffset(int fieldNo)
	{
		switch (fieldNo)
		{
		case 0:
			return 0;
		case 1:
			return 2;
		case 2:
			return 3;
		case 3:
			return 11;
		case 4:
			return 19;
		default:
			return -1;
		}
	}

	@Override
	public void setFieldValue(Tilskudsintervaller obj, int fieldNo, String value)
	{
		switch (fieldNo)
		{
		case 0:
			obj.setType(NumberUtils.createLong(value));
			break;
		case 1:
			obj.setNiveau(NumberUtils.createLong(value));
			break;
		case 2:
			obj.setNedreGraense(NumberUtils.createLong(value));
			break;
		case 3:
			obj.setOevreGraense(NumberUtils.createLong(value));
			break;
		case 4:
			obj.setProcent(Double.parseDouble(value));
			break;
		}
	}
}
