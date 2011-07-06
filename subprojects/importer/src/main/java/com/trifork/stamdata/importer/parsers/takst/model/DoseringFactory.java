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


public class DoseringFactory implements FixedLengthParserConfiguration<Dosering>
{
	@Override
	public String getFilename()
	{
		return "LMS28";
	}

	@Override
	public int getLength(int fieldNo)
	{
		switch (fieldNo)
		{
		case 0:
			return 7;
		case 1:
			return 10;
		case 2:
			return 78;
		case 3:
			return 9;
		case 4:
			return 26;
		case 5:
			return 26;
		case 6:
			return 26;
		case 7:
			return 1;
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
			return 7;
		case 2:
			return 17;
		case 3:
			return 95;
		case 4:
			return 104;
		case 5:
			return 130;
		case 6:
			return 156;
		case 7:
			return 182;
		default:
			return -1;
		}
	}

	@Override
	public void setFieldValue(Dosering obj, int fieldNo, String value)
	{
		switch (fieldNo)
		{
		case 0:
			obj.setDoseringskode(Long.parseLong(value));
			break;
		case 1:
			obj.setDoseringKortTekst(value);
			break;
		case 2:
			obj.setDoseringstekstTotal(value);
			break;
		case 3:
			obj.setAntalEnhDoegn(Long.parseLong(value));
			break;
		case 4:
			obj.setDoseringstekstLinie1(value);
			break;
		case 5:
			obj.setDoseringstekstLinie2(value);
			break;
		case 6:
			obj.setDoseringstekstLinie3(value);
			break;
		case 7:
			obj.setAktivInaktiv(value);
			break;
		}
	}
}
