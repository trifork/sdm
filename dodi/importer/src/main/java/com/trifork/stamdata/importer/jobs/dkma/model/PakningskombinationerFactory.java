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

package com.trifork.stamdata.importer.jobs.dkma.model;

import org.apache.commons.lang.math.NumberUtils;

import com.trifork.stamdata.importer.jobs.dkma.FixedLengthParserConfiguration;



public class PakningskombinationerFactory implements FixedLengthParserConfiguration<Pakningskombination>
{
	@Override
	public String getFilename()
	{
		return "lms32.txt";
	}

	@Override
	public int getLength(int fieldNo)
	{
		switch (fieldNo)
		{
		case 0:
			return 6;
		case 1:
			return 6;
		case 2:
			return 6;
		case 3:
			return 2;
		case 4:
			return 9;
		case 5:
			return 1;
		default:
			return -1;
		}
	}

	@Override
	public int getNumberOfFields()
	{
		return 6;
	}

	@Override
	public int getOffset(int fieldNo)
	{
		switch (fieldNo)
		{
		case 0:
			return 0;
		case 1:
			return 6;
		case 2:
			return 12;
		case 3:
			return 18;
		case 4:
			return 20;
		case 5:
			return 29;
		default:
			return -1;
		}
	}

	@Override
	public void setFieldValue(Pakningskombination obj, int fieldNo, String value)
	{
		switch (fieldNo)
		{
		case 0:
			obj.setVarenummerOrdineret(NumberUtils.createLong(value));
			break;
		case 1:
			obj.setVarenummerSubstitueret(NumberUtils.createLong(value));
			break;
		case 2:
			obj.setVarenummerAlternativt(NumberUtils.createLong(value));
			break;
		case 3:
			obj.setAntalPakninger(NumberUtils.createLong(value));
			break;
		case 4:
			obj.setEkspeditionensSamledePris(NumberUtils.createLong(value));
			break;
		case 5:
			obj.setInformationspligtMarkering(value);
			break;
		default:
			break;
		}
	}
}
