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

package com.trifork.stamdata.importer.parsers.takst.model;

import com.trifork.stamdata.importer.parsers.takst.FixedLengthParserConfiguration;



public class OplysningerOmDosisdispenseringFactory implements FixedLengthParserConfiguration<OplysningerOmDosisdispensering>
{
	@Override
	public String getFilename()
	{
		return "LMS24";
	}

	@Override
	public int getLength(int fieldNo)
	{
		switch (fieldNo)
		{
		case 0:
			return 11;
		case 1:
			return 6;
		case 2:
			return 4;
		case 3:
			return 9;
		case 4:
			return 9;
		case 5:
			return 9;
		case 6:
			return 1;
		case 7:
			return 11;
		default:
			return -1;
		}
	}

	@Override
	public int getNumberOfFields()
	{
		return 8;
	}

	@Override
	public int getOffset(int fieldNo)
	{
		switch (fieldNo)
		{
		case 0:
			return 0;
		case 1:
			return 11;
		case 2:
			return 17;
		case 3:
			return 21;
		case 4:
			return 30;
		case 5:
			return 39;
		case 6:
			return 48;
		case 7:
			return 49;
		default:
			return -1;
		}
	}

	@Override
	public void setFieldValue(OplysningerOmDosisdispensering obj, int fieldNo, String value)
	{
		switch (fieldNo)
		{
		case 0:
			obj.setDrugid(Long.parseLong(value));
			break;
		case 1:
			obj.setVarenummer(Long.parseLong(value));
			break;
		case 2:
			obj.setLaegemidletsSubstitutionsgruppe(value);
			break;
		case 3:
			obj.setMindsteAIPPrEnhed(Long.parseLong(value));
			break;
		case 4:
			obj.setMindsteRegisterprisEnh(Long.parseLong(value));
			break;
		case 5:
			obj.setTSPPrEnhed(Long.parseLong(value));
			break;
		case 6:
			obj.setKodeForBilligsteDrugid(value);
			break;
		case 7:
			obj.setBilligsteDrugid(Long.parseLong(value));
			break;
		default:
			break;
		}
	}
}
